package com.mobiles.mobil.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventarioDTO {
    private Long idInventario;
    private Long idLibro;     // solo id para simplificar
    private Double precio;
    private Integer cantidadStock;
}
