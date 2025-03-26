package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Parameter;
import com.mycompany.myapp.repository.ParameterRepository;
import com.mycompany.myapp.service.dto.ParameterDTO;
import com.mycompany.myapp.service.mapper.ParameterMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Parameter}.
 */
@Service
@Transactional
public class ParameterService {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterService.class);

    private final ParameterRepository parameterRepository;

    private final ParameterMapper parameterMapper;

    public ParameterService(ParameterRepository parameterRepository, ParameterMapper parameterMapper) {
        this.parameterRepository = parameterRepository;
        this.parameterMapper = parameterMapper;
    }

    /**
     * Save a parameter.
     *
     * @param parameterDTO the entity to save.
     * @return the persisted entity.
     */
    public ParameterDTO save(ParameterDTO parameterDTO) {
        LOG.debug("Request to save Parameter : {}", parameterDTO);
        Parameter parameter = parameterMapper.toEntity(parameterDTO);
        parameter = parameterRepository.save(parameter);
        return parameterMapper.toDto(parameter);
    }

    /**
     * Update a parameter.
     *
     * @param parameterDTO the entity to save.
     * @return the persisted entity.
     */
    public ParameterDTO update(ParameterDTO parameterDTO) {
        LOG.debug("Request to update Parameter : {}", parameterDTO);
        Parameter parameter = parameterMapper.toEntity(parameterDTO);
        parameter = parameterRepository.save(parameter);
        return parameterMapper.toDto(parameter);
    }

    /**
     * Partially update a parameter.
     *
     * @param parameterDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ParameterDTO> partialUpdate(ParameterDTO parameterDTO) {
        LOG.debug("Request to partially update Parameter : {}", parameterDTO);

        return parameterRepository
            .findById(parameterDTO.getId())
            .map(existingParameter -> {
                parameterMapper.partialUpdate(existingParameter, parameterDTO);

                return existingParameter;
            })
            .map(parameterRepository::save)
            .map(parameterMapper::toDto);
    }

    /**
     * Get all the parameters.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ParameterDTO> findAll() {
        LOG.debug("Request to get all Parameters");
        return parameterRepository.findAll().stream().map(parameterMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one parameter by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ParameterDTO> findOne(Long id) {
        LOG.debug("Request to get Parameter : {}", id);
        return parameterRepository.findById(id).map(parameterMapper::toDto);
    }

    /**
     * Delete the parameter by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Parameter : {}", id);
        parameterRepository.deleteById(id);
    }
}
