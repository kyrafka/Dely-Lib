package com.mobiles.mobil.model.mapper;

import org.mapstruct.Mapper;

import com.mobiles.mobil.model.Dto.GeneroDTO;
import com.mobiles.mobil.model.entity.Genero;
import com.mobiles.mobil.model.mapper.Base.BaseMappers;

@Mapper(componentModel = "spring")
public interface GeneroMapper extends BaseMappers<Genero, GeneroDTO> {
}