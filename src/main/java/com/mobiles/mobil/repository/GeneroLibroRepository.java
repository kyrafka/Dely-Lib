package com.mobiles.mobil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.GeneroLibro;

public interface GeneroLibroRepository extends JpaRepository<GeneroLibro, Long>{



      // Método para verificar si un género tiene libros asociados (DELETE validation)
    boolean existsByGeneroIdGenero(Long idGenero);

    // Método para verificar si un género tiene libros ACTIVOS asociados
    boolean existsByGeneroIdGeneroAndEstado(Long idGenero, String estado);

    // Método para obtener todos los libros de un género (ENDPOINT MÓVIL)
    List<GeneroLibro> findByGeneroIdGenero(Long idGenero);

    // Método para obtener solo libros ACTIVOS de un género
    List<GeneroLibro> findByGeneroIdGeneroAndEstado(Long idGenero, String estado);


    // Para verificar si un libro tiene géneros asociados (DELETE validation Libro)
    boolean existsByLibroIdLibro(Long idLibro);


    // Para obtener todos los géneros de un libro específico (ENDPOINT GeneroLibro)
    List<GeneroLibro> findByLibroIdLibro(Long idLibro);

    // Para verificar si ya existe una relación específica (CREATE validation)
    boolean existsByGeneroIdGeneroAndLibroIdLibro(Long idGenero, Long idLibro);
}
