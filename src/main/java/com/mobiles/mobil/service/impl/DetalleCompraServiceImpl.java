package com.mobiles.mobil.service.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import com.mobiles.mobil.model.Dto.DetalleCompraDTO;
import com.mobiles.mobil.model.entity.DetalleCompra;
import com.mobiles.mobil.model.mapper.DetalleCompraMapper;
import com.mobiles.mobil.repository.CompraRepository;
import com.mobiles.mobil.repository.DetalleCompraRepository;
import com.mobiles.mobil.repository.InventarioRepository;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.service.service.DetalleCompraService;

@Service
public class DetalleCompraServiceImpl implements DetalleCompraService {
    private final DetalleCompraMapper detalleCompraMapper;
    private final DetalleCompraRepository detalleCompraRepository;
    private final CompraRepository compraRepository;
    private final LibroRepository libroRepository;
    private final InventarioRepository inventarioRepository;

    public DetalleCompraServiceImpl(DetalleCompraMapper detalleCompraMapper, 
                                   DetalleCompraRepository detalleCompraRepository,
                                   CompraRepository compraRepository,
                                   LibroRepository libroRepository,
                                   InventarioRepository inventarioRepository) {
        this.detalleCompraMapper = detalleCompraMapper;
        this.detalleCompraRepository = detalleCompraRepository;
        this.compraRepository = compraRepository;
        this.libroRepository = libroRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public DetalleCompraDTO create(DetalleCompraDTO dto) throws ServiceException {
        try {
            if (dto.getCantidad() == null || dto.getCantidad() <= 0) {
                throw new ServiceException("Quantity must be greater than zero");
            }

            // 1. Validate the purchase, book, and current inventory price on the server.
            if (!compraRepository.existsById(dto.getIdCompra())) {
                throw new ServiceException("Compra not found with id: " + dto.getIdCompra());
            }
            
            // 2. Validar que el libro existe
            if (!libroRepository.existsById(dto.getIdLibro())) {
                throw new ServiceException("Libro not found with id: " + dto.getIdLibro());
            }

            var inventario = inventarioRepository.findByLibroIdLibro(dto.getIdLibro())
                    .orElseThrow(() -> new ServiceException("Inventory not found for book"));
            if (inventario.getPrecio() == null || inventario.getPrecio() < 0) {
                throw new ServiceException("Inventory price is invalid");
            }

            dto.setPrecioUnitario(inventario.getPrecio());
            dto.setSubtotal(inventario.getPrecio() * dto.getCantidad());
            
            DetalleCompra entity = detalleCompraMapper.toEntity(dto);
            DetalleCompra saved = detalleCompraRepository.save(entity);
            return detalleCompraMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ServiceException("Error creating detalle compra: " + e.getMessage());
        }
    }

    @Override
    public DetalleCompraDTO update(Long id, DetalleCompraDTO dto) throws ServiceException {
        try {
            Optional<DetalleCompra> existingOpt = detalleCompraRepository.findById(id);
            if (existingOpt.isEmpty()) {
                throw new ServiceException("DetalleCompra not found with id: " + id);
            }
            
            DetalleCompra existing = existingOpt.get();
            
            // Solo permitir actualizar cantidad (recalcular subtotal)
            if (dto.getCantidad() != null) {
                existing.setCantidad(dto.getCantidad());
                existing.setSubtotal(existing.getPrecioUnitario() * dto.getCantidad());
            }
            
            DetalleCompra updated = detalleCompraRepository.save(existing);
            return detalleCompraMapper.toDTO(updated);
        } catch (Exception e) {
            throw new ServiceException("Error updating detalle compra: " + e.getMessage());
        }
    }

    @Override
    public DetalleCompraDTO findById(Long id) throws ServiceException {
        try {
            Optional<DetalleCompra> detalle = detalleCompraRepository.findById(id);
            if (detalle.isEmpty()) {
                throw new ServiceException("DetalleCompra not found with id: " + id);
            }
            return detalleCompraMapper.toDTO(detalle.get());
        } catch (Exception e) {
            throw new ServiceException("Error finding detalle compra: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        try {
            if (!detalleCompraRepository.existsById(id)) {
                throw new ServiceException("DetalleCompra not found with id: " + id);
            }
            detalleCompraRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error deleting detalle compra: " + e.getMessage());
        }
    }

    @Override
    public List<DetalleCompraDTO> findAll() throws ServiceException {
        try {
            List<DetalleCompra> entities = detalleCompraRepository.findAll();
            return detalleCompraMapper.toDTOs(entities);
        } catch (Exception e) {
            throw new ServiceException("Error finding all detalle compras: " + e.getMessage());
        }
    }

    public List<DetalleCompraDTO> findByCompraId(Long compraId) throws ServiceException {
        try {
            if (!compraRepository.existsById(compraId)) {
                throw new ServiceException("Compra not found with id: " + compraId);
            }
            
            List<DetalleCompra> detalles = detalleCompraRepository.findByCompraIdCompra(compraId);
            return detalleCompraMapper.toDTOs(detalles);
        } catch (Exception e) {
            throw new ServiceException("Error finding detalles by compra: " + e.getMessage());
        }
    }
}
