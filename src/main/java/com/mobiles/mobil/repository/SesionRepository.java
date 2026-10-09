package com.mobiles.mobil.repository;

import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mobiles.mobil.model.entity.Sesion;

public interface SesionRepository extends JpaRepository <Sesion, String> {
    Optional<Sesion> findByIdSesionAndActivo(String idSesion, String activo);

    @Modifying
    @Transactional
    @Query("delete from Sesion s where coalesce(s.activo, '0') <> '1' or (s.expiraEn is not null and s.expiraEn <= :now)")
    int deleteExpiredOrInactive(@Param("now") LocalDateTime now);

}
