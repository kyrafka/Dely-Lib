package com.mobiles.mobil.service.impl;
import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.mapper.LibroMapper;
import com.mobiles.mobil.repository.CarritoRepository;
import com.mobiles.mobil.repository.DetalleCompraRepository;
import com.mobiles.mobil.repository.GeneroLibroRepository;
import com.mobiles.mobil.repository.InventarioRepository;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.service.service.LibroService;

@Service
public class LibroServiceImpl implements LibroService {
    private final LibroMapper libroMapper; 
    private final LibroRepository libroRepository;
    private final InventarioRepository inventarioRepository;
    private final GeneroLibroRepository generoLibroRepository;
    private final CarritoRepository carritoRepository;
    private final DetalleCompraRepository detalleCompraRepository;

    public LibroServiceImpl(LibroMapper libroMapper, LibroRepository libroRepository,
                           InventarioRepository inventarioRepository, GeneroLibroRepository generoLibroRepository,
                           CarritoRepository carritoRepository, DetalleCompraRepository detalleCompraRepository) {
        this.libroMapper = libroMapper;
        this.libroRepository = libroRepository;
        this.inventarioRepository = inventarioRepository;
        this.generoLibroRepository = generoLibroRepository;
        this.carritoRepository = carritoRepository;
        this.detalleCompraRepository = detalleCompraRepository;
    }

    @Override
    public LibroDTO create(LibroDTO dto) throws ServiceException {
        try {

            Libro entity = libroMapper.toEntity(dto);
            Libro saved = libroRepository.save(entity);
            return libroMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ServiceException("Error creating libro: " + e.getMessage());
        }
    }

    @Override
    public LibroDTO update(Long id, LibroDTO dto) throws ServiceException {
        try {
            Optional<Libro> existingOpt = libroRepository.findById(id);
            if (existingOpt.isEmpty()) {
                throw new ServiceException("Libro not found with id: " + id);
            }
            
            Libro existing = existingOpt.get();
            existing.setTitulo(dto.getTitulo());
            existing.setPuntuacionPromedio(dto.getPuntuacionPromedio());
            existing.setSinopsis(dto.getSinopsis());
            existing.setFechaLanzamiento(dto.getFechaLanzamiento());
            existing.setIsbn(dto.getIsbn());
            existing.setEdicion(dto.getEdicion());
            existing.setEditorial(dto.getEditorial());
            existing.setIdioma(dto.getIdioma());
            existing.setNumPaginas(dto.getNumPaginas());
            existing.setNombreCompletoAutor(dto.getNombreCompletoAutor());
            existing.setImagenPortada(dto.getImagenPortada());
            
            Libro updated = libroRepository.save(existing);
            return libroMapper.toDTO(updated);
        } catch (Exception e) {
            throw new ServiceException("Error updating libro: " + e.getMessage());
        }
    }

    @Override
    public LibroDTO findById(Long id) throws ServiceException {
        try {
            Optional<Libro> libro = libroRepository.findById(id);
            if (libro.isEmpty()) {
                throw new ServiceException("Libro not found with id: " + id);
            }
            return libroMapper.toDTO(libro.get());
        } catch (Exception e) {
            throw new ServiceException("Error finding libro: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        try {
            // 1. Verificar que el libro existe
            if (!libroRepository.existsById(id)) {
                throw new ServiceException("Libro not found with id: " + id);
            }
            
            // 2. Verificar relaciones que impiden el DELETE
            boolean hasInventario = inventarioRepository.existsByLibroIdLibro(id);
            if (hasInventario) {
                throw new ServiceException("Cannot delete libro: has inventory records. Remove inventory first.");
            }
            
            boolean hasGeneros = generoLibroRepository.existsByLibroIdLibro(id);
            if (hasGeneros) {
                throw new ServiceException("Cannot delete libro: has genre associations. Remove genre associations first.");
            }
            
            boolean hasCarrito = carritoRepository.existsByLibroIdLibro(id);
            if (hasCarrito) {
                throw new ServiceException("Cannot delete libro: exists in shopping carts. Remove from carts first.");
            }
            
            boolean hasCompras = detalleCompraRepository.existsByLibroIdLibro(id);
            if (hasCompras) {
                throw new ServiceException("Cannot delete libro: has purchase records. Cannot delete books with purchase history.");
            }
            
            // 3. Si no tiene relaciones, eliminar
            libroRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error deleting libro: " + e.getMessage());
        }
    }

    @Override
    public List<LibroDTO> findAll() throws ServiceException {
        try {
            List<Libro> entities = libroRepository.findAll();
            return libroMapper.toDTOs(entities);
        } catch (Exception e) {
            throw new ServiceException("Error finding all libros: " + e.getMessage());
        }
    }
}