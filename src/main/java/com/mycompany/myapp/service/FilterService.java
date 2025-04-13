package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Filter;
import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.repository.FilterRepository;
import com.mycompany.myapp.repository.SearchRepository;
import com.mycompany.myapp.service.dto.FilterDTO;
import com.mycompany.myapp.service.mapper.FilterMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Filter}.
 */
@Service
@Transactional
public class FilterService {

    private static final Logger LOG = LoggerFactory.getLogger(FilterService.class);

    private final FilterRepository filterRepository;

    private final FilterMapper filterMapper;
    private final SearchRepository searchRepository;

    public FilterService(FilterRepository filterRepository, FilterMapper filterMapper, SearchRepository searchRepository) {
        this.filterRepository = filterRepository;
        this.filterMapper = filterMapper;
        this.searchRepository = searchRepository;
    }

    /**
     * Save a filter.
     *
     * @param filterDTO the entity to save.
     * @param searchId
     * @return the persisted entity.
     */
    public FilterDTO save(FilterDTO filterDTO, Long searchId) {
        LOG.debug("Request to save Filter : {}", filterDTO);
        Filter filter = filterMapper.toEntity(filterDTO);
        Search search = searchRepository.findById(searchId).get();
        filter.setSearch(search);
        filter = filterRepository.save(filter);
        return filterMapper.toDto(filter);
    }

    /**
     * Update a filter.
     *
     * @param filterDTO the entity to save.
     * @return the persisted entity.
     */
    public FilterDTO update(FilterDTO filterDTO) {
        LOG.debug("Request to update Filter : {}", filterDTO);
        Filter filter = filterMapper.toEntity(filterDTO);
        filter = filterRepository.save(filter);
        return filterMapper.toDto(filter);
    }

    /**
     * Partially update a filter.
     *
     * @param filterDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FilterDTO> partialUpdate(FilterDTO filterDTO) {
        LOG.debug("Request to partially update Filter : {}", filterDTO);

        return filterRepository
            .findById(filterDTO.getId())
            .map(existingFilter -> {
                filterMapper.partialUpdate(existingFilter, filterDTO);

                return existingFilter;
            })
            .map(filterRepository::save)
            .map(filterMapper::toDto);
    }

    /**
     * Get all the filters.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FilterDTO> findAll() {
        LOG.debug("Request to get all Filters");
        return filterRepository.findAll().stream().map(filterMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one filter by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FilterDTO> findOne(Long id) {
        LOG.debug("Request to get Filter : {}", id);
        return filterRepository.findById(id).map(filterMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<FilterDTO> findBySearchId(Long searchId) {
        LOG.debug("Request to get Filter by search id : {}", searchId);
        return filterRepository.findBySearchId(searchId).map(filterMapper::toDto);
    }

    /**
     * Delete the filter by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Filter : {}", id);
        filterRepository.deleteById(id);
    }
}
