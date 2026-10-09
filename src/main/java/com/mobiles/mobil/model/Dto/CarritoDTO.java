package com.mobiles.mobil.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarritoDTO {
    private Long idCarrito;
    private Long idUsuario;
    private Long idLibro;
    private Double precioUnitario;
    private Integer cantidad;
}
