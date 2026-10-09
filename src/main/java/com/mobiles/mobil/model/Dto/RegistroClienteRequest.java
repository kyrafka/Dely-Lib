package com.mobiles.mobil.model.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroClienteRequest {
    @NotBlank(message = "Ingresa tu nombre y apellido.")
    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres.")
    private String nombreCompleto;

    @NotBlank(message = "Ingresa tu correo electrónico.")
    @Email(message = "Ingresa un correo electrónico válido.")
    @Size(max = 254, message = "El correo electrónico es demasiado largo.")
    private String email;

    @NotBlank(message = "Ingresa una contraseña.")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres.")
    private String password;

    @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$", message = "La fecha debe usar el formato AAAA-MM-DD.")
    private String fechaNacimiento;
}
