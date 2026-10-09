package com.mobiles.mobil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.Compra;

public interface CompraRepository extends JpaRepository<Compra, Long>{
 // Método adicional para CompraService
    List<Compra> findByUsuarioIdUsuario(Long idUsuario);
}
