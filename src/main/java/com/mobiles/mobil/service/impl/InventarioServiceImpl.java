package com.mobiles.mobil.service.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import com.mobiles.mobil.model.Dto.InventarioDTO;
import com.mobiles.mobil.model.entity.Inventario;
import com.mobiles.mobil.model.mapper.InventarioMapper;
import com.mobiles.mobil.repository.CarritoRepository;
import com.mobiles.mobil.repository.DetalleCompraRepository;
import com.mobiles.mobil.repository.InventarioRepository;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.service.service.InventarioService;

@Service
public class InventarioServiceImpl implements InventarioService {
    private final InventarioRepository inventarioRepository;
    private final InventarioMapper inventarioMapper;
    private final LibroRepository libroRepository;
    private final CarritoRepository carritoRepository;
    private final DetalleCompraRepository detalleCompraRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository, InventarioMapper inventarioMapper,
                               LibroRepository libroRepository, CarritoRepository carritoRepository,
                               DetalleCompraRepository detalleCompraRepository) {
        this.inventarioRepository = inventarioRepository;
        this.inventarioMapper = inventarioMapper;
        this.libroRepository = libroRepository;
        this.carritoRepository = carritoRepository;
        this.detalleCompraRepository = detalleCompraRepository;
    }

    @Override
    public InventarioDTO create(InventarioDTO dto) throws ServiceException {
        try {
            // 1. Validar que el libro existe
            if (!libroRepository.existsById(dto.getIdLibro())) {
                throw new ServiceException("Libro not found with id: " + dto.getIdLibro());
            }

            // 2. Verificar que el libro no tenga ya inventario
            boolean hasInventory = inventarioRepository.existsByLibroIdLibro(dto.getIdLibro());
            if (hasInventory) {
                throw new ServiceException("Libro already has inventory. Use update instead.");
            }

            // 3. Validaciones de negocio
            if (dto.getPrecio() == null || dto.getPrecio() <= 0) {
                throw new ServiceException("Price must be greater than 0");
            }

            if (dto.getCantidadStock() == null || dto.getCantidadStock() < 0) {
                throw new ServiceException("Stock quantity cannot be negative");
            }

            // 4. Crear inventario
            Inventario entity = inventarioMapper.toEntity(dto);
            Inventario saved = inventarioRepository.save(entity);
            return inventarioMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ServiceException("Error creating inventario: " + e.getMessage());
        }
    }

    @Override
    public InventarioDTO update(Long id, InventarioDTO dto) throws ServiceException {
        try {
            Optional<Inventario> existingOpt = inventarioRepository.findById(id);
            if (existingOpt.isEmpty()) {
                throw new ServiceException("Inventario not found with id: " + id);
            }

            // Validaciones de negocio
            if (dto.getPrecio() != null && dto.getPrecio() <= 0) {
                throw new ServiceException("Price must be greater than 0");
            }

            if (dto.getCantidadStock() != null && dto.getCantidadStock() < 0) {
                throw new ServiceException("Stock quantity cannot be negative");
            }

            Inventario existing = existingOpt.get();
            if (dto.getPrecio() != null) {
                existing.setPrecio(dto.getPrecio());
            }
            if (dto.getCantidadStock() != null) {
                existing.setCantidadStock(dto.getCantidadStock());
            }

            Inventario updated = inventarioRepository.save(existing);
            return inventarioMapper.toDTO(updated);
        } catch (Exception e) {
            throw new ServiceException("Error updating inventario: " + e.getMessage());
        }
    }

    @Override
    public InventarioDTO findById(Long id) throws ServiceException {
        try {
            Optional<Inventario> inventario = inventarioRepository.findById(id);
            if (inventario.isEmpty()) {
                throw new ServiceException("Inventario not found with id: " + id);
            }
            return inventarioMapper.toDTO(inventario.get());
        } catch (Exception e) {
            throw new ServiceException("Error finding inventario: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        try {
            // 1. Verificar que el inventario existe
            if (!inventarioRepository.existsById(id)) {
                throw new ServiceException("Inventario not found with id: " + id);
            }

            // 2. Obtener el libro asociado para validaciones
            Inventario inventario = inventarioRepository.findById(id).get();
            Long idLibro = inventario.getLibro().getIdLibro();

            // 3. Verificar dependencias
            boolean hasCarrito = carritoRepository.existsByLibroIdLibro(idLibro);
            boolean hasCompras = detalleCompraRepository.existsByLibroIdLibro(idLibro);

            // 4. Si tiene dependencias, solo marcar como no disponible (stock = 0)
            if (hasCarrito || hasCompras) {
                inventario.setCantidadStock(0);
                inventarioRepository.save(inventario);
                System.out.println("⚠️ Inventario marcado como no disponible (stock=0) debido a dependencias");
            } else {
                // 5. Si no tiene dependencias, eliminar completamente
                inventarioRepository.deleteById(id);
                System.out.println("✅ Inventario eliminado completamente");
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error deleting inventario: " + e.getMessage());
        }
    }

    @Override
    public List<InventarioDTO> findAll() throws ServiceException {
        try {
            List<Inventario> entities = inventarioRepository.findAll();
            return inventarioMapper.toDTOs(entities);
        } catch (Exception e) {
            throw new ServiceException("Error finding all inventarios: " + e.getMessage());
        }
    }

    public InventarioDTO findByLibroId(Long libroId) throws ServiceException {
        try {
            if (!libroRepository.existsById(libroId)) {
                throw new ServiceException("Libro not found with id: " + libroId);
            }

            Optional<Inventario> inventario = inventarioRepository.findByLibroIdLibro(libroId);
            if (inventario.isEmpty()) {
                throw new ServiceException("No inventory found for libro with id: " + libroId);
            }

            return inventarioMapper.toDTO(inventario.get());
        } catch (Exception e) {
            throw new ServiceException("Error finding inventario by libro: " + e.getMessage());
        }
    }
}