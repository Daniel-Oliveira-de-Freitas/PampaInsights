package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SearchAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.repository.SearchRepository;
import com.mycompany.myapp.service.dto.SearchDTO;
import com.mycompany.myapp.service.mapper.SearchMapper;
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
 * Integration tests for the {@link SearchResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SearchResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_FAVORITE = false;
    private static final Boolean UPDATED_FAVORITE = true;

    private static final Instant DEFAULT_CREATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/searches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private SearchMapper searchMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSearchMockMvc;

    private Search search;

    private Search insertedSearch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Search createEntity() {
        return new Search().name(DEFAULT_NAME).favorite(DEFAULT_FAVORITE).createDate(DEFAULT_CREATE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Search createUpdatedEntity() {
        return new Search().name(UPDATED_NAME).favorite(UPDATED_FAVORITE).createDate(UPDATED_CREATE_DATE);
    }

    @BeforeEach
    public void initTest() {
        search = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSearch != null) {
            searchRepository.delete(insertedSearch);
            insertedSearch = null;
        }
    }

    @Test
    @Transactional
    void createSearch() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Search
        SearchDTO searchDTO = searchMapper.toDto(search);
        var returnedSearchDTO = om.readValue(
            restSearchMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(searchDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SearchDTO.class
        );

        // Validate the Search in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSearch = searchMapper.toEntity(returnedSearchDTO);
        assertSearchUpdatableFieldsEquals(returnedSearch, getPersistedSearch(returnedSearch));

        insertedSearch = returnedSearch;
    }

    @Test
    @Transactional
    void createSearchWithExistingId() throws Exception {
        // Create the Search with an existing ID
        search.setId(1L);
        SearchDTO searchDTO = searchMapper.toDto(search);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSearchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(searchDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Search in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSearches() throws Exception {
        // Initialize the database
        insertedSearch = searchRepository.saveAndFlush(search);

        // Get all the searchList
        restSearchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(search.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].favorite").value(hasItem(DEFAULT_FAVORITE)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())));
    }

    @Test
    @Transactional
    void getSearch() throws Exception {
        // Initialize the database
        insertedSearch = searchRepository.saveAndFlush(search);

        // Get the search
        restSearchMockMvc
            .perform(get(ENTITY_API_URL_ID, search.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(search.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.favorite").value(DEFAULT_FAVORITE))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSearch() throws Exception {
        // Get the search
        restSearchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSearch() throws Exception {
        // Initialize the database
        insertedSearch = searchRepository.saveAndFlush(search);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the search
        Search updatedSearch = searchRepository.findById(search.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSearch are not directly saved in db
        em.detach(updatedSearch);
        updatedSearch.name(UPDATED_NAME).favorite(UPDATED_FAVORITE).createDate(UPDATED_CREATE_DATE);
        SearchDTO searchDTO = searchMapper.toDto(updatedSearch);

        restSearchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, searchDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(searchDTO))
            )
            .andExpect(status().isOk());

        // Validate the Search in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSearchToMatchAllProperties(updatedSearch);
    }

    @Test
    @Transactional
    void putNonExistingSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        search.setId(longCount.incrementAndGet());

        // Create the Search
        SearchDTO searchDTO = searchMapper.toDto(search);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSearchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, searchDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(searchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Search in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        search.setId(longCount.incrementAndGet());

        // Create the Search
        SearchDTO searchDTO = searchMapper.toDto(search);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSearchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(searchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Search in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        search.setId(longCount.incrementAndGet());

        // Create the Search
        SearchDTO searchDTO = searchMapper.toDto(search);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSearchMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(searchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Search in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSearchWithPatch() throws Exception {
        // Initialize the database
        insertedSearch = searchRepository.saveAndFlush(search);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the search using partial update
        Search partialUpdatedSearch = new Search();
        partialUpdatedSearch.setId(search.getId());

        partialUpdatedSearch.name(UPDATED_NAME).favorite(UPDATED_FAVORITE);

        restSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSearch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSearch))
            )
            .andExpect(status().isOk());

        // Validate the Search in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSearchUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSearch, search), getPersistedSearch(search));
    }

    @Test
    @Transactional
    void fullUpdateSearchWithPatch() throws Exception {
        // Initialize the database
        insertedSearch = searchRepository.saveAndFlush(search);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the search using partial update
        Search partialUpdatedSearch = new Search();
        partialUpdatedSearch.setId(search.getId());

        partialUpdatedSearch.name(UPDATED_NAME).favorite(UPDATED_FAVORITE).createDate(UPDATED_CREATE_DATE);

        restSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSearch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSearch))
            )
            .andExpect(status().isOk());

        // Validate the Search in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSearchUpdatableFieldsEquals(partialUpdatedSearch, getPersistedSearch(partialUpdatedSearch));
    }

    @Test
    @Transactional
    void patchNonExistingSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        search.setId(longCount.incrementAndGet());

        // Create the Search
        SearchDTO searchDTO = searchMapper.toDto(search);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, searchDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(searchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Search in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        search.setId(longCount.incrementAndGet());

        // Create the Search
        SearchDTO searchDTO = searchMapper.toDto(search);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(searchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Search in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        search.setId(longCount.incrementAndGet());

        // Create the Search
        SearchDTO searchDTO = searchMapper.toDto(search);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSearchMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(searchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Search in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSearch() throws Exception {
        // Initialize the database
        insertedSearch = searchRepository.saveAndFlush(search);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the search
        restSearchMockMvc
            .perform(delete(ENTITY_API_URL_ID, search.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return searchRepository.count();
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

    protected Search getPersistedSearch(Search search) {
        return searchRepository.findById(search.getId()).orElseThrow();
    }

    protected void assertPersistedSearchToMatchAllProperties(Search expectedSearch) {
        assertSearchAllPropertiesEquals(expectedSearch, getPersistedSearch(expectedSearch));
    }

    protected void assertPersistedSearchToMatchUpdatableProperties(Search expectedSearch) {
        assertSearchAllUpdatablePropertiesEquals(expectedSearch, getPersistedSearch(expectedSearch));
    }
}
