package com.mobiles.mobil.service.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import com.mobiles.mobil.model.Dto.GeneroDTO;
import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Genero;
import com.mobiles.mobil.model.entity.GeneroLibro;
import com.mobiles.mobil.model.mapper.GeneroMapper;
import com.mobiles.mobil.model.mapper.LibroMapper;
import com.mobiles.mobil.repository.GeneroLibroRepository;
import com.mobiles.mobil.repository.GeneroRepository;
import com.mobiles.mobil.service.service.GeneroService;


@Service
public class GeneroServiceImpl implements GeneroService{
private final GeneroMapper generoMapper;
    private final GeneroRepository generoRepository;
    private final GeneroLibroRepository generoLibroRepository;
    private final LibroMapper libroMapper;

    public GeneroServiceImpl(GeneroMapper generoMapper, GeneroRepository generoRepository,
                           GeneroLibroRepository generoLibroRepository, LibroMapper libroMapper) {
        this.generoMapper = generoMapper;
        this.generoRepository = generoRepository;
        this.generoLibroRepository = generoLibroRepository;
        this.libroMapper = libroMapper;
    }

    @Override
    public GeneroDTO create(GeneroDTO dto) throws ServiceException {
        try {
            Genero entity = generoMapper.toEntity(dto);
            Genero saved = generoRepository.save(entity);
            return generoMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ServiceException("Error creating genero: " + e.getMessage());
        }
    }

    @Override
    public GeneroDTO update(Long id, GeneroDTO dto) throws ServiceException {
        try {
            Optional<Genero> existingOpt = generoRepository.findById(id);
            if (existingOpt.isEmpty()) {
                throw new ServiceException("Genero not found with id: " + id);
            }

            Genero existing = existingOpt.get();
            existing.setNombre(dto.getNombre());
            existing.setDescripcion(dto.getDescripcion());

            Genero updated = generoRepository.save(existing);
            return generoMapper.toDTO(updated);
        } catch (Exception e) {
            throw new ServiceException("Error updating genero: " + e.getMessage());
        }
    }

    @Override
    public GeneroDTO findById(Long id) throws ServiceException {
        try {
            Optional<Genero> genero = generoRepository.findById(id);
            if (genero.isEmpty()) {
                throw new ServiceException("Genero not found with id: " + id);
            }
            return generoMapper.toDTO(genero.get());
        } catch (Exception e) {
            throw new ServiceException("Error finding genero: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        try {
            // 1. Verificar que el género existe
            if (!generoRepository.existsById(id)) {
                throw new ServiceException("Genero not found with id: " + id);
            }

            // 2. Verificar que no tiene libros ACTIVOS asociados (REGLA DE NEGOCIO CRÍTICA)
            boolean hasActiveBooks = generoLibroRepository.existsByGeneroIdGeneroAndEstado(id, "ACTIVO");
            if (hasActiveBooks) {
                throw new ServiceException("Cannot delete genero: has active books associated. Remove books first.");
            }

            // 3. Si no tiene libros activos, eliminar
            generoRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error deleting genero: " + e.getMessage());
        }
    }

    @Override
    public List<GeneroDTO> findAll() throws ServiceException {
        try {
            List<Genero> entities = generoRepository.findAll();
            return generoMapper.toDTOs(entities);
        } catch (Exception e) {
            throw new ServiceException("Error finding all generos: " + e.getMessage());
        }
    }

    public List<LibroDTO> findLibrosByGeneroId(Long generoId) throws ServiceException {
        try {
            // 1. Verificar que el género existe
            if (!generoRepository.existsById(generoId)) {
                throw new ServiceException("Genero not found with id: " + generoId);
            }

            // 2. Buscar solo las relaciones ACTIVAS genero-libro para este género
            List<GeneroLibro> generoLibros = generoLibroRepository.findByGeneroIdGeneroAndEstado(generoId, "ACTIVO");

            // 3. Extraer los libros y convertir a DTO
            List<LibroDTO> libros = generoLibros.stream()
                .map(gl -> libroMapper.toDTO(gl.getLibro()))
                .toList();

            return libros;
        } catch (Exception e) {
            throw new ServiceException("Error finding libros by genero: " + e.getMessage());
        }
    }
}
