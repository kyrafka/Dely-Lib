package com.mobiles.mobil.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLibro;

    private String titulo;
    private Double puntuacionPromedio;
    private String sinopsis;
    private String fechaLanzamiento;
    private String isbn;
    private String edicion;
    private String editorial;
    private String idioma;
    private Integer numPaginas;
    private String nombreCompletoAutor;
    private String imagenPortada;

  
}