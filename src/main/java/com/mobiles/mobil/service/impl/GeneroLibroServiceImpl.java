package com.mobiles.mobil.service.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import com.mobiles.mobil.model.Dto.GeneroLibroDTO;
import com.mobiles.mobil.model.entity.GeneroLibro;
import com.mobiles.mobil.model.mapper.GeneroLibroMapper;
import com.mobiles.mobil.repository.GeneroLibroRepository;
import com.mobiles.mobil.repository.GeneroRepository;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.service.service.GeneroLibroService;

@Service
public class GeneroLibroServiceImpl implements GeneroLibroService {

    private final GeneroLibroMapper generoLibroMapper;
    private final GeneroLibroRepository generoLibroRepository;
    private final GeneroRepository generoRepository;
    private final LibroRepository libroRepository;

    public GeneroLibroServiceImpl(GeneroLibroMapper generoLibroMapper, GeneroLibroRepository generoLibroRepository,
                                 GeneroRepository generoRepository, LibroRepository libroRepository) {
        this.generoLibroMapper = generoLibroMapper;
        this.generoLibroRepository = generoLibroRepository;
        this.generoRepository = generoRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    public GeneroLibroDTO create(GeneroLibroDTO dto) throws ServiceException {
        try {
            // 1. Validar que el género existe
            if (!generoRepository.existsById(dto.getIdGenero())) {
                throw new ServiceException("Genero not found with id: " + dto.getIdGenero());
            }
            
            // 2. Validar que el libro existe
            if (!libroRepository.existsById(dto.getIdLibro())) {
                throw new ServiceException("Libro not found with id: " + dto.getIdLibro());
            }
            
            // 3. Verificar que la relación no existe ya
            boolean relationExists = generoLibroRepository.existsByGeneroIdGeneroAndLibroIdLibro(
                dto.getIdGenero(), dto.getIdLibro());
            if (relationExists) {
                throw new ServiceException("Relation already exists between genero " + dto.getIdGenero() + 
                                         " and libro " + dto.getIdLibro());
            }
            
            // 4. Crear la relación
            GeneroLibro entity = generoLibroMapper.toEntity(dto);
            if (entity.getEstado() == null) {
                entity.setEstado("ACTIVO"); // Estado por defecto
            }
            
            GeneroLibro saved = generoLibroRepository.save(entity);
            return generoLibroMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ServiceException("Error creating genero-libro relation: " + e.getMessage());
        }
    }

    @Override
    public GeneroLibroDTO update(Long id, GeneroLibroDTO dto) throws ServiceException {
        try {
            Optional<GeneroLibro> existingOpt = generoLibroRepository.findById(id);
            if (existingOpt.isEmpty()) {
                throw new ServiceException("GeneroLibro relation not found with id: " + id);
            }
            
            GeneroLibro existing = existingOpt.get();
            existing.setEstado(dto.getEstado());
            
            GeneroLibro updated = generoLibroRepository.save(existing);
            return generoLibroMapper.toDTO(updated);
        } catch (Exception e) {
            throw new ServiceException("Error updating genero-libro relation: " + e.getMessage());
        }
    }

    @Override
    public GeneroLibroDTO findById(Long id) throws ServiceException {
        try {
            Optional<GeneroLibro> generoLibro = generoLibroRepository.findById(id);
            if (generoLibro.isEmpty()) {
                throw new ServiceException("GeneroLibro relation not found with id: " + id);
            }
            return generoLibroMapper.toDTO(generoLibro.get());
        } catch (Exception e) {
            throw new ServiceException("Error finding genero-libro relation: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        try {
            if (!generoLibroRepository.existsById(id)) {
                throw new ServiceException("GeneroLibro relation not found with id: " + id);
            }
            
            generoLibroRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error deleting genero-libro relation: " + e.getMessage());
        }
    }

    @Override
    public List<GeneroLibroDTO> findAll() throws ServiceException {
        try {
            List<GeneroLibro> entities = generoLibroRepository.findAll();
            return generoLibroMapper.toDTOs(entities);
        } catch (Exception e) {
            throw new ServiceException("Error finding all genero-libro relations: " + e.getMessage());
        }
    }

    public List<GeneroLibroDTO> findGenerosByLibroId(Long libroId) throws ServiceException {
        try {
            if (!libroRepository.existsById(libroId)) {
                throw new ServiceException("Libro not found with id: " + libroId);
            }
            
            List<GeneroLibro> relations = generoLibroRepository.findByLibroIdLibro(libroId);
            return generoLibroMapper.toDTOs(relations);
        } catch (Exception e) {
            throw new ServiceException("Error finding generos for libro: " + e.getMessage());
        }
    }
}