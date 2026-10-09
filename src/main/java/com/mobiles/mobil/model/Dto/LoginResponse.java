package com.mobiles.mobil.model.Dto;

import lombok.Data;

@Data
public class LoginResponse {
private String sessionId;
    private Long userId;
    private String nombre;
    private String rol;
}
