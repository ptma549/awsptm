package com.awsptm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.Engineer;
import com.awsptm.domain.Job;
import com.awsptm.domain.User;
import com.awsptm.repository.EngineerRepository;
import com.awsptm.service.criteria.EngineerCriteria;
import com.awsptm.service.dto.EngineerDTO;
import com.awsptm.service.mapper.EngineerMapper;
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
 * Integration tests for the {@link EngineerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EngineerResourceIT {

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/engineers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EngineerRepository engineerRepository;

    @Autowired
    private EngineerMapper engineerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEngineerMockMvc;

    private Engineer engineer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engineer createEntity(EntityManager em) {
        Engineer engineer = new Engineer().firstname(DEFAULT_FIRSTNAME).lastname(DEFAULT_LASTNAME).email(DEFAULT_EMAIL);
        return engineer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Engineer createUpdatedEntity(EntityManager em) {
        Engineer engineer = new Engineer().firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME).email(UPDATED_EMAIL);
        return engineer;
    }

    @BeforeEach
    public void initTest() {
        engineer = createEntity(em);
    }

    @Test
    @Transactional
    void createEngineer() throws Exception {
        int databaseSizeBeforeCreate = engineerRepository.findAll().size();
        // Create the Engineer
        EngineerDTO engineerDTO = engineerMapper.toDto(engineer);
        restEngineerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engineerDTO)))
            .andExpect(status().isCreated());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeCreate + 1);
        Engineer testEngineer = engineerList.get(engineerList.size() - 1);
        assertThat(testEngineer.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testEngineer.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testEngineer.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void createEngineerWithExistingId() throws Exception {
        // Create the Engineer with an existing ID
        engineer.setId(1L);
        EngineerDTO engineerDTO = engineerMapper.toDto(engineer);

        int databaseSizeBeforeCreate = engineerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEngineerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engineerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEngineers() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList
        restEngineerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(engineer.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @Test
    @Transactional
    void getEngineer() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get the engineer
        restEngineerMockMvc
            .perform(get(ENTITY_API_URL_ID, engineer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(engineer.getId().intValue()))
            .andExpect(jsonPath("$.firstname").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastname").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getEngineersByIdFiltering() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        Long id = engineer.getId();

        defaultEngineerShouldBeFound("id.equals=" + id);
        defaultEngineerShouldNotBeFound("id.notEquals=" + id);

        defaultEngineerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEngineerShouldNotBeFound("id.greaterThan=" + id);

        defaultEngineerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEngineerShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEngineersByFirstnameIsEqualToSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where firstname equals to DEFAULT_FIRSTNAME
        defaultEngineerShouldBeFound("firstname.equals=" + DEFAULT_FIRSTNAME);

        // Get all the engineerList where firstname equals to UPDATED_FIRSTNAME
        defaultEngineerShouldNotBeFound("firstname.equals=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByFirstnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where firstname not equals to DEFAULT_FIRSTNAME
        defaultEngineerShouldNotBeFound("firstname.notEquals=" + DEFAULT_FIRSTNAME);

        // Get all the engineerList where firstname not equals to UPDATED_FIRSTNAME
        defaultEngineerShouldBeFound("firstname.notEquals=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByFirstnameIsInShouldWork() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where firstname in DEFAULT_FIRSTNAME or UPDATED_FIRSTNAME
        defaultEngineerShouldBeFound("firstname.in=" + DEFAULT_FIRSTNAME + "," + UPDATED_FIRSTNAME);

        // Get all the engineerList where firstname equals to UPDATED_FIRSTNAME
        defaultEngineerShouldNotBeFound("firstname.in=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByFirstnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where firstname is not null
        defaultEngineerShouldBeFound("firstname.specified=true");

        // Get all the engineerList where firstname is null
        defaultEngineerShouldNotBeFound("firstname.specified=false");
    }

    @Test
    @Transactional
    void getAllEngineersByFirstnameContainsSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where firstname contains DEFAULT_FIRSTNAME
        defaultEngineerShouldBeFound("firstname.contains=" + DEFAULT_FIRSTNAME);

        // Get all the engineerList where firstname contains UPDATED_FIRSTNAME
        defaultEngineerShouldNotBeFound("firstname.contains=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByFirstnameNotContainsSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where firstname does not contain DEFAULT_FIRSTNAME
        defaultEngineerShouldNotBeFound("firstname.doesNotContain=" + DEFAULT_FIRSTNAME);

        // Get all the engineerList where firstname does not contain UPDATED_FIRSTNAME
        defaultEngineerShouldBeFound("firstname.doesNotContain=" + UPDATED_FIRSTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByLastnameIsEqualToSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where lastname equals to DEFAULT_LASTNAME
        defaultEngineerShouldBeFound("lastname.equals=" + DEFAULT_LASTNAME);

        // Get all the engineerList where lastname equals to UPDATED_LASTNAME
        defaultEngineerShouldNotBeFound("lastname.equals=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByLastnameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where lastname not equals to DEFAULT_LASTNAME
        defaultEngineerShouldNotBeFound("lastname.notEquals=" + DEFAULT_LASTNAME);

        // Get all the engineerList where lastname not equals to UPDATED_LASTNAME
        defaultEngineerShouldBeFound("lastname.notEquals=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByLastnameIsInShouldWork() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where lastname in DEFAULT_LASTNAME or UPDATED_LASTNAME
        defaultEngineerShouldBeFound("lastname.in=" + DEFAULT_LASTNAME + "," + UPDATED_LASTNAME);

        // Get all the engineerList where lastname equals to UPDATED_LASTNAME
        defaultEngineerShouldNotBeFound("lastname.in=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByLastnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where lastname is not null
        defaultEngineerShouldBeFound("lastname.specified=true");

        // Get all the engineerList where lastname is null
        defaultEngineerShouldNotBeFound("lastname.specified=false");
    }

    @Test
    @Transactional
    void getAllEngineersByLastnameContainsSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where lastname contains DEFAULT_LASTNAME
        defaultEngineerShouldBeFound("lastname.contains=" + DEFAULT_LASTNAME);

        // Get all the engineerList where lastname contains UPDATED_LASTNAME
        defaultEngineerShouldNotBeFound("lastname.contains=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByLastnameNotContainsSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where lastname does not contain DEFAULT_LASTNAME
        defaultEngineerShouldNotBeFound("lastname.doesNotContain=" + DEFAULT_LASTNAME);

        // Get all the engineerList where lastname does not contain UPDATED_LASTNAME
        defaultEngineerShouldBeFound("lastname.doesNotContain=" + UPDATED_LASTNAME);
    }

    @Test
    @Transactional
    void getAllEngineersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where email equals to DEFAULT_EMAIL
        defaultEngineerShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the engineerList where email equals to UPDATED_EMAIL
        defaultEngineerShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEngineersByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where email not equals to DEFAULT_EMAIL
        defaultEngineerShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the engineerList where email not equals to UPDATED_EMAIL
        defaultEngineerShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEngineersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultEngineerShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the engineerList where email equals to UPDATED_EMAIL
        defaultEngineerShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEngineersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where email is not null
        defaultEngineerShouldBeFound("email.specified=true");

        // Get all the engineerList where email is null
        defaultEngineerShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllEngineersByEmailContainsSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where email contains DEFAULT_EMAIL
        defaultEngineerShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the engineerList where email contains UPDATED_EMAIL
        defaultEngineerShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEngineersByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        // Get all the engineerList where email does not contain DEFAULT_EMAIL
        defaultEngineerShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the engineerList where email does not contain UPDATED_EMAIL
        defaultEngineerShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEngineersByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);
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
        engineer.setUser(user);
        engineerRepository.saveAndFlush(engineer);
        Long userId = user.getId();

        // Get all the engineerList where user equals to userId
        defaultEngineerShouldBeFound("userId.equals=" + userId);

        // Get all the engineerList where user equals to (userId + 1)
        defaultEngineerShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllEngineersByJobsIsEqualToSomething() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);
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
        engineer.addJobs(jobs);
        engineerRepository.saveAndFlush(engineer);
        Long jobsId = jobs.getId();

        // Get all the engineerList where jobs equals to jobsId
        defaultEngineerShouldBeFound("jobsId.equals=" + jobsId);

        // Get all the engineerList where jobs equals to (jobsId + 1)
        defaultEngineerShouldNotBeFound("jobsId.equals=" + (jobsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEngineerShouldBeFound(String filter) throws Exception {
        restEngineerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(engineer.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));

        // Check, that the count call also returns 1
        restEngineerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEngineerShouldNotBeFound(String filter) throws Exception {
        restEngineerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEngineerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEngineer() throws Exception {
        // Get the engineer
        restEngineerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEngineer() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        int databaseSizeBeforeUpdate = engineerRepository.findAll().size();

        // Update the engineer
        Engineer updatedEngineer = engineerRepository.findById(engineer.getId()).get();
        // Disconnect from session so that the updates on updatedEngineer are not directly saved in db
        em.detach(updatedEngineer);
        updatedEngineer.firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME).email(UPDATED_EMAIL);
        EngineerDTO engineerDTO = engineerMapper.toDto(updatedEngineer);

        restEngineerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, engineerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(engineerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeUpdate);
        Engineer testEngineer = engineerList.get(engineerList.size() - 1);
        assertThat(testEngineer.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testEngineer.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testEngineer.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void putNonExistingEngineer() throws Exception {
        int databaseSizeBeforeUpdate = engineerRepository.findAll().size();
        engineer.setId(count.incrementAndGet());

        // Create the Engineer
        EngineerDTO engineerDTO = engineerMapper.toDto(engineer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEngineerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, engineerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(engineerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEngineer() throws Exception {
        int databaseSizeBeforeUpdate = engineerRepository.findAll().size();
        engineer.setId(count.incrementAndGet());

        // Create the Engineer
        EngineerDTO engineerDTO = engineerMapper.toDto(engineer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEngineerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(engineerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEngineer() throws Exception {
        int databaseSizeBeforeUpdate = engineerRepository.findAll().size();
        engineer.setId(count.incrementAndGet());

        // Create the Engineer
        EngineerDTO engineerDTO = engineerMapper.toDto(engineer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEngineerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(engineerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEngineerWithPatch() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        int databaseSizeBeforeUpdate = engineerRepository.findAll().size();

        // Update the engineer using partial update
        Engineer partialUpdatedEngineer = new Engineer();
        partialUpdatedEngineer.setId(engineer.getId());

        partialUpdatedEngineer.email(UPDATED_EMAIL);

        restEngineerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEngineer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEngineer))
            )
            .andExpect(status().isOk());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeUpdate);
        Engineer testEngineer = engineerList.get(engineerList.size() - 1);
        assertThat(testEngineer.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testEngineer.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testEngineer.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void fullUpdateEngineerWithPatch() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        int databaseSizeBeforeUpdate = engineerRepository.findAll().size();

        // Update the engineer using partial update
        Engineer partialUpdatedEngineer = new Engineer();
        partialUpdatedEngineer.setId(engineer.getId());

        partialUpdatedEngineer.firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME).email(UPDATED_EMAIL);

        restEngineerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEngineer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEngineer))
            )
            .andExpect(status().isOk());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeUpdate);
        Engineer testEngineer = engineerList.get(engineerList.size() - 1);
        assertThat(testEngineer.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testEngineer.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testEngineer.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void patchNonExistingEngineer() throws Exception {
        int databaseSizeBeforeUpdate = engineerRepository.findAll().size();
        engineer.setId(count.incrementAndGet());

        // Create the Engineer
        EngineerDTO engineerDTO = engineerMapper.toDto(engineer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEngineerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, engineerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(engineerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEngineer() throws Exception {
        int databaseSizeBeforeUpdate = engineerRepository.findAll().size();
        engineer.setId(count.incrementAndGet());

        // Create the Engineer
        EngineerDTO engineerDTO = engineerMapper.toDto(engineer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEngineerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(engineerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEngineer() throws Exception {
        int databaseSizeBeforeUpdate = engineerRepository.findAll().size();
        engineer.setId(count.incrementAndGet());

        // Create the Engineer
        EngineerDTO engineerDTO = engineerMapper.toDto(engineer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEngineerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(engineerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Engineer in the database
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEngineer() throws Exception {
        // Initialize the database
        engineerRepository.saveAndFlush(engineer);

        int databaseSizeBeforeDelete = engineerRepository.findAll().size();

        // Delete the engineer
        restEngineerMockMvc
            .perform(delete(ENTITY_API_URL_ID, engineer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Engineer> engineerList = engineerRepository.findAll();
        assertThat(engineerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
