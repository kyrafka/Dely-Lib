package com.mobiles.mobil.model.mapper;

import org.mapstruct.Mapper;

import com.mobiles.mobil.model.Dto.UsuarioDTO;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.model.mapper.Base.BaseMappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper extends BaseMappers<Usuario, UsuarioDTO> {
}

