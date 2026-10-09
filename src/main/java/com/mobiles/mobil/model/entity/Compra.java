package com.mobiles.mobil.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "compra")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompra;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    private String direccionEnvio;
    private String distrito;
    private String calle;
    private String ciudad;
    private String fechaPago;
    private String fechaCreacionEmpaquetado;
    private String fechaEntrega;
    private String estadoProcesoCompra; // PAGADO, ENVIADO, ENTREGADO

    // getters y setters
}
