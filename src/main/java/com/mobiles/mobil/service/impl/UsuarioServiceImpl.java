package com.mobiles.mobil.service.impl;

import java.util.List;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import com.mobiles.mobil.model.Dto.UsuarioDTO;
import com.mobiles.mobil.model.mapper.UsuarioMapper;
import com.mobiles.mobil.repository.UsuarioRepository;
import com.mobiles.mobil.service.service.UsuarioService;


@Service
public class UsuarioServiceImpl implements UsuarioService{
  
    private final UsuarioRepository  usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }


    @Override
    public UsuarioDTO create(UsuarioDTO dto) throws ServiceException {
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public UsuarioDTO update(Long id, UsuarioDTO dto) throws ServiceException {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public UsuarioDTO findById(Long id) throws ServiceException {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public List<UsuarioDTO> findAll() throws ServiceException {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
