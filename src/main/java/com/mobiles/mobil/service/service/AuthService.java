package com.mobiles.mobil.service.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mobiles.mobil.exception.AuthException;
import com.mobiles.mobil.model.Dto.LoginRequest;
import com.mobiles.mobil.model.Dto.LoginResponse;
import com.mobiles.mobil.model.Dto.RegistroClienteRequest;
import com.mobiles.mobil.model.entity.Sesion;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.repository.SesionRepository;
import com.mobiles.mobil.repository.UsuarioRepository;

@Service
public class AuthService {
    private static final int BCRYPT_MAX_BYTES = 72;
    private static final int MAX_SESSION_TOKEN_LENGTH = 128;
    private static final String BCRYPT_HASH_PATTERN = "^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;
    private final PasswordEncoder passwordEncoder;
    private final String dummyPasswordHash;

    public AuthService(
            UsuarioRepository usuarioRepository,
            SesionRepository sesionRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.sesionRepository = sesionRepository;
        this.passwordEncoder = passwordEncoder;
        this.dummyPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    /** Registers a customer and stores only a one-way password hash. */
    @Transactional
    public Long registrarCliente(RegistroClienteRequest request) {
        String email = normalizeEmail(request.getEmail());
        String password = request.getPassword();
        validateBcryptLength(password);
        validateBirthDate(request.getFechaNacimiento());

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw AuthException.conflict("Ese correo ya tiene una cuenta.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(normalizeName(request.getNombreCompleto()));
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol("CLIENTE");
        usuario.setEstado("ACTIVO");
        usuario.setFechaNacimiento(StringUtils.hasText(request.getFechaNacimiento())
                ? request.getFechaNacimiento().trim()
                : null);

        return usuarioRepository.save(usuario).getIdUsuario();
    }

    /** Authenticates a customer and upgrades legacy plain-text passwords on success. */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        String submittedPassword = request.getPassword();
        validateBcryptLength(submittedPassword);
        Optional<Usuario> userResult = usuarioRepository.findByEmailIgnoreCase(email);
        if (userResult.isEmpty()) {
            passwordEncoder.matches(submittedPassword, dummyPasswordHash);
            throw AuthException.unauthorized();
        }
        Usuario usuario = userResult.get();

        if (!"ACTIVO".equalsIgnoreCase(usuario.getEstado())
                || !passwordMatches(usuario.getPassword(), submittedPassword)) {
            throw AuthException.unauthorized();
        }

        if (!isBcryptHash(usuario.getPassword()) || passwordEncoder.upgradeEncoding(usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(submittedPassword));
            usuarioRepository.save(usuario);
        }

        LocalDateTime now = LocalDateTime.now();
        Sesion sesion = new Sesion();
        String sessionToken = newSessionToken();
        sesion.setIdSesion(hashSessionToken(sessionToken));
        sesion.setUsuario(usuario);
        sesion.setCreadoEn(now);
        sesion.setExpiraEn(now.plusDays(7));
        sesion.setActivo("1");
        sesionRepository.save(sesion);

        LoginResponse response = new LoginResponse();
        response.setSessionId(sessionToken);
        response.setUserId(usuario.getIdUsuario());
        response.setNombre(usuario.getNombreCompleto());
        response.setRol(usuario.getRol());
        return response;
    }

    /** Logout is idempotent so a repeated request cannot leave a session active. */
    @Transactional
    public void logout(String sessionId) {
        if (!StringUtils.hasText(sessionId) || sessionId.length() > MAX_SESSION_TOKEN_LENGTH) {
            return;
        }

        String token = sessionId.trim();
        Optional<Sesion> session = sesionRepository.findById(hashSessionToken(token));
        // Permite que las sesiones UUID existentes sigan funcionando mientras expiran.
        if (session.isEmpty()) {
            session = sesionRepository.findById(token);
        }
        session.ifPresent(current -> {
            current.setActivo("0");
            sesionRepository.save(current);
        });
    }

    /** Validates that a session exists, is active, and has not expired. */
    @Transactional(readOnly = true)
    public Usuario validar(String sessionId) {
        if (!StringUtils.hasText(sessionId) || sessionId.length() > MAX_SESSION_TOKEN_LENGTH) {
            throw AuthException.unauthorized();
        }

        String token = sessionId.trim();
        Optional<Sesion> sessionResult = sesionRepository.findByIdSesionAndActivo(hashSessionToken(token), "1");
        // Compatibilidad temporal con sesiones creadas antes del hash en base de datos.
        if (sessionResult.isEmpty()) {
            sessionResult = sesionRepository.findByIdSesionAndActivo(token, "1");
        }
        Sesion session = sessionResult.orElseThrow(AuthException::unauthorized);
        if (session.getExpiraEn() != null && !session.getExpiraEn().isAfter(LocalDateTime.now())) {
            throw AuthException.unauthorized();
        }
        if (session.getUsuario() == null || !"ACTIVO".equalsIgnoreCase(session.getUsuario().getEstado())) {
            throw AuthException.unauthorized();
        }
        return session.getUsuario();
    }

    private boolean passwordMatches(String storedPassword, String submittedPassword) {
        if (!StringUtils.hasText(storedPassword) || submittedPassword == null) {
            return false;
        }
        if (isBcryptHash(storedPassword)) {
            return submittedPassword.getBytes(StandardCharsets.UTF_8).length <= BCRYPT_MAX_BYTES
                    && passwordEncoder.matches(submittedPassword, storedPassword);
        }
        return storedPassword.equals(submittedPassword);
    }

    private String newSessionToken() {
        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hashSessionToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is required by the Java runtime", exception);
        }
    }

    private boolean isBcryptHash(String value) {
        return value != null && value.matches(BCRYPT_HASH_PATTERN);
    }

    private void validateBcryptLength(String password) {
        if (password == null || password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw AuthException.badRequest("La contraseña supera el límite permitido.");
        }
    }

    private void validateBirthDate(String value) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        try {
            if (LocalDate.parse(value.trim()).isAfter(LocalDate.now())) {
                throw AuthException.badRequest("La fecha de nacimiento no puede ser futura.");
            }
        } catch (DateTimeParseException ex) {
            throw AuthException.badRequest("La fecha debe ser una fecha real con formato AAAA-MM-DD.");
        }
    }

    private String normalizeEmail(String value) {
        if (!StringUtils.hasText(value)) {
            throw AuthException.badRequest("Ingresa tu correo electrónico.");
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeName(String value) {
        if (!StringUtils.hasText(value)) {
            throw AuthException.badRequest("Ingresa tu nombre y apellido.");
        }
        return value.trim().replaceAll("\\s+", " ");
    }
}
