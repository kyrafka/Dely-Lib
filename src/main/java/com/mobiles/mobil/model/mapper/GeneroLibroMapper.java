package com.mobiles.mobil.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mobiles.mobil.model.Dto.GeneroLibroDTO;
import com.mobiles.mobil.model.entity.Genero;
import com.mobiles.mobil.model.entity.GeneroLibro;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.mapper.Base.BaseMappers;

@Mapper(componentModel = "spring")
public interface GeneroLibroMapper extends BaseMappers<GeneroLibro, GeneroLibroDTO> {

    @Override
    @Mapping(source = "idGenero", target = "genero.idGenero")
    @Mapping(source = "idLibro", target = "libro.idLibro")
    GeneroLibro toEntity(GeneroLibroDTO dto);

    @Override
    @Mapping(source = "genero.idGenero", target = "idGenero")
    @Mapping(source = "libro.idLibro", target = "idLibro")
    GeneroLibroDTO toDTO(GeneroLibro entity);

    
    // Métodos auxiliares
    default Genero mapGenero(Long idGenero) {
        if (idGenero == null) return null;
        Genero g = new Genero();
        g.setIdGenero(idGenero);
        return g;
    }

    default Libro mapLibro(Long idLibro) {
        if (idLibro == null) return null;
        Libro l = new Libro();
        l.setIdLibro(idLibro);
        return l;
    }
}