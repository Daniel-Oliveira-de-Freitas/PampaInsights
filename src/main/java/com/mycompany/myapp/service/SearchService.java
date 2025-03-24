package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.repository.SearchRepository;
import com.mycompany.myapp.service.dto.SearchDTO;
import com.mycompany.myapp.service.mapper.SearchMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Search}.
 */
@Service
@Transactional
public class SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

    private final SearchRepository searchRepository;

    private final SearchMapper searchMapper;

    public SearchService(SearchRepository searchRepository, SearchMapper searchMapper) {
        this.searchRepository = searchRepository;
        this.searchMapper = searchMapper;
    }

    /**
     * Save a search.
     *
     * @param searchDTO the entity to save.
     * @return the persisted entity.
     */
    public SearchDTO save(SearchDTO searchDTO) {
        LOG.debug("Request to save Search : {}", searchDTO);
        Search search = searchMapper.toEntity(searchDTO);
        search = searchRepository.save(search);
        return searchMapper.toDto(search);
    }

    /**
     * Update a search.
     *
     * @param searchDTO the entity to save.
     * @return the persisted entity.
     */
    public SearchDTO update(SearchDTO searchDTO) {
        LOG.debug("Request to update Search : {}", searchDTO);
        Search search = searchMapper.toEntity(searchDTO);
        search = searchRepository.save(search);
        return searchMapper.toDto(search);
    }

    /**
     * Partially update a search.
     *
     * @param searchDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SearchDTO> partialUpdate(SearchDTO searchDTO) {
        LOG.debug("Request to partially update Search : {}", searchDTO);

        return searchRepository
            .findById(searchDTO.getId())
            .map(existingSearch -> {
                searchMapper.partialUpdate(existingSearch, searchDTO);

                return existingSearch;
            })
            .map(searchRepository::save)
            .map(searchMapper::toDto);
    }

    /**
     * Get all the searches.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SearchDTO> findAll() {
        LOG.debug("Request to get all Searches");
        return searchRepository.findAll().stream().map(searchMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one search by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SearchDTO> findOne(Long id) {
        LOG.debug("Request to get Search : {}", id);
        return searchRepository.findById(id).map(searchMapper::toDto);
    }

    /**
     * Delete the search by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Search : {}", id);
        searchRepository.deleteById(id);
    }
}
