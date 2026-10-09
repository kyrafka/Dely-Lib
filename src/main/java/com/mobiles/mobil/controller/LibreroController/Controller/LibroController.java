package com.mobiles.mobil.controller.LibreroController.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.hibernate.service.spi.ServiceException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.controller.LoginController.LoginHeaders;
import com.mobiles.mobil.service.service.AuthService;
import com.mobiles.mobil.service.service.LibroService;

@RestController
@RequestMapping("/api/v1/libros")
public class LibroController {

    private final LibroService libroService;
    private final AuthService authService;
    private final LibroRepository libroRepository;

    public LibroController(LibroService libroService, AuthService authService, LibroRepository libroRepository) {
        this.libroService = libroService;
        this.authService = authService;
        this.libroRepository = libroRepository;
    }

    // CREATE - Solo EMPRESA
    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @RequestBody LibroDTO libroDTO) {
        try {
            Usuario usuario = authService.validar(sessionId);
            if (!"EMPRESA".equals(usuario.getRol())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Acceso denegado: solo usuarios EMPRESA pueden crear libros");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            // Validaciones básicas
            if (libroDTO.getTitulo() == null || libroDTO.getTitulo().trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "El título del libro es obligatorio");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (libroDTO.getNombreCompletoAutor() == null || libroDTO.getNombreCompletoAutor().trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "El nombre del autor es obligatorio");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Inicializar puntuacionPromedio si viene null
            if (libroDTO.getPuntuacionPromedio() == null) {
                libroDTO.setPuntuacionPromedio(0.0);
            }

            LibroDTO created = libroService.create(libroDTO);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Libro creado exitosamente");
            successResponse.put("data", created);

            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);

        } catch (ServiceException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "No se pudo guardar el libro. Revisa los datos e inténtalo de nuevo.");
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "No se pudo autenticar la solicitud.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Ocurrió un error interno. Inténtalo de nuevo.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // READ ALL - Ambos roles
    @GetMapping
    public ResponseEntity<List<LibroDTO>> findAll(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId) throws ServiceException {

        authService.validar(sessionId);
        List<LibroDTO> libros = libroService.findAll();
        return ResponseEntity.ok(libros);
    }

    // READ BY ID - Ambos roles
    @GetMapping("/{id}")
    public ResponseEntity<LibroDTO> findById(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id) throws ServiceException {

        authService.validar(sessionId);
        LibroDTO libro = libroService.findById(id);
        return ResponseEntity.ok(libro);
    }

    // UPDATE - Solo EMPRESA
    @PutMapping("/{id}")
    public ResponseEntity<LibroDTO> update(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id,
            @RequestBody LibroDTO libroDTO) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LibroDTO updated = libroService.update(id, libroDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Solo EMPRESA
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        libroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // SUBIR IMAGEN - Solo EMPRESA
    @PostMapping("/{id}/imagen")
    public ResponseEntity<String> uploadImage(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Usuario usuario = authService.validar(sessionId);
            if (!"EMPRESA".equals(usuario.getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 1) Asegurar carpeta uploads
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 2) Generar nombre único y preservar extensión
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String storedFileName = "libro_" + id + "_" + System.currentTimeMillis() + ext;

            // 3) Guardar archivo
            Path filePath = uploadDir.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4) Actualizar entidad Libro: guardar SOLO el nombre del archivo
            Libro libro = libroRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado con id " + id));
            libro.setImagenPortada(storedFileName);
            libroRepository.save(libro);

            // 5) Construir URL pública desde el contexto actual (sin hardcodear host/puerto)
            String publicUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/libros/")
                    .path(id.toString())
                    .path("/imagen")
                    .toUriString();

            return ResponseEntity.ok("Imagen subida: " + publicUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("No se pudo subir la imagen. Inténtalo de nuevo.");
        }
    }

    // SERVIR IMAGEN - Ambos roles
    @GetMapping("/{id}/imagen")
    public ResponseEntity<ByteArrayResource> getImage(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id) {
        try {
            authService.validar(sessionId);

            Libro libro = libroRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado con id " + id));

            // Ahora imagenPortada guarda el NOMBRE del archivo (no una URL)
            String fileName = libro.getImagenPortada();
            Path filePath = null;

            // Si tiene nombre de archivo, intentar cargarlo
            if (fileName != null && !fileName.isEmpty()) {
                filePath = Paths.get("uploads").resolve(fileName);
            }

            // Si no existe el archivo o no tiene nombre, usar imagen por defecto
            if (filePath == null || !Files.exists(filePath)) {
                // Buscar cualquier imagen disponible como fallback
                Path uploadsDir = Paths.get("uploads");
                if (Files.exists(uploadsDir)) {
                    filePath = Files.list(uploadsDir)
                        .filter(p -> p.toString().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$"))
                        .findFirst()
                        .orElse(null);
                }

                // Si aún no hay imagen, devolver 204 No Conten
                if (filePath == null || !Files.exists(filePath)) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }
            }

            byte[] bytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            MediaType mediaType = (contentType != null)
                    ? MediaType.parseMediaType(contentType)
                    : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(new ByteArrayResource(bytes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
