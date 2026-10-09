package com.mobiles.mobil.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LibroDTO {
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