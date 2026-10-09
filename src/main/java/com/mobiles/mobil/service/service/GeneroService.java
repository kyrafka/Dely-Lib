package com.mobiles.mobil.service.service;

import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.mobiles.mobil.model.Dto.GeneroDTO;
import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Genero;
import com.mobiles.mobil.service.base.GenericService;

public interface GeneroService extends GenericService<Genero, GeneroDTO, Long>{
// Método adicional para app móvil
    List<LibroDTO> findLibrosByGeneroId(Long generoId) throws ServiceException;
}
