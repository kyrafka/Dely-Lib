package com.mobiles.mobil.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "sesion", indexes = {
    @Index(name = "idx_sesion_expira_en", columnList = "expira_en")
})
public class Sesion {
  @Id
    @Column(name = "id_sesion", length = 64)
    private String idSesion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "creado_en")
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(name = "expira_en")
    private LocalDateTime expiraEn;

    @Column(name = "activo")
    @Builder.Default
    private String activo = "1"; // '1' activo, '0' inactivo
}
