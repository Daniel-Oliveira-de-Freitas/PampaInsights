package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ParameterAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Parameter;
import com.mycompany.myapp.repository.ParameterRepository;
import com.mycompany.myapp.service.dto.ParameterDTO;
import com.mycompany.myapp.service.mapper.ParameterMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ParameterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParameterResourceIT {

    private static final String DEFAULT_TERMS = "AAAAAAAAAA";
    private static final String UPDATED_TERMS = "BBBBBBBBBB";

    private static final String DEFAULT_WEB_SITE = "AAAAAAAAAA";
    private static final String UPDATED_WEB_SITE = "BBBBBBBBBB";

    private static final String DEFAULT_INSTAGRAM = "AAAAAAAAAA";
    private static final String UPDATED_INSTAGRAM = "BBBBBBBBBB";

    private static final String DEFAULT_FACEBOOK = "AAAAAAAAAA";
    private static final String UPDATED_FACEBOOK = "BBBBBBBBBB";

    private static final String DEFAULT_LINKEDIN = "AAAAAAAAAA";
    private static final String UPDATED_LINKEDIN = "BBBBBBBBBB";

    private static final String DEFAULT_X = "AAAAAAAAAA";
    private static final String UPDATED_X = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/parameters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private ParameterMapper parameterMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParameterMockMvc;

    private Parameter parameter;

    private Parameter insertedParameter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parameter createEntity() {
        return new Parameter()
            .terms(DEFAULT_TERMS)
            .webSite(DEFAULT_WEB_SITE)
            .instagram(DEFAULT_INSTAGRAM)
            .facebook(DEFAULT_FACEBOOK)
            .linkedin(DEFAULT_LINKEDIN)
            .x(DEFAULT_X)
            .createDate(DEFAULT_CREATE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parameter createUpdatedEntity() {
        return new Parameter()
            .terms(UPDATED_TERMS)
            .webSite(UPDATED_WEB_SITE)
            .instagram(UPDATED_INSTAGRAM)
            .facebook(UPDATED_FACEBOOK)
            .linkedin(UPDATED_LINKEDIN)
            .x(UPDATED_X)
            .createDate(UPDATED_CREATE_DATE);
    }

    @BeforeEach
    public void initTest() {
        parameter = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedParameter != null) {
            parameterRepository.delete(insertedParameter);
            insertedParameter = null;
        }
    }

    @Test
    @Transactional
    void createParameter() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Parameter
        ParameterDTO parameterDTO = parameterMapper.toDto(parameter);
        var returnedParameterDTO = om.readValue(
            restParameterMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parameterDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParameterDTO.class
        );

        // Validate the Parameter in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParameter = parameterMapper.toEntity(returnedParameterDTO);
        assertParameterUpdatableFieldsEquals(returnedParameter, getPersistedParameter(returnedParameter));

        insertedParameter = returnedParameter;
    }

    @Test
    @Transactional
    void createParameterWithExistingId() throws Exception {
        // Create the Parameter with an existing ID
        parameter.setId(1L);
        ParameterDTO parameterDTO = parameterMapper.toDto(parameter);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParameterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parameterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllParameters() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get all the parameterList
        restParameterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parameter.getId().intValue())))
            .andExpect(jsonPath("$.[*].terms").value(hasItem(DEFAULT_TERMS)))
            .andExpect(jsonPath("$.[*].webSite").value(hasItem(DEFAULT_WEB_SITE)))
            .andExpect(jsonPath("$.[*].instagram").value(hasItem(DEFAULT_INSTAGRAM)))
            .andExpect(jsonPath("$.[*].facebook").value(hasItem(DEFAULT_FACEBOOK)))
            .andExpect(jsonPath("$.[*].linkedin").value(hasItem(DEFAULT_LINKEDIN)))
            .andExpect(jsonPath("$.[*].x").value(hasItem(DEFAULT_X)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())));
    }

    @Test
    @Transactional
    void getParameter() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        // Get the parameter
        restParameterMockMvc
            .perform(get(ENTITY_API_URL_ID, parameter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parameter.getId().intValue()))
            .andExpect(jsonPath("$.terms").value(DEFAULT_TERMS))
            .andExpect(jsonPath("$.webSite").value(DEFAULT_WEB_SITE))
            .andExpect(jsonPath("$.instagram").value(DEFAULT_INSTAGRAM))
            .andExpect(jsonPath("$.facebook").value(DEFAULT_FACEBOOK))
            .andExpect(jsonPath("$.linkedin").value(DEFAULT_LINKEDIN))
            .andExpect(jsonPath("$.x").value(DEFAULT_X))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingParameter() throws Exception {
        // Get the parameter
        restParameterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParameter() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parameter
        Parameter updatedParameter = parameterRepository.findById(parameter.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParameter are not directly saved in db
        em.detach(updatedParameter);
        updatedParameter
            .terms(UPDATED_TERMS)
            .webSite(UPDATED_WEB_SITE)
            .instagram(UPDATED_INSTAGRAM)
            .facebook(UPDATED_FACEBOOK)
            .linkedin(UPDATED_LINKEDIN)
            .x(UPDATED_X)
            .createDate(UPDATED_CREATE_DATE);
        ParameterDTO parameterDTO = parameterMapper.toDto(updatedParameter);

        restParameterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parameterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parameterDTO))
            )
            .andExpect(status().isOk());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParameterToMatchAllProperties(updatedParameter);
    }

    @Test
    @Transactional
    void putNonExistingParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // Create the Parameter
        ParameterDTO parameterDTO = parameterMapper.toDto(parameter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parameterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parameterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // Create the Parameter
        ParameterDTO parameterDTO = parameterMapper.toDto(parameter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parameterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // Create the Parameter
        ParameterDTO parameterDTO = parameterMapper.toDto(parameter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parameterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParameterWithPatch() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parameter using partial update
        Parameter partialUpdatedParameter = new Parameter();
        partialUpdatedParameter.setId(parameter.getId());

        partialUpdatedParameter.terms(UPDATED_TERMS).facebook(UPDATED_FACEBOOK).createDate(UPDATED_CREATE_DATE);

        restParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParameter.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParameter))
            )
            .andExpect(status().isOk());

        // Validate the Parameter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParameterUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParameter, parameter),
            getPersistedParameter(parameter)
        );
    }

    @Test
    @Transactional
    void fullUpdateParameterWithPatch() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parameter using partial update
        Parameter partialUpdatedParameter = new Parameter();
        partialUpdatedParameter.setId(parameter.getId());

        partialUpdatedParameter
            .terms(UPDATED_TERMS)
            .webSite(UPDATED_WEB_SITE)
            .instagram(UPDATED_INSTAGRAM)
            .facebook(UPDATED_FACEBOOK)
            .linkedin(UPDATED_LINKEDIN)
            .x(UPDATED_X)
            .createDate(UPDATED_CREATE_DATE);

        restParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParameter.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParameter))
            )
            .andExpect(status().isOk());

        // Validate the Parameter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParameterUpdatableFieldsEquals(partialUpdatedParameter, getPersistedParameter(partialUpdatedParameter));
    }

    @Test
    @Transactional
    void patchNonExistingParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // Create the Parameter
        ParameterDTO parameterDTO = parameterMapper.toDto(parameter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parameterDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parameterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // Create the Parameter
        ParameterDTO parameterDTO = parameterMapper.toDto(parameter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parameterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParameter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parameter.setId(longCount.incrementAndGet());

        // Create the Parameter
        ParameterDTO parameterDTO = parameterMapper.toDto(parameter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParameterMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(parameterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parameter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParameter() throws Exception {
        // Initialize the database
        insertedParameter = parameterRepository.saveAndFlush(parameter);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the parameter
        restParameterMockMvc
            .perform(delete(ENTITY_API_URL_ID, parameter.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return parameterRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Parameter getPersistedParameter(Parameter parameter) {
        return parameterRepository.findById(parameter.getId()).orElseThrow();
    }

    protected void assertPersistedParameterToMatchAllProperties(Parameter expectedParameter) {
        assertParameterAllPropertiesEquals(expectedParameter, getPersistedParameter(expectedParameter));
    }

    protected void assertPersistedParameterToMatchUpdatableProperties(Parameter expectedParameter) {
        assertParameterAllUpdatablePropertiesEquals(expectedParameter, getPersistedParameter(expectedParameter));
    }
}
