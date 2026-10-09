package com.mobiles.mobil.controller.LoginController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.mobiles.mobil.model.Dto.LoginRequest;
import com.mobiles.mobil.model.Dto.LoginResponse;
import com.mobiles.mobil.model.Dto.RegistroClienteRequest;
import com.mobiles.mobil.service.service.AuthService;


@RestController
@RequestMapping("/api/v1")
public class LoginController {
 private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    /** Registro de cliente */
    @PostMapping("/register")
    public ResponseEntity<Long> register(@Valid @RequestBody RegistroClienteRequest req) {
        Long id = authService.registrarCliente(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /** Login */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    /** Logout */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-Session-Id") String sessionId) {
        authService.logout(sessionId);
        return ResponseEntity.noContent().build();
    }
}
