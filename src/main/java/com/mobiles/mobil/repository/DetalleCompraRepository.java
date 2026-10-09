package com.mobiles.mobil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.DetalleCompra;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long>{
  // Método para verificar si un libro tiene historial de compras (DELETE validation)
    boolean existsByLibroIdLibro(Long idLibro);
    
    // Método adicional para DetalleCompraService
    List<DetalleCompra> findByCompraIdCompra(Long idCompra);
}
