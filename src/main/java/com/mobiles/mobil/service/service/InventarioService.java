package com.mobiles.mobil.service.service;

import org.hibernate.service.spi.ServiceException;

import com.mobiles.mobil.model.Dto.InventarioDTO;
import com.mobiles.mobil.model.entity.Inventario;
import com.mobiles.mobil.service.base.GenericService;

public interface InventarioService extends GenericService<Inventario, InventarioDTO, Long>{
 // Método adicional para buscar por libro
    InventarioDTO findByLibroId(Long libroId) throws ServiceException;
}
