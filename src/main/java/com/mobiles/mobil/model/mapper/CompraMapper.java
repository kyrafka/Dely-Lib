package com.mobiles.mobil.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mobiles.mobil.model.Dto.CompraDTO;
import com.mobiles.mobil.model.entity.Compra;
import com.mobiles.mobil.model.mapper.Base.BaseMappers;

@Mapper(componentModel = "spring")
public interface CompraMapper extends BaseMappers<Compra, CompraDTO> {

    @Override
    @Mapping(source = "idUsuario", target = "usuario.idUsuario")
    Compra toEntity(CompraDTO dto);

    @Override
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "usuario.nombreCompleto", target = "nombreUsuario")
    CompraDTO toDTO(Compra entity);
}
