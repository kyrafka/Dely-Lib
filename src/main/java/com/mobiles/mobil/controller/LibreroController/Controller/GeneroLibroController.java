package com.mobiles.mobil.controller.LibreroController.Controller;

import java.util.List;

import org.hibernate.service.spi.ServiceException;
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
import org.springframework.web.bind.annotation.RestController;

import com.mobiles.mobil.model.Dto.GeneroLibroDTO;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.service.impl.GeneroLibroServiceImpl;
import com.mobiles.mobil.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/genero-libros")
public class GeneroLibroController {
    
    private final GeneroLibroServiceImpl generoLibroService;
    private final AuthService authService;

    public GeneroLibroController(GeneroLibroServiceImpl generoLibroService, AuthService authService) {
        this.generoLibroService = generoLibroService;
        this.authService = authService;
    }

    // CREATE - Solo EMPRESA (asignar género a libro)
    @PostMapping
    public ResponseEntity<GeneroLibroDTO> create(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody GeneroLibroDTO generoLibroDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        GeneroLibroDTO created = generoLibroService.create(generoLibroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL - Ambos roles (ver todas las relaciones)
    @GetMapping
    public ResponseEntity<List<GeneroLibroDTO>> findAll(
            @RequestHeader("X-Session-Id") String sessionId) throws ServiceException {
        
        authService.validar(sessionId); // Solo valida sesión activa
        List<GeneroLibroDTO> relaciones = generoLibroService.findAll();
        return ResponseEntity.ok(relaciones);
    }

    // READ BY ID - Ambos roles
    @GetMapping("/{id}")
    public ResponseEntity<GeneroLibroDTO> findById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        authService.validar(sessionId); // Solo valida sesión activa
        GeneroLibroDTO relacion = generoLibroService.findById(id);
        return ResponseEntity.ok(relacion);
    }

    // UPDATE - Solo EMPRESA (cambiar estado de la relación)
    @PutMapping("/{id}")
    public ResponseEntity<GeneroLibroDTO> update(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id,
            @RequestBody GeneroLibroDTO generoLibroDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        GeneroLibroDTO updated = generoLibroService.update(id, generoLibroDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Solo EMPRESA (remover género de libro)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        generoLibroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/libro/{libroId}")
    public ResponseEntity<List<GeneroLibroDTO>> findGenerosByLibroId(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long libroId) throws ServiceException {
        
        authService.validar(sessionId); // Ambos roles pueden acceder
        List<GeneroLibroDTO> generos = generoLibroService.findGenerosByLibroId(libroId);
        return ResponseEntity.ok(generos);
    }
}
