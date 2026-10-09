package com.mobiles.mobil.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mobiles.mobil.model.Dto.CarritoDTO;
import com.mobiles.mobil.model.entity.Carrito;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.model.mapper.Base.BaseMappers;

@Mapper(componentModel = "spring")
public interface CarritoMapper extends BaseMappers<Carrito, CarritoDTO> {


 @Override
    @Mapping(source = "idUsuario", target = "usuario.idUsuario")
    @Mapping(source = "idLibro", target = "libro.idLibro")
    Carrito toEntity(CarritoDTO dto);

    @Override
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "libro.idLibro", target = "idLibro")
    CarritoDTO toDTO(Carrito entity);

    default Usuario mapUsuario(Long idUsuario) {
        if (idUsuario == null) return null;
        Usuario u = new Usuario();
        u.setIdUsuario(idUsuario);
        return u;
    }

    default Libro mapLibro(Long idLibro) {
        if (idLibro == null) return null;
        Libro l = new Libro();
        l.setIdLibro(idLibro);
        return l;
    }

}
