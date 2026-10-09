package com.mobiles.mobil.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobiles.mobil.model.entity.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long>{
  // Método para verificar si un libro está en carritos (DELETE validation)
    boolean existsByLibroIdLibro(Long idLibro);

        // Métodos adicionales para CarritoService
    List<Carrito> findByUsuarioIdUsuario(Long idUsuario);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Carrito c where c.usuario.idUsuario = :usuarioId order by c.libro.idLibro")
    List<Carrito> findByUsuarioIdUsuarioForCheckout(@Param("usuarioId") Long usuarioId);
    Optional<Carrito> findByUsuarioIdUsuarioAndLibroIdLibro(Long idUsuario, Long idLibro);
    void deleteByUsuarioIdUsuario(Long idUsuario);
}
