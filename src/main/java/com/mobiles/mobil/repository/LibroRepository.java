package com.mobiles.mobil.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.Libro;

public interface LibroRepository extends JpaRepository<Libro, Long>{

}
