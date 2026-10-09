package com.mobiles.mobil.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mobiles.mobil.model.entity.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long>{


    // Método para verificar si un libro tiene inventario (DELETE validation)
    boolean existsByLibroIdLibro(Long idLibro);

     // Para buscar inventario por libro específico (ENDPOINT adicional)
    Optional<Inventario> findByLibroIdLibro(Long idLibro);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventario i where i.libro.idLibro = :idLibro")
    Optional<Inventario> findByLibroIdLibroForUpdate(@Param("idLibro") Long idLibro);
}
