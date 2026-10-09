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

import com.mobiles.mobil.model.Dto.GeneroDTO;
import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.service.impl.GeneroServiceImpl;
import com.mobiles.mobil.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/generos")
public class GeneroController {
    
    private final GeneroServiceImpl generoService;
    private final AuthService authService;

    public GeneroController(GeneroServiceImpl generoService, AuthService authService) {
        this.generoService = generoService;
        this.authService = authService;
    }

    // CREATE - Solo EMPRESA
    @PostMapping
    public ResponseEntity<GeneroDTO> create(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody GeneroDTO generoDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        GeneroDTO created = generoService.create(generoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL - Ambos roles
    @GetMapping
    public ResponseEntity<List<GeneroDTO>> findAll(
            @RequestHeader("X-Session-Id") String sessionId) throws ServiceException {
        
        authService.validar(sessionId); // Solo valida sesión activa
        List<GeneroDTO> generos = generoService.findAll();
        return ResponseEntity.ok(generos);
    }

    // READ BY ID - Ambos roles
    @GetMapping("/{id}")
    public ResponseEntity<GeneroDTO> findById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        authService.validar(sessionId); // Solo valida sesión activa
        GeneroDTO genero = generoService.findById(id);
        return ResponseEntity.ok(genero);
    }

    // UPDATE - Solo EMPRESA
    @PutMapping("/{id}")
    public ResponseEntity<GeneroDTO> update(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id,
            @RequestBody GeneroDTO generoDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        GeneroDTO updated = generoService.update(id, generoDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Solo EMPRESA + validar relaciones
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        generoService.deleteById(id); // Aquí se valida la regla de negocio
        return ResponseEntity.noContent().build();
    }

    // ENDPOINT NUEVO PARA APP MÓVIL: Listar libros por género
    @GetMapping("/{id}/libros")
    public ResponseEntity<List<LibroDTO>> findLibrosByGenero(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        authService.validar(sessionId); // Ambos roles pueden acceder
        List<LibroDTO> libros = generoService.findLibrosByGeneroId(id);
        return ResponseEntity.ok(libros);
    }
}