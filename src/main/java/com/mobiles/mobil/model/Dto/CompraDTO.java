package com.mobiles.mobil.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompraDTO {
    private Long idCompra;
    private Long idUsuario;
    private String nombreUsuario; // Nombre del cliente
    private String direccionEnvio;
    private String distrito;
    private String calle;
    private String ciudad;
    private String fechaPago;
    private String fechaCreacionEmpaquetado;
    private String fechaEntrega;
    private String estadoProcesoCompra;
}

