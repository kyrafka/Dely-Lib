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
@Table(name = "genero_libros")
public class GeneroLibro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGeneroLibros;

    @ManyToOne
    @JoinColumn(name = "idGenero")
    private Genero genero;

    @ManyToOne
    @JoinColumn(name = "idLibro")
    private Libro libro;

    private String estado;

    // getters y setters
}