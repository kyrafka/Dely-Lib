package com.mobiles.mobil.service.impl;
import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import com.mobiles.mobil.model.Dto.CarritoDTO;
import com.mobiles.mobil.model.entity.Carrito;
import com.mobiles.mobil.model.entity.Inventario;
import com.mobiles.mobil.model.mapper.CarritoMapper;
import com.mobiles.mobil.repository.CarritoRepository;
import com.mobiles.mobil.repository.InventarioRepository;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.repository.UsuarioRepository;
import com.mobiles.mobil.service.service.CarritoService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoServiceImpl implements CarritoService {
    private final CarritoMapper carritoMapper;
    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final InventarioRepository inventarioRepository;

    public CarritoServiceImpl(CarritoMapper carritoMapper, CarritoRepository carritoRepository,
                             UsuarioRepository usuarioRepository, LibroRepository libroRepository,
                             InventarioRepository inventarioRepository) {
        this.carritoMapper = carritoMapper;
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public CarritoDTO create(CarritoDTO dto) throws ServiceException {
        try {
            if (dto.getCantidad() == null || dto.getCantidad() <= 0) {
                throw new ServiceException("Quantity must be greater than zero");
            }

            // 1. Validar que el usuario existe
            if (!usuarioRepository.existsById(dto.getIdUsuario())) {
                throw new ServiceException("Usuario not found with id: " + dto.getIdUsuario());
            }

            // 2. Validar que el libro existe
            if (!libroRepository.existsById(dto.getIdLibro())) {
                throw new ServiceException("Libro not found with id: " + dto.getIdLibro());
            }

            // 3. Obtener inventario del libro para validar stock y precio
            Optional<Inventario> inventarioOpt = inventarioRepository.findByLibroIdLibro(dto.getIdLibro());
            if (inventarioOpt.isEmpty()) {
                throw new ServiceException("No inventory found for libro with id: " + dto.getIdLibro());
            }

            Inventario inventario = inventarioOpt.get();

            // 4. Validar stock suficiente
            if (inventario.getCantidadStock() < dto.getCantidad()) {
                throw new ServiceException("Insufficient stock. Available: " + inventario.getCantidadStock() +
                                         ", requested: " + dto.getCantidad());
            }

            // 5. Verificar si ya existe el item en el carrito del usuario
            Optional<Carrito> existingCarrito = carritoRepository.findByUsuarioIdUsuarioAndLibroIdLibro(
                dto.getIdUsuario(), dto.getIdLibro());

            if (existingCarrito.isPresent()) {
                // Actualizar cantidad existente
                Carrito existing = existingCarrito.get();
                int nuevaCantidad = existing.getCantidad() + dto.getCantidad();

                // Validar stock para la nueva cantidad
                if (inventario.getCantidadStock() < nuevaCantidad) {
                    throw new ServiceException("Insufficient stock for total quantity. Available: " +
                                             inventario.getCantidadStock() + ", requested total: " + nuevaCantidad);
                }

                existing.setCantidad(nuevaCantidad);
                existing.setPrecioUnitario(inventario.getPrecio()); // Actualizar con precio actual
                Carrito updated = carritoRepository.save(existing);
                return carritoMapper.toDTO(updated);
            } else {
                // 6. Crear nuevo item en carrito con precio actual del inventario
                dto.setPrecioUnitario(inventario.getPrecio());
                Carrito entity = carritoMapper.toEntity(dto);
                Carrito saved = carritoRepository.save(entity);
                return carritoMapper.toDTO(saved);
            }
        } catch (Exception e) {
            throw new ServiceException("Error creating carrito item: " + e.getMessage());
        }
    }

    @Override
    public CarritoDTO update(Long id, CarritoDTO dto) throws ServiceException {
        try {
            Optional<Carrito> existingOpt = carritoRepository.findById(id);
            if (existingOpt.isEmpty()) {
                throw new ServiceException("Carrito item not found with id: " + id);
            }

            Carrito existing = existingOpt.get();

            // Validar stock si se está cambiando la cantidad
            if (dto.getCantidad() != null && !dto.getCantidad().equals(existing.getCantidad())) {
                if (dto.getCantidad() <= 0) {
                    throw new ServiceException("Quantity must be greater than zero");
                }
                Optional<Inventario> inventarioOpt = inventarioRepository.findByLibroIdLibro(
                    existing.getLibro().getIdLibro());

                if (inventarioOpt.isEmpty()) {
                    throw new ServiceException("No inventory found for this libro");
                }

                Inventario inventario = inventarioOpt.get();
                if (inventario.getCantidadStock() < dto.getCantidad()) {
                    throw new ServiceException("Insufficient stock. Available: " + inventario.getCantidadStock());
                }

                existing.setCantidad(dto.getCantidad());
                existing.setPrecioUnitario(inventario.getPrecio()); // Actualizar precio actual
            }

            Carrito updated = carritoRepository.save(existing);
            return carritoMapper.toDTO(updated);
        } catch (Exception e) {
            throw new ServiceException("Error updating carrito item: " + e.getMessage());
        }
    }

    @Override
    public CarritoDTO findById(Long id) throws ServiceException {
        try {
            Optional<Carrito> carrito = carritoRepository.findById(id);
            if (carrito.isEmpty()) {
                throw new ServiceException("Carrito item not found with id: " + id);
            }
            return carritoMapper.toDTO(carrito.get());
        } catch (Exception e) {
            throw new ServiceException("Error finding carrito item: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        try {
            if (!carritoRepository.existsById(id)) {
                throw new ServiceException("Carrito item not found with id: " + id);
            }
            carritoRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error deleting carrito item: " + e.getMessage());
        }
    }

    @Override
    public List<CarritoDTO> findAll() throws ServiceException {
        try {
            List<Carrito> entities = carritoRepository.findAll();
            return carritoMapper.toDTOs(entities);
        } catch (Exception e) {
            throw new ServiceException("Error finding all carrito items: " + e.getMessage());
        }
    }

    public List<CarritoDTO> findByUsuarioId(Long usuarioId) throws ServiceException {
        try {
            if (!usuarioRepository.existsById(usuarioId)) {
                throw new ServiceException("Usuario not found with id: " + usuarioId);
            }

            List<Carrito> items = carritoRepository.findByUsuarioIdUsuario(usuarioId);
            return carritoMapper.toDTOs(items);
        } catch (Exception e) {
            throw new ServiceException("Error finding carrito by usuario: " + e.getMessage());
        }
    }

    @Transactional
    public void clearCarritoByUsuarioId(Long usuarioId) throws ServiceException {
        try {
            if (!usuarioRepository.existsById(usuarioId)) {
                throw new ServiceException("Usuario not found with id: " + usuarioId);
            }

            carritoRepository.deleteByUsuarioIdUsuario(usuarioId);
        } catch (Exception e) {
            throw new ServiceException("Error clearing carrito: " + e.getMessage());
        }
    }
}
