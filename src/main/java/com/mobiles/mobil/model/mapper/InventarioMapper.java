package com.mobiles.mobil.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.mobiles.mobil.model.Dto.InventarioDTO;
import com.mobiles.mobil.model.entity.Inventario;
import com.mobiles.mobil.model.mapper.Base.BaseMappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventarioMapper extends BaseMappers<Inventario, InventarioDTO> {

   @Override
    @Mapping(source = "idLibro", target = "libro.idLibro")
    Inventario toEntity(InventarioDTO dto);

    @Override
    @Mapping(source = "libro.idLibro", target = "idLibro")
    InventarioDTO toDTO(Inventario entity);
}
