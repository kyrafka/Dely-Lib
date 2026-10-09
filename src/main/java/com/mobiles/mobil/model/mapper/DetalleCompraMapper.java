package com.mobiles.mobil.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.mobiles.mobil.model.Dto.DetalleCompraDTO;
import com.mobiles.mobil.model.entity.Compra;
import com.mobiles.mobil.model.entity.DetalleCompra;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.mapper.Base.BaseMappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface DetalleCompraMapper extends BaseMappers<DetalleCompra, DetalleCompraDTO> {


  @Override
    @Mapping(source = "idCompra", target = "compra.idCompra")
    @Mapping(source = "idLibro", target = "libro.idLibro")
    DetalleCompra toEntity(DetalleCompraDTO dto);

    @Override
    @Mapping(source = "compra.idCompra", target = "idCompra")
    @Mapping(source = "libro.idLibro", target = "idLibro")
    @Mapping(source = "libro.titulo", target = "tituloLibro")
    DetalleCompraDTO toDTO(DetalleCompra entity);

    // Métodos auxiliares (por si MapStruct los necesita)
    default Compra mapCompra(Long idCompra) {
        if (idCompra == null) return null;
        Compra compra = new Compra();
        compra.setIdCompra(idCompra);
        return compra;
    }

    default Libro mapLibro(Long idLibro) {
        if (idLibro == null) return null;
        Libro libro = new Libro();
        libro.setIdLibro(idLibro);
        return libro;
    }


}
