package com.awsptm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.ClientOrganisation;
import com.awsptm.domain.ClientUser;
import com.awsptm.domain.Inspection;
import com.awsptm.domain.Job;
import com.awsptm.domain.User;
import com.awsptm.repository.ClientUserRepository;
import com.awsptm.service.criteria.ClientUserCriteria;
import com.awsptm.service.dto.ClientUserDTO;
import com.awsptm.service.mapper.ClientUserMapper;
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
 * Integration tests for the {@link ClientUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientUserResourceIT {

    private static final String DEFAULT_LANDLINE = "AAAAAAAAAA";
    private static final String UPDATED_LANDLINE = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientUserRepository clientUserRepository;

    @Autowired
    private ClientUserMapper clientUserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientUserMockMvc;

    private ClientUser clientUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientUser createEntity(EntityManager em) {
        ClientUser clientUser = new ClientUser().landline(DEFAULT_LANDLINE).mobile(DEFAULT_MOBILE);
        return clientUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientUser createUpdatedEntity(EntityManager em) {
        ClientUser clientUser = new ClientUser().landline(UPDATED_LANDLINE).mobile(UPDATED_MOBILE);
        return clientUser;
    }

    @BeforeEach
    public void initTest() {
        clientUser = createEntity(em);
    }

    @Test
    @Transactional
    void createClientUser() throws Exception {
        int databaseSizeBeforeCreate = clientUserRepository.findAll().size();
        // Create the ClientUser
        ClientUserDTO clientUserDTO = clientUserMapper.toDto(clientUser);
        restClientUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientUserDTO)))
            .andExpect(status().isCreated());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeCreate + 1);
        ClientUser testClientUser = clientUserList.get(clientUserList.size() - 1);
        assertThat(testClientUser.getLandline()).isEqualTo(DEFAULT_LANDLINE);
        assertThat(testClientUser.getMobile()).isEqualTo(DEFAULT_MOBILE);
    }

    @Test
    @Transactional
    void createClientUserWithExistingId() throws Exception {
        // Create the ClientUser with an existing ID
        clientUser.setId(1L);
        ClientUserDTO clientUserDTO = clientUserMapper.toDto(clientUser);

        int databaseSizeBeforeCreate = clientUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClientUsers() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList
        restClientUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].landline").value(hasItem(DEFAULT_LANDLINE)))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE)));
    }

    @Test
    @Transactional
    void getClientUser() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get the clientUser
        restClientUserMockMvc
            .perform(get(ENTITY_API_URL_ID, clientUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientUser.getId().intValue()))
            .andExpect(jsonPath("$.landline").value(DEFAULT_LANDLINE))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE));
    }

    @Test
    @Transactional
    void getClientUsersByIdFiltering() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        Long id = clientUser.getId();

        defaultClientUserShouldBeFound("id.equals=" + id);
        defaultClientUserShouldNotBeFound("id.notEquals=" + id);

        defaultClientUserShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultClientUserShouldNotBeFound("id.greaterThan=" + id);

        defaultClientUserShouldBeFound("id.lessThanOrEqual=" + id);
        defaultClientUserShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClientUsersByLandlineIsEqualToSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where landline equals to DEFAULT_LANDLINE
        defaultClientUserShouldBeFound("landline.equals=" + DEFAULT_LANDLINE);

        // Get all the clientUserList where landline equals to UPDATED_LANDLINE
        defaultClientUserShouldNotBeFound("landline.equals=" + UPDATED_LANDLINE);
    }

    @Test
    @Transactional
    void getAllClientUsersByLandlineIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where landline not equals to DEFAULT_LANDLINE
        defaultClientUserShouldNotBeFound("landline.notEquals=" + DEFAULT_LANDLINE);

        // Get all the clientUserList where landline not equals to UPDATED_LANDLINE
        defaultClientUserShouldBeFound("landline.notEquals=" + UPDATED_LANDLINE);
    }

    @Test
    @Transactional
    void getAllClientUsersByLandlineIsInShouldWork() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where landline in DEFAULT_LANDLINE or UPDATED_LANDLINE
        defaultClientUserShouldBeFound("landline.in=" + DEFAULT_LANDLINE + "," + UPDATED_LANDLINE);

        // Get all the clientUserList where landline equals to UPDATED_LANDLINE
        defaultClientUserShouldNotBeFound("landline.in=" + UPDATED_LANDLINE);
    }

    @Test
    @Transactional
    void getAllClientUsersByLandlineIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where landline is not null
        defaultClientUserShouldBeFound("landline.specified=true");

        // Get all the clientUserList where landline is null
        defaultClientUserShouldNotBeFound("landline.specified=false");
    }

    @Test
    @Transactional
    void getAllClientUsersByLandlineContainsSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where landline contains DEFAULT_LANDLINE
        defaultClientUserShouldBeFound("landline.contains=" + DEFAULT_LANDLINE);

        // Get all the clientUserList where landline contains UPDATED_LANDLINE
        defaultClientUserShouldNotBeFound("landline.contains=" + UPDATED_LANDLINE);
    }

    @Test
    @Transactional
    void getAllClientUsersByLandlineNotContainsSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where landline does not contain DEFAULT_LANDLINE
        defaultClientUserShouldNotBeFound("landline.doesNotContain=" + DEFAULT_LANDLINE);

        // Get all the clientUserList where landline does not contain UPDATED_LANDLINE
        defaultClientUserShouldBeFound("landline.doesNotContain=" + UPDATED_LANDLINE);
    }

    @Test
    @Transactional
    void getAllClientUsersByMobileIsEqualToSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where mobile equals to DEFAULT_MOBILE
        defaultClientUserShouldBeFound("mobile.equals=" + DEFAULT_MOBILE);

        // Get all the clientUserList where mobile equals to UPDATED_MOBILE
        defaultClientUserShouldNotBeFound("mobile.equals=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void getAllClientUsersByMobileIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where mobile not equals to DEFAULT_MOBILE
        defaultClientUserShouldNotBeFound("mobile.notEquals=" + DEFAULT_MOBILE);

        // Get all the clientUserList where mobile not equals to UPDATED_MOBILE
        defaultClientUserShouldBeFound("mobile.notEquals=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void getAllClientUsersByMobileIsInShouldWork() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where mobile in DEFAULT_MOBILE or UPDATED_MOBILE
        defaultClientUserShouldBeFound("mobile.in=" + DEFAULT_MOBILE + "," + UPDATED_MOBILE);

        // Get all the clientUserList where mobile equals to UPDATED_MOBILE
        defaultClientUserShouldNotBeFound("mobile.in=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void getAllClientUsersByMobileIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where mobile is not null
        defaultClientUserShouldBeFound("mobile.specified=true");

        // Get all the clientUserList where mobile is null
        defaultClientUserShouldNotBeFound("mobile.specified=false");
    }

    @Test
    @Transactional
    void getAllClientUsersByMobileContainsSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where mobile contains DEFAULT_MOBILE
        defaultClientUserShouldBeFound("mobile.contains=" + DEFAULT_MOBILE);

        // Get all the clientUserList where mobile contains UPDATED_MOBILE
        defaultClientUserShouldNotBeFound("mobile.contains=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void getAllClientUsersByMobileNotContainsSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        // Get all the clientUserList where mobile does not contain DEFAULT_MOBILE
        defaultClientUserShouldNotBeFound("mobile.doesNotContain=" + DEFAULT_MOBILE);

        // Get all the clientUserList where mobile does not contain UPDATED_MOBILE
        defaultClientUserShouldBeFound("mobile.doesNotContain=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void getAllClientUsersByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            user = UserResourceIT.createEntity(em);
            em.persist(user);
            em.flush();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        clientUser.setUser(user);
        clientUserRepository.saveAndFlush(clientUser);
        Long userId = user.getId();

        // Get all the clientUserList where user equals to userId
        defaultClientUserShouldBeFound("userId.equals=" + userId);

        // Get all the clientUserList where user equals to (userId + 1)
        defaultClientUserShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllClientUsersByJobsIsEqualToSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);
        Job jobs;
        if (TestUtil.findAll(em, Job.class).isEmpty()) {
            jobs = JobResourceIT.createEntity(em);
            em.persist(jobs);
            em.flush();
        } else {
            jobs = TestUtil.findAll(em, Job.class).get(0);
        }
        em.persist(jobs);
        em.flush();
        clientUser.addJobs(jobs);
        clientUserRepository.saveAndFlush(clientUser);
        Long jobsId = jobs.getId();

        // Get all the clientUserList where jobs equals to jobsId
        defaultClientUserShouldBeFound("jobsId.equals=" + jobsId);

        // Get all the clientUserList where jobs equals to (jobsId + 1)
        defaultClientUserShouldNotBeFound("jobsId.equals=" + (jobsId + 1));
    }

    @Test
    @Transactional
    void getAllClientUsersByInspectionsIsEqualToSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);
        Inspection inspections;
        if (TestUtil.findAll(em, Inspection.class).isEmpty()) {
            inspections = InspectionResourceIT.createEntity(em);
            em.persist(inspections);
            em.flush();
        } else {
            inspections = TestUtil.findAll(em, Inspection.class).get(0);
        }
        em.persist(inspections);
        em.flush();
        clientUser.addInspections(inspections);
        clientUserRepository.saveAndFlush(clientUser);
        Long inspectionsId = inspections.getId();

        // Get all the clientUserList where inspections equals to inspectionsId
        defaultClientUserShouldBeFound("inspectionsId.equals=" + inspectionsId);

        // Get all the clientUserList where inspections equals to (inspectionsId + 1)
        defaultClientUserShouldNotBeFound("inspectionsId.equals=" + (inspectionsId + 1));
    }

    @Test
    @Transactional
    void getAllClientUsersByClientIsEqualToSomething() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);
        ClientOrganisation client;
        if (TestUtil.findAll(em, ClientOrganisation.class).isEmpty()) {
            client = ClientOrganisationResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, ClientOrganisation.class).get(0);
        }
        em.persist(client);
        em.flush();
        clientUser.setClient(client);
        clientUserRepository.saveAndFlush(clientUser);
        Long clientId = client.getId();

        // Get all the clientUserList where client equals to clientId
        defaultClientUserShouldBeFound("clientId.equals=" + clientId);

        // Get all the clientUserList where client equals to (clientId + 1)
        defaultClientUserShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClientUserShouldBeFound(String filter) throws Exception {
        restClientUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].landline").value(hasItem(DEFAULT_LANDLINE)))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE)));

        // Check, that the count call also returns 1
        restClientUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClientUserShouldNotBeFound(String filter) throws Exception {
        restClientUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClientUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClientUser() throws Exception {
        // Get the clientUser
        restClientUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewClientUser() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        int databaseSizeBeforeUpdate = clientUserRepository.findAll().size();

        // Update the clientUser
        ClientUser updatedClientUser = clientUserRepository.findById(clientUser.getId()).get();
        // Disconnect from session so that the updates on updatedClientUser are not directly saved in db
        em.detach(updatedClientUser);
        updatedClientUser.landline(UPDATED_LANDLINE).mobile(UPDATED_MOBILE);
        ClientUserDTO clientUserDTO = clientUserMapper.toDto(updatedClientUser);

        restClientUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeUpdate);
        ClientUser testClientUser = clientUserList.get(clientUserList.size() - 1);
        assertThat(testClientUser.getLandline()).isEqualTo(UPDATED_LANDLINE);
        assertThat(testClientUser.getMobile()).isEqualTo(UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void putNonExistingClientUser() throws Exception {
        int databaseSizeBeforeUpdate = clientUserRepository.findAll().size();
        clientUser.setId(count.incrementAndGet());

        // Create the ClientUser
        ClientUserDTO clientUserDTO = clientUserMapper.toDto(clientUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientUser() throws Exception {
        int databaseSizeBeforeUpdate = clientUserRepository.findAll().size();
        clientUser.setId(count.incrementAndGet());

        // Create the ClientUser
        ClientUserDTO clientUserDTO = clientUserMapper.toDto(clientUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientUser() throws Exception {
        int databaseSizeBeforeUpdate = clientUserRepository.findAll().size();
        clientUser.setId(count.incrementAndGet());

        // Create the ClientUser
        ClientUserDTO clientUserDTO = clientUserMapper.toDto(clientUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientUserWithPatch() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        int databaseSizeBeforeUpdate = clientUserRepository.findAll().size();

        // Update the clientUser using partial update
        ClientUser partialUpdatedClientUser = new ClientUser();
        partialUpdatedClientUser.setId(clientUser.getId());

        partialUpdatedClientUser.mobile(UPDATED_MOBILE);

        restClientUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientUser))
            )
            .andExpect(status().isOk());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeUpdate);
        ClientUser testClientUser = clientUserList.get(clientUserList.size() - 1);
        assertThat(testClientUser.getLandline()).isEqualTo(DEFAULT_LANDLINE);
        assertThat(testClientUser.getMobile()).isEqualTo(UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void fullUpdateClientUserWithPatch() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        int databaseSizeBeforeUpdate = clientUserRepository.findAll().size();

        // Update the clientUser using partial update
        ClientUser partialUpdatedClientUser = new ClientUser();
        partialUpdatedClientUser.setId(clientUser.getId());

        partialUpdatedClientUser.landline(UPDATED_LANDLINE).mobile(UPDATED_MOBILE);

        restClientUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientUser))
            )
            .andExpect(status().isOk());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeUpdate);
        ClientUser testClientUser = clientUserList.get(clientUserList.size() - 1);
        assertThat(testClientUser.getLandline()).isEqualTo(UPDATED_LANDLINE);
        assertThat(testClientUser.getMobile()).isEqualTo(UPDATED_MOBILE);
    }

    @Test
    @Transactional
    void patchNonExistingClientUser() throws Exception {
        int databaseSizeBeforeUpdate = clientUserRepository.findAll().size();
        clientUser.setId(count.incrementAndGet());

        // Create the ClientUser
        ClientUserDTO clientUserDTO = clientUserMapper.toDto(clientUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientUserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientUser() throws Exception {
        int databaseSizeBeforeUpdate = clientUserRepository.findAll().size();
        clientUser.setId(count.incrementAndGet());

        // Create the ClientUser
        ClientUserDTO clientUserDTO = clientUserMapper.toDto(clientUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientUser() throws Exception {
        int databaseSizeBeforeUpdate = clientUserRepository.findAll().size();
        clientUser.setId(count.incrementAndGet());

        // Create the ClientUser
        ClientUserDTO clientUserDTO = clientUserMapper.toDto(clientUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientUserMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(clientUserDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientUser in the database
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientUser() throws Exception {
        // Initialize the database
        clientUserRepository.saveAndFlush(clientUser);

        int databaseSizeBeforeDelete = clientUserRepository.findAll().size();

        // Delete the clientUser
        restClientUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClientUser> clientUserList = clientUserRepository.findAll();
        assertThat(clientUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
