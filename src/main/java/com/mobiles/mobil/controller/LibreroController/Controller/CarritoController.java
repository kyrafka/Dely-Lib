package com.mobiles.mobil.controller.LibreroController.Controller;

import java.util.List;

import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobiles.mobil.model.Dto.CarritoDTO;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.service.impl.CarritoServiceImpl;
import com.mobiles.mobil.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {

    private final CarritoServiceImpl carritoService;
    private final AuthService authService;

    public CarritoController(CarritoServiceImpl carritoService, AuthService authService) {
        this.carritoService = carritoService;
        this.authService = authService;
    }

    // CREATE - Solo CLIENTE puede agregar a su carrito
    @PostMapping
    public ResponseEntity<CarritoDTO> create(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody CarritoDTO carritoDTO) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);

        // Solo clientes pueden tener carrito
        if (!"CLIENTE".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Asegurar que está agregando a SU carrito
        carritoDTO.setIdUsuario(usuario.getIdUsuario());

        CarritoDTO created = carritoService.create(carritoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ - Ver carrito del usuario autenticado (solo CLIENTE)
    @GetMapping
    public ResponseEntity<List<CarritoDTO>> findMyCarrito(
            @RequestHeader("X-Session-Id") String sessionId) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);

        if (!"CLIENTE".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<CarritoDTO> carrito = carritoService.findByUsuarioId(usuario.getIdUsuario());
        return ResponseEntity.ok(carrito);
    }

    // READ - Ver carrito de usuario específico (solo para EMPRESA)
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<CarritoDTO>> findCarritoByUsuario(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long userId) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);

        // Solo empresa puede ver carritos de otros usuarios
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<CarritoDTO> carrito = carritoService.findByUsuarioId(userId);
        return ResponseEntity.ok(carrito);
    }

    // READ BY ID - Ver item específico del carrito
    @GetMapping("/{id}")
    public ResponseEntity<CarritoDTO> findById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);
        CarritoDTO item = carritoService.findById(id);

        // Solo el dueño del carrito o EMPRESA puede ver el item.
        if ("CLIENTE".equals(usuario.getRol())) {
            if (!usuario.getIdUsuario().equals(item.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(item);
    }

    // UPDATE - Solo el dueño puede actualizar su carrito
    @PutMapping("/{id}")
    public ResponseEntity<CarritoDTO> update(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id,
            @RequestBody CarritoDTO carritoDTO) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);

        if (!"CLIENTE".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Verificar que el item pertenece al usuario
        CarritoDTO existing = carritoService.findById(id);
        if (!usuario.getIdUsuario().equals(existing.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Never accept a user identifier from the request body.
        carritoDTO.setIdUsuario(usuario.getIdUsuario());

        CarritoDTO updated = carritoService.update(id, carritoDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Remover item del carrito
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);

        if (!"CLIENTE".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Verificar que el item pertenece al usuario
        CarritoDTO existing = carritoService.findById(id);
        if (!usuario.getIdUsuario().equals(existing.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        carritoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE - Limpiar todo el carrito del usuario
    @DeleteMapping
    public ResponseEntity<Void> clearMyCarrito(
            @RequestHeader("X-Session-Id") String sessionId) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);

        if (!"CLIENTE".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        carritoService.clearCarritoByUsuarioId(usuario.getIdUsuario());
        return ResponseEntity.noContent().build();
    }
}
