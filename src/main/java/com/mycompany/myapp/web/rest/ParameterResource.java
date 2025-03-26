package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ParameterRepository;
import com.mycompany.myapp.service.ParameterService;
import com.mycompany.myapp.service.dto.ParameterDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Parameter}.
 */
@RestController
@RequestMapping("/api/parameters")
public class ParameterResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterResource.class);

    private static final String ENTITY_NAME = "parameter";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParameterService parameterService;

    private final ParameterRepository parameterRepository;

    public ParameterResource(ParameterService parameterService, ParameterRepository parameterRepository) {
        this.parameterService = parameterService;
        this.parameterRepository = parameterRepository;
    }

    /**
     * {@code POST  /parameters} : Create a new parameter.
     *
     * @param parameterDTO the parameterDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parameterDTO, or with status {@code 400 (Bad Request)} if the parameter has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ParameterDTO> createParameter(@RequestBody ParameterDTO parameterDTO) throws URISyntaxException {
        LOG.debug("REST request to save Parameter : {}", parameterDTO);
        if (parameterDTO.getId() != null) {
            throw new BadRequestAlertException("A new parameter cannot already have an ID", ENTITY_NAME, "idexists");
        }
        parameterDTO = parameterService.save(parameterDTO);
        return ResponseEntity.created(new URI("/api/parameters/" + parameterDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, parameterDTO.getId().toString()))
            .body(parameterDTO);
    }

    /**
     * {@code PUT  /parameters/:id} : Updates an existing parameter.
     *
     * @param id the id of the parameterDTO to save.
     * @param parameterDTO the parameterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parameterDTO,
     * or with status {@code 400 (Bad Request)} if the parameterDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parameterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParameterDTO> updateParameter(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParameterDTO parameterDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Parameter : {}, {}", id, parameterDTO);
        if (parameterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parameterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parameterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        parameterDTO = parameterService.update(parameterDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parameterDTO.getId().toString()))
            .body(parameterDTO);
    }

    /**
     * {@code PATCH  /parameters/:id} : Partial updates given fields of an existing parameter, field will ignore if it is null
     *
     * @param id the id of the parameterDTO to save.
     * @param parameterDTO the parameterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parameterDTO,
     * or with status {@code 400 (Bad Request)} if the parameterDTO is not valid,
     * or with status {@code 404 (Not Found)} if the parameterDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the parameterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ParameterDTO> partialUpdateParameter(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParameterDTO parameterDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Parameter partially : {}, {}", id, parameterDTO);
        if (parameterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parameterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parameterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParameterDTO> result = parameterService.partialUpdate(parameterDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parameterDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /parameters} : get all the parameters.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parameters in body.
     */
    @GetMapping("")
    public List<ParameterDTO> getAllParameters() {
        LOG.debug("REST request to get all Parameters");
        return parameterService.findAll();
    }

    /**
     * {@code GET  /parameters/:id} : get the "id" parameter.
     *
     * @param id the id of the parameterDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parameterDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParameterDTO> getParameter(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Parameter : {}", id);
        Optional<ParameterDTO> parameterDTO = parameterService.findOne(id);
        return ResponseUtil.wrapOrNotFound(parameterDTO);
    }

    /**
     * {@code DELETE  /parameters/:id} : delete the "id" parameter.
     *
     * @param id the id of the parameterDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParameter(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Parameter : {}", id);
        parameterService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
