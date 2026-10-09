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

import com.mobiles.mobil.model.Dto.InventarioDTO;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.service.impl.InventarioServiceImpl;
import com.mobiles.mobil.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/inventarios")
public class InventarioController {
    
    private final InventarioServiceImpl inventarioService;
    private final AuthService authService;

    public InventarioController(InventarioServiceImpl inventarioService, AuthService authService) {
        this.inventarioService = inventarioService;
        this.authService = authService;
    }

    // CREATE - Solo EMPRESA (gestión de precio y stock)
    @PostMapping
    public ResponseEntity<InventarioDTO> create(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody InventarioDTO inventarioDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        InventarioDTO created = inventarioService.create(inventarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL - Ambos roles (catálogo de precios para CLIENTE, gestión para EMPRESA)
    @GetMapping
    public ResponseEntity<List<InventarioDTO>> findAll(
            @RequestHeader("X-Session-Id") String sessionId) throws ServiceException {
        
        authService.validar(sessionId); // Solo valida sesión activa
        List<InventarioDTO> inventarios = inventarioService.findAll();
        return ResponseEntity.ok(inventarios);
    }

    // READ BY ID - Ambos roles
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> findById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        authService.validar(sessionId); // Solo valida sesión activa
        InventarioDTO inventario = inventarioService.findById(id);
        return ResponseEntity.ok(inventario);
    }

    // UPDATE - Solo EMPRESA (actualizar precio/stock)
    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> update(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id,
            @RequestBody InventarioDTO inventarioDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        InventarioDTO updated = inventarioService.update(id, inventarioDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Solo EMPRESA + validar dependencias
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        inventarioService.deleteById(id); // Aquí se validan todas las reglas de negocio
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/libro/{libroId}")
    public ResponseEntity<InventarioDTO> findByLibroId(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long libroId) throws ServiceException {
        
        authService.validar(sessionId); // Ambos roles pueden acceder
        InventarioDTO inventario = inventarioService.findByLibroId(libroId);
        return ResponseEntity.ok(inventario);
    }
}
