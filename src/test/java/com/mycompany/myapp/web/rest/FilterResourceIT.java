package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.FilterAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Filter;
import com.mycompany.myapp.domain.enumeration.Emotions;
import com.mycompany.myapp.domain.enumeration.TypeOfChart;
import com.mycompany.myapp.domain.enumeration.Visualization;
import com.mycompany.myapp.repository.FilterRepository;
import com.mycompany.myapp.service.dto.FilterDTO;
import com.mycompany.myapp.service.mapper.FilterMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link FilterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FilterResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Visualization DEFAULT_VISUALIZATION = Visualization.ALL;
    private static final Visualization UPDATED_VISUALIZATION = Visualization.WEB;

    private static final TypeOfChart DEFAULT_TYPE_OF_CHART = TypeOfChart.PIZZA;
    private static final TypeOfChart UPDATED_TYPE_OF_CHART = TypeOfChart.COLUNAS;

    private static final Emotions DEFAULT_EMOTIONS = Emotions.ALL;
    private static final Emotions UPDATED_EMOTIONS = Emotions.POSITIVE;

    private static final String ENTITY_API_URL = "/api/filters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FilterRepository filterRepository;

    @Autowired
    private FilterMapper filterMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFilterMockMvc;

    private Filter filter;

    private Filter insertedFilter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Filter createEntity() {
        return new Filter()
            .name(DEFAULT_NAME)
            .visualization(DEFAULT_VISUALIZATION)
            .typeOfChart(DEFAULT_TYPE_OF_CHART)
            .emotions(DEFAULT_EMOTIONS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Filter createUpdatedEntity() {
        return new Filter()
            .name(UPDATED_NAME)
            .visualization(UPDATED_VISUALIZATION)
            .typeOfChart(UPDATED_TYPE_OF_CHART)
            .emotions(UPDATED_EMOTIONS);
    }

    @BeforeEach
    public void initTest() {
        filter = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFilter != null) {
            filterRepository.delete(insertedFilter);
            insertedFilter = null;
        }
    }

    @Test
    @Transactional
    void createFilter() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Filter
        FilterDTO filterDTO = filterMapper.toDto(filter);
        var returnedFilterDTO = om.readValue(
            restFilterMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(filterDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FilterDTO.class
        );

        // Validate the Filter in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFilter = filterMapper.toEntity(returnedFilterDTO);
        assertFilterUpdatableFieldsEquals(returnedFilter, getPersistedFilter(returnedFilter));

        insertedFilter = returnedFilter;
    }

    @Test
    @Transactional
    void createFilterWithExistingId() throws Exception {
        // Create the Filter with an existing ID
        filter.setId(1L);
        FilterDTO filterDTO = filterMapper.toDto(filter);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFilterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(filterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Filter in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFilters() throws Exception {
        // Initialize the database
        insertedFilter = filterRepository.saveAndFlush(filter);

        // Get all the filterList
        restFilterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(filter.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].visualization").value(hasItem(DEFAULT_VISUALIZATION.toString())))
            .andExpect(jsonPath("$.[*].typeOfChart").value(hasItem(DEFAULT_TYPE_OF_CHART.toString())))
            .andExpect(jsonPath("$.[*].emotions").value(hasItem(DEFAULT_EMOTIONS.toString())));
    }

    @Test
    @Transactional
    void getFilter() throws Exception {
        // Initialize the database
        insertedFilter = filterRepository.saveAndFlush(filter);

        // Get the filter
        restFilterMockMvc
            .perform(get(ENTITY_API_URL_ID, filter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(filter.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.visualization").value(DEFAULT_VISUALIZATION.toString()))
            .andExpect(jsonPath("$.typeOfChart").value(DEFAULT_TYPE_OF_CHART.toString()))
            .andExpect(jsonPath("$.emotions").value(DEFAULT_EMOTIONS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFilter() throws Exception {
        // Get the filter
        restFilterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFilter() throws Exception {
        // Initialize the database
        insertedFilter = filterRepository.saveAndFlush(filter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the filter
        Filter updatedFilter = filterRepository.findById(filter.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFilter are not directly saved in db
        em.detach(updatedFilter);
        updatedFilter.name(UPDATED_NAME).visualization(UPDATED_VISUALIZATION).typeOfChart(UPDATED_TYPE_OF_CHART).emotions(UPDATED_EMOTIONS);
        FilterDTO filterDTO = filterMapper.toDto(updatedFilter);

        restFilterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, filterDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(filterDTO))
            )
            .andExpect(status().isOk());

        // Validate the Filter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFilterToMatchAllProperties(updatedFilter);
    }

    @Test
    @Transactional
    void putNonExistingFilter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        filter.setId(longCount.incrementAndGet());

        // Create the Filter
        FilterDTO filterDTO = filterMapper.toDto(filter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFilterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, filterDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(filterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Filter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFilter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        filter.setId(longCount.incrementAndGet());

        // Create the Filter
        FilterDTO filterDTO = filterMapper.toDto(filter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(filterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Filter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFilter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        filter.setId(longCount.incrementAndGet());

        // Create the Filter
        FilterDTO filterDTO = filterMapper.toDto(filter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilterMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(filterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Filter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFilterWithPatch() throws Exception {
        // Initialize the database
        insertedFilter = filterRepository.saveAndFlush(filter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the filter using partial update
        Filter partialUpdatedFilter = new Filter();
        partialUpdatedFilter.setId(filter.getId());

        partialUpdatedFilter.name(UPDATED_NAME).visualization(UPDATED_VISUALIZATION).emotions(UPDATED_EMOTIONS);

        restFilterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFilter.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFilter))
            )
            .andExpect(status().isOk());

        // Validate the Filter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFilterUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFilter, filter), getPersistedFilter(filter));
    }

    @Test
    @Transactional
    void fullUpdateFilterWithPatch() throws Exception {
        // Initialize the database
        insertedFilter = filterRepository.saveAndFlush(filter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the filter using partial update
        Filter partialUpdatedFilter = new Filter();
        partialUpdatedFilter.setId(filter.getId());

        partialUpdatedFilter
            .name(UPDATED_NAME)
            .visualization(UPDATED_VISUALIZATION)
            .typeOfChart(UPDATED_TYPE_OF_CHART)
            .emotions(UPDATED_EMOTIONS);

        restFilterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFilter.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFilter))
            )
            .andExpect(status().isOk());

        // Validate the Filter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFilterUpdatableFieldsEquals(partialUpdatedFilter, getPersistedFilter(partialUpdatedFilter));
    }

    @Test
    @Transactional
    void patchNonExistingFilter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        filter.setId(longCount.incrementAndGet());

        // Create the Filter
        FilterDTO filterDTO = filterMapper.toDto(filter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFilterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, filterDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(filterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Filter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFilter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        filter.setId(longCount.incrementAndGet());

        // Create the Filter
        FilterDTO filterDTO = filterMapper.toDto(filter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(filterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Filter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFilter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        filter.setId(longCount.incrementAndGet());

        // Create the Filter
        FilterDTO filterDTO = filterMapper.toDto(filter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFilterMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(filterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Filter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFilter() throws Exception {
        // Initialize the database
        insertedFilter = filterRepository.saveAndFlush(filter);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the filter
        restFilterMockMvc
            .perform(delete(ENTITY_API_URL_ID, filter.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return filterRepository.count();
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

    protected Filter getPersistedFilter(Filter filter) {
        return filterRepository.findById(filter.getId()).orElseThrow();
    }

    protected void assertPersistedFilterToMatchAllProperties(Filter expectedFilter) {
        assertFilterAllPropertiesEquals(expectedFilter, getPersistedFilter(expectedFilter));
    }

    protected void assertPersistedFilterToMatchUpdatableProperties(Filter expectedFilter) {
        assertFilterAllUpdatablePropertiesEquals(expectedFilter, getPersistedFilter(expectedFilter));
    }
}
