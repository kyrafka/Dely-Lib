package com.mobiles.mobil.model.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Ingresa tu correo electrónico.")
    @Email(message = "Ingresa un correo electrónico válido.")
    @Size(max = 254, message = "El correo electrónico es demasiado largo.")
    private String email;

    @NotBlank(message = "Ingresa tu contraseña.")
    @Size(max = 72, message = "La contraseña es demasiado larga.")
    private String password;
}
