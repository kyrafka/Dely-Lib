package com.mobiles.mobil.model.mapper;

import org.mapstruct.Mapper;

import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.mapper.Base.BaseMappers;

@Mapper(componentModel = "spring")

public interface LibroMapper extends BaseMappers<Libro, LibroDTO> {
}

