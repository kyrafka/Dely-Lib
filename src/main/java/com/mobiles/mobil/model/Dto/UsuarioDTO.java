package com.mobiles.mobil.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioDTO {
 private Long idUsuario;
    private String nombreCompleto;
    private String fechaNacimiento;
    private String estado;
    private String rol;
    private String password;
    private String email;

}
