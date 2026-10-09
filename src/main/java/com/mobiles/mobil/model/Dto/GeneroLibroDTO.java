package com.mobiles.mobil.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeneroLibroDTO {
private Long idGeneroLibros;
    private Long idGenero;
    private Long idLibro;
    private String estado;
}
