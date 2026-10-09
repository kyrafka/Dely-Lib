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

import com.mobiles.mobil.model.Dto.CompraDTO;
import com.mobiles.mobil.model.Dto.DetalleCompraDTO;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.service.impl.CompraServiceImpl;
import com.mobiles.mobil.service.impl.DetalleCompraServiceImpl;
import com.mobiles.mobil.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/detalle-compras")
public class DetalleCompraController {
    
    private final DetalleCompraServiceImpl detalleCompraService;
    private final CompraServiceImpl compraService;
    private final AuthService authService;

    public DetalleCompraController(DetalleCompraServiceImpl detalleCompraService,
                                  CompraServiceImpl compraService,
                                  AuthService authService) {
        this.detalleCompraService = detalleCompraService;
        this.compraService = compraService;
        this.authService = authService;
    }

    // Purchase lines are created from the server-validated cart; only EMPRESA may adjust them manually.
    @PostMapping
    public ResponseEntity<DetalleCompraDTO> create(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody DetalleCompraDTO detalleCompraDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        DetalleCompraDTO created = detalleCompraService.create(detalleCompraDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);


    }

    // READ ALL - Solo EMPRESA puede ver todos los detalles
    @GetMapping
    public ResponseEntity<List<DetalleCompraDTO>> findAll(
            @RequestHeader("X-Session-Id") String sessionId) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<DetalleCompraDTO> detalles = detalleCompraService.findAll();
        return ResponseEntity.ok(detalles);
    }

    // READ - Ver detalles de una compra específica
    @GetMapping("/compra/{compraId}")
    public ResponseEntity<List<DetalleCompraDTO>> findDetallesByCompra(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long compraId) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        
        // Verificar acceso a la compra
        CompraDTO compra = compraService.findById(compraId);
        
        // Cliente solo puede ver detalles de sus compras
        if ("CLIENTE".equals(usuario.getRol()) && !usuario.getIdUsuario().equals(compra.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<DetalleCompraDTO> detalles = detalleCompraService.findByCompraId(compraId);
        return ResponseEntity.ok(detalles);
    }

    // READ BY ID - Ver detalle específico
    @GetMapping("/{id}")
    public ResponseEntity<DetalleCompraDTO> findById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        DetalleCompraDTO detalle = detalleCompraService.findById(id);
        
        // Verificar acceso a través de la compra
        CompraDTO compra = compraService.findById(detalle.getIdCompra());
        
        if ("CLIENTE".equals(usuario.getRol()) && !usuario.getIdUsuario().equals(compra.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(detalle);
    }

    // UPDATE - Solo EMPRESA puede actualizar detalles
    @PutMapping("/{id}")
    public ResponseEntity<DetalleCompraDTO> update(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id,
            @RequestBody DetalleCompraDTO detalleCompraDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        DetalleCompraDTO updated = detalleCompraService.update(id, detalleCompraDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Solo EMPRESA puede eliminar detalles
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        detalleCompraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
