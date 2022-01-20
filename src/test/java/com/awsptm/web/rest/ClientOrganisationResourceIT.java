package com.awsptm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.ClientOrganisation;
import com.awsptm.domain.ClientUser;
import com.awsptm.repository.ClientOrganisationRepository;
import com.awsptm.service.criteria.ClientOrganisationCriteria;
import com.awsptm.service.dto.ClientOrganisationDTO;
import com.awsptm.service.mapper.ClientOrganisationMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClientOrganisationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientOrganisationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DOMAIN = "AAAAAAAAAA";
    private static final String UPDATED_DOMAIN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-organisations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientOrganisationRepository clientOrganisationRepository;

    @Autowired
    private ClientOrganisationMapper clientOrganisationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientOrganisationMockMvc;

    private ClientOrganisation clientOrganisation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientOrganisation createEntity(EntityManager em) {
        ClientOrganisation clientOrganisation = new ClientOrganisation().name(DEFAULT_NAME).domain(DEFAULT_DOMAIN);
        return clientOrganisation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientOrganisation createUpdatedEntity(EntityManager em) {
        ClientOrganisation clientOrganisation = new ClientOrganisation().name(UPDATED_NAME).domain(UPDATED_DOMAIN);
        return clientOrganisation;
    }

    @BeforeEach
    public void initTest() {
        clientOrganisation = createEntity(em);
    }

    @Test
    @Transactional
    void createClientOrganisation() throws Exception {
        int databaseSizeBeforeCreate = clientOrganisationRepository.findAll().size();
        // Create the ClientOrganisation
        ClientOrganisationDTO clientOrganisationDTO = clientOrganisationMapper.toDto(clientOrganisation);
        restClientOrganisationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientOrganisationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeCreate + 1);
        ClientOrganisation testClientOrganisation = clientOrganisationList.get(clientOrganisationList.size() - 1);
        assertThat(testClientOrganisation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClientOrganisation.getDomain()).isEqualTo(DEFAULT_DOMAIN);
    }

    @Test
    @Transactional
    void createClientOrganisationWithExistingId() throws Exception {
        // Create the ClientOrganisation with an existing ID
        clientOrganisation.setId(1L);
        ClientOrganisationDTO clientOrganisationDTO = clientOrganisationMapper.toDto(clientOrganisation);

        int databaseSizeBeforeCreate = clientOrganisationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientOrganisationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientOrganisationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClientOrganisations() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList
        restClientOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientOrganisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN)));
    }

    @Test
    @Transactional
    void getClientOrganisation() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get the clientOrganisation
        restClientOrganisationMockMvc
            .perform(get(ENTITY_API_URL_ID, clientOrganisation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientOrganisation.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN));
    }

    @Test
    @Transactional
    void getClientOrganisationsByIdFiltering() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        Long id = clientOrganisation.getId();

        defaultClientOrganisationShouldBeFound("id.equals=" + id);
        defaultClientOrganisationShouldNotBeFound("id.notEquals=" + id);

        defaultClientOrganisationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultClientOrganisationShouldNotBeFound("id.greaterThan=" + id);

        defaultClientOrganisationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultClientOrganisationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where name equals to DEFAULT_NAME
        defaultClientOrganisationShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the clientOrganisationList where name equals to UPDATED_NAME
        defaultClientOrganisationShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where name not equals to DEFAULT_NAME
        defaultClientOrganisationShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the clientOrganisationList where name not equals to UPDATED_NAME
        defaultClientOrganisationShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where name in DEFAULT_NAME or UPDATED_NAME
        defaultClientOrganisationShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the clientOrganisationList where name equals to UPDATED_NAME
        defaultClientOrganisationShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where name is not null
        defaultClientOrganisationShouldBeFound("name.specified=true");

        // Get all the clientOrganisationList where name is null
        defaultClientOrganisationShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByNameContainsSomething() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where name contains DEFAULT_NAME
        defaultClientOrganisationShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the clientOrganisationList where name contains UPDATED_NAME
        defaultClientOrganisationShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where name does not contain DEFAULT_NAME
        defaultClientOrganisationShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the clientOrganisationList where name does not contain UPDATED_NAME
        defaultClientOrganisationShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByDomainIsEqualToSomething() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where domain equals to DEFAULT_DOMAIN
        defaultClientOrganisationShouldBeFound("domain.equals=" + DEFAULT_DOMAIN);

        // Get all the clientOrganisationList where domain equals to UPDATED_DOMAIN
        defaultClientOrganisationShouldNotBeFound("domain.equals=" + UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByDomainIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where domain not equals to DEFAULT_DOMAIN
        defaultClientOrganisationShouldNotBeFound("domain.notEquals=" + DEFAULT_DOMAIN);

        // Get all the clientOrganisationList where domain not equals to UPDATED_DOMAIN
        defaultClientOrganisationShouldBeFound("domain.notEquals=" + UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByDomainIsInShouldWork() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where domain in DEFAULT_DOMAIN or UPDATED_DOMAIN
        defaultClientOrganisationShouldBeFound("domain.in=" + DEFAULT_DOMAIN + "," + UPDATED_DOMAIN);

        // Get all the clientOrganisationList where domain equals to UPDATED_DOMAIN
        defaultClientOrganisationShouldNotBeFound("domain.in=" + UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByDomainIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where domain is not null
        defaultClientOrganisationShouldBeFound("domain.specified=true");

        // Get all the clientOrganisationList where domain is null
        defaultClientOrganisationShouldNotBeFound("domain.specified=false");
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByDomainContainsSomething() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where domain contains DEFAULT_DOMAIN
        defaultClientOrganisationShouldBeFound("domain.contains=" + DEFAULT_DOMAIN);

        // Get all the clientOrganisationList where domain contains UPDATED_DOMAIN
        defaultClientOrganisationShouldNotBeFound("domain.contains=" + UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByDomainNotContainsSomething() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        // Get all the clientOrganisationList where domain does not contain DEFAULT_DOMAIN
        defaultClientOrganisationShouldNotBeFound("domain.doesNotContain=" + DEFAULT_DOMAIN);

        // Get all the clientOrganisationList where domain does not contain UPDATED_DOMAIN
        defaultClientOrganisationShouldBeFound("domain.doesNotContain=" + UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    void getAllClientOrganisationsByClientUsersIsEqualToSomething() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);
        ClientUser clientUsers;
        if (TestUtil.findAll(em, ClientUser.class).isEmpty()) {
            clientUsers = ClientUserResourceIT.createEntity(em);
            em.persist(clientUsers);
            em.flush();
        } else {
            clientUsers = TestUtil.findAll(em, ClientUser.class).get(0);
        }
        em.persist(clientUsers);
        em.flush();
        clientOrganisation.addClientUsers(clientUsers);
        clientOrganisationRepository.saveAndFlush(clientOrganisation);
        Long clientUsersId = clientUsers.getId();

        // Get all the clientOrganisationList where clientUsers equals to clientUsersId
        defaultClientOrganisationShouldBeFound("clientUsersId.equals=" + clientUsersId);

        // Get all the clientOrganisationList where clientUsers equals to (clientUsersId + 1)
        defaultClientOrganisationShouldNotBeFound("clientUsersId.equals=" + (clientUsersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClientOrganisationShouldBeFound(String filter) throws Exception {
        restClientOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientOrganisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN)));

        // Check, that the count call also returns 1
        restClientOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClientOrganisationShouldNotBeFound(String filter) throws Exception {
        restClientOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClientOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClientOrganisation() throws Exception {
        // Get the clientOrganisation
        restClientOrganisationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewClientOrganisation() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        int databaseSizeBeforeUpdate = clientOrganisationRepository.findAll().size();

        // Update the clientOrganisation
        ClientOrganisation updatedClientOrganisation = clientOrganisationRepository.findById(clientOrganisation.getId()).get();
        // Disconnect from session so that the updates on updatedClientOrganisation are not directly saved in db
        em.detach(updatedClientOrganisation);
        updatedClientOrganisation.name(UPDATED_NAME).domain(UPDATED_DOMAIN);
        ClientOrganisationDTO clientOrganisationDTO = clientOrganisationMapper.toDto(updatedClientOrganisation);

        restClientOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientOrganisationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientOrganisationDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeUpdate);
        ClientOrganisation testClientOrganisation = clientOrganisationList.get(clientOrganisationList.size() - 1);
        assertThat(testClientOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClientOrganisation.getDomain()).isEqualTo(UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    void putNonExistingClientOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = clientOrganisationRepository.findAll().size();
        clientOrganisation.setId(count.incrementAndGet());

        // Create the ClientOrganisation
        ClientOrganisationDTO clientOrganisationDTO = clientOrganisationMapper.toDto(clientOrganisation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientOrganisationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientOrganisationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = clientOrganisationRepository.findAll().size();
        clientOrganisation.setId(count.incrementAndGet());

        // Create the ClientOrganisation
        ClientOrganisationDTO clientOrganisationDTO = clientOrganisationMapper.toDto(clientOrganisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientOrganisationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = clientOrganisationRepository.findAll().size();
        clientOrganisation.setId(count.incrementAndGet());

        // Create the ClientOrganisation
        ClientOrganisationDTO clientOrganisationDTO = clientOrganisationMapper.toDto(clientOrganisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientOrganisationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientOrganisationWithPatch() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        int databaseSizeBeforeUpdate = clientOrganisationRepository.findAll().size();

        // Update the clientOrganisation using partial update
        ClientOrganisation partialUpdatedClientOrganisation = new ClientOrganisation();
        partialUpdatedClientOrganisation.setId(clientOrganisation.getId());

        partialUpdatedClientOrganisation.name(UPDATED_NAME).domain(UPDATED_DOMAIN);

        restClientOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientOrganisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeUpdate);
        ClientOrganisation testClientOrganisation = clientOrganisationList.get(clientOrganisationList.size() - 1);
        assertThat(testClientOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClientOrganisation.getDomain()).isEqualTo(UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    void fullUpdateClientOrganisationWithPatch() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        int databaseSizeBeforeUpdate = clientOrganisationRepository.findAll().size();

        // Update the clientOrganisation using partial update
        ClientOrganisation partialUpdatedClientOrganisation = new ClientOrganisation();
        partialUpdatedClientOrganisation.setId(clientOrganisation.getId());

        partialUpdatedClientOrganisation.name(UPDATED_NAME).domain(UPDATED_DOMAIN);

        restClientOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientOrganisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeUpdate);
        ClientOrganisation testClientOrganisation = clientOrganisationList.get(clientOrganisationList.size() - 1);
        assertThat(testClientOrganisation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClientOrganisation.getDomain()).isEqualTo(UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    void patchNonExistingClientOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = clientOrganisationRepository.findAll().size();
        clientOrganisation.setId(count.incrementAndGet());

        // Create the ClientOrganisation
        ClientOrganisationDTO clientOrganisationDTO = clientOrganisationMapper.toDto(clientOrganisation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientOrganisationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientOrganisationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = clientOrganisationRepository.findAll().size();
        clientOrganisation.setId(count.incrementAndGet());

        // Create the ClientOrganisation
        ClientOrganisationDTO clientOrganisationDTO = clientOrganisationMapper.toDto(clientOrganisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientOrganisationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = clientOrganisationRepository.findAll().size();
        clientOrganisation.setId(count.incrementAndGet());

        // Create the ClientOrganisation
        ClientOrganisationDTO clientOrganisationDTO = clientOrganisationMapper.toDto(clientOrganisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientOrganisationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientOrganisation in the database
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientOrganisation() throws Exception {
        // Initialize the database
        clientOrganisationRepository.saveAndFlush(clientOrganisation);

        int databaseSizeBeforeDelete = clientOrganisationRepository.findAll().size();

        // Delete the clientOrganisation
        restClientOrganisationMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientOrganisation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClientOrganisation> clientOrganisationList = clientOrganisationRepository.findAll();
        assertThat(clientOrganisationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
