package com.mobiles.mobil.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobiles.mobil.model.entity.Sesion;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.repository.SesionRepository;
import com.mobiles.mobil.repository.CompraRepository;
import com.mobiles.mobil.repository.UsuarioRepository;
import com.mobiles.mobil.service.service.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Test
    void registerThenLoginStoresHashAndReturnsSession() throws Exception {
        String email = "reader-" + UUID.randomUUID() + "@example.test";
        String password = "Lectura-segura-42";

        String registration = objectMapper.writeValueAsString(new RegistrationPayload(
                "  Ana   Torres ", email, password, "1998-04-13"));
        String idResponse = mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = Long.valueOf(idResponse);
        Usuario registeredUser = usuarioRepository.findById(userId).orElseThrow();
        assertThat(registeredUser.getNombreCompleto()).isEqualTo("Ana Torres");
        assertThat(registeredUser.getEmail()).isEqualTo(email.toLowerCase());
        assertThat(registeredUser.getPassword()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, registeredUser.getPassword())).isTrue();

        String loginBody = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginPayload(email.toUpperCase(), password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.nombre").value("Ana Torres"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String sessionToken = objectMapper.readTree(loginBody).get("sessionId").asText();

        Sesion session = sesionRepository.findAll().stream()
                .filter(item -> item.getUsuario().getIdUsuario().equals(userId))
                .findFirst()
                .orElseThrow();
        assertThat(session.getActivo()).isEqualTo("1");
        assertThat(session.getExpiraEn()).isNotNull();
        assertThat(session.getIdSesion()).isEqualTo(hash(sessionToken));
        assertThat(session.getIdSesion()).isNotEqualTo(sessionToken);
        assertThat(authService.validar(sessionToken).getIdUsuario()).isEqualTo(userId);

        mockMvc.perform(post("/api/v1/logout").header("X-Session-Id", sessionToken))
                .andExpect(status().isNoContent());
        assertThat(sesionRepository.findById(session.getIdSesion()).orElseThrow().getActivo()).isEqualTo("0");
    }

    @Test
    void customerCannotCreatePurchaseLinesDirectly() throws Exception {
        String email = "buyer-" + UUID.randomUUID() + "@example.test";
        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationPayload(
                                "Buyer Example", email, "secure-password-42", "2000-01-01"))))
                .andExpect(status().isCreated());

        String loginBody = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginPayload(email, "secure-password-42"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String sessionToken = objectMapper.readTree(loginBody).get("sessionId").asText();

        mockMvc.perform(post("/api/v1/detalle-compras")
                        .header("X-Session-Id", sessionToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void checkoutIsDisabledByDefault() throws Exception {
        String email = "checkout-" + UUID.randomUUID() + "@example.test";
        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationPayload(
                                "Buyer Example", email, "secure-password-42", "2000-01-01"))))
                .andExpect(status().isCreated());

        String loginBody = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginPayload(email, "secure-password-42"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String sessionToken = objectMapper.readTree(loginBody).get("sessionId").asText();

        mockMvc.perform(post("/api/v1/compras")
                        .header("X-Session-Id", sessionToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("El checkout no está habilitado en este entorno."));
        assertThat(compraRepository.count()).isZero();
    }

    @Test
    void duplicateEmailAndInvalidCredentialsUseClientErrorStatuses() throws Exception {
        String email = "duplicate-" + UUID.randomUUID() + "@example.test";
        String registration = objectMapper.writeValueAsString(new RegistrationPayload(
                "Luz Rojas", email, "Clave-segura-123", "2000-01-01"));

        mockMvc.perform(post("/api/v1/register").contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationPayload(
                                "Luz Rojas", email.toUpperCase(), "Clave-segura-123", "2000-01-01"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ese correo ya tiene una cuenta."));

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginPayload(email, "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Correo o contraseña incorrectos."));
    }

    @Test
    void invalidRegistrationPayloadReturnsReadableValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCompleto\":\"\",\"email\":\"bad-email\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void successfulLegacyLoginUpgradesPlainTextPassword() throws Exception {
        String email = "legacy-" + UUID.randomUUID() + "@example.test";
        String password = "plain-legacy-password";
        Usuario legacyUser = new Usuario();
        legacyUser.setNombreCompleto("Legacy Reader");
        legacyUser.setEmail(email);
        legacyUser.setPassword(password);
        legacyUser.setEstado("ACTIVO");
        legacyUser.setRol("CLIENTE");
        usuarioRepository.saveAndFlush(legacyUser);

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginPayload(email, password))))
                .andExpect(status().isOk());

        Usuario upgradedUser = usuarioRepository.findById(legacyUser.getIdUsuario()).orElseThrow();
        assertThat(upgradedUser.getPassword()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, upgradedUser.getPassword())).isTrue();
    }

    private record RegistrationPayload(
            String nombreCompleto,
            String email,
            String password,
            String fechaNacimiento) {}

    private record LoginPayload(String email, String password) {}

    private String hash(String token) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }
}
