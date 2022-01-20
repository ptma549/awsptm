package com.awsptm.web.rest;

import static com.awsptm.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.Address;
import com.awsptm.domain.ClientUser;
import com.awsptm.domain.Inspection;
import com.awsptm.domain.Job;
import com.awsptm.domain.enumeration.Priority;
import com.awsptm.repository.InspectionRepository;
import com.awsptm.service.criteria.InspectionCriteria;
import com.awsptm.service.dto.InspectionDTO;
import com.awsptm.service.mapper.InspectionMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link InspectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InspectionResourceIT {

    private static final Priority DEFAULT_PRIORITY = Priority.P1_HIGH;
    private static final Priority UPDATED_PRIORITY = Priority.P2_MEDIUM;

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_OCCUPIERS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OCCUPIERS_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OCCUPIERS_HOME_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_OCCUPIERS_HOME_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_OCCUPIERS_WORK_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_OCCUPIERS_WORK_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_OCCUPIERS_MOBILE_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_OCCUPIERS_MOBILE_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_WORK = "AAAAAAAAAA";
    private static final String UPDATED_WORK = "BBBBBBBBBB";

    private static final String DEFAULT_ACCESS_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_ACCESS_INSTRUCTIONS = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final LocalDate DEFAULT_START = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START = LocalDate.ofEpochDay(-1L);

    private static final Duration DEFAULT_FREQUENCY = Duration.ofHours(6);
    private static final Duration UPDATED_FREQUENCY = Duration.ofHours(12);
    private static final Duration SMALLER_FREQUENCY = Duration.ofHours(5);

    private static final String ENTITY_API_URL = "/api/inspections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private InspectionMapper inspectionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInspectionMockMvc;

    private Inspection inspection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inspection createEntity(EntityManager em) {
        Inspection inspection = new Inspection()
            .priority(DEFAULT_PRIORITY)
            .created(DEFAULT_CREATED)
            .occupiersName(DEFAULT_OCCUPIERS_NAME)
            .occupiersHomePhone(DEFAULT_OCCUPIERS_HOME_PHONE)
            .occupiersWorkPhone(DEFAULT_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(DEFAULT_OCCUPIERS_MOBILE_PHONE)
            .work(DEFAULT_WORK)
            .accessInstructions(DEFAULT_ACCESS_INSTRUCTIONS)
            .updated(DEFAULT_UPDATED)
            .start(DEFAULT_START)
            .frequency(DEFAULT_FREQUENCY);
        return inspection;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inspection createUpdatedEntity(EntityManager em) {
        Inspection inspection = new Inspection()
            .priority(UPDATED_PRIORITY)
            .created(UPDATED_CREATED)
            .occupiersName(UPDATED_OCCUPIERS_NAME)
            .occupiersHomePhone(UPDATED_OCCUPIERS_HOME_PHONE)
            .occupiersWorkPhone(UPDATED_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(UPDATED_OCCUPIERS_MOBILE_PHONE)
            .work(UPDATED_WORK)
            .accessInstructions(UPDATED_ACCESS_INSTRUCTIONS)
            .updated(UPDATED_UPDATED)
            .start(UPDATED_START)
            .frequency(UPDATED_FREQUENCY);
        return inspection;
    }

    @BeforeEach
    public void initTest() {
        inspection = createEntity(em);
    }

    @Test
    @Transactional
    void createInspection() throws Exception {
        int databaseSizeBeforeCreate = inspectionRepository.findAll().size();
        // Create the Inspection
        InspectionDTO inspectionDTO = inspectionMapper.toDto(inspection);
        restInspectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inspectionDTO)))
            .andExpect(status().isCreated());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeCreate + 1);
        Inspection testInspection = inspectionList.get(inspectionList.size() - 1);
        assertThat(testInspection.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testInspection.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testInspection.getOccupiersName()).isEqualTo(DEFAULT_OCCUPIERS_NAME);
        assertThat(testInspection.getOccupiersHomePhone()).isEqualTo(DEFAULT_OCCUPIERS_HOME_PHONE);
        assertThat(testInspection.getOccupiersWorkPhone()).isEqualTo(DEFAULT_OCCUPIERS_WORK_PHONE);
        assertThat(testInspection.getOccupiersMobilePhone()).isEqualTo(DEFAULT_OCCUPIERS_MOBILE_PHONE);
        assertThat(testInspection.getWork()).isEqualTo(DEFAULT_WORK);
        assertThat(testInspection.getAccessInstructions()).isEqualTo(DEFAULT_ACCESS_INSTRUCTIONS);
        assertThat(testInspection.getUpdated()).isEqualTo(DEFAULT_UPDATED);
        assertThat(testInspection.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testInspection.getFrequency()).isEqualTo(DEFAULT_FREQUENCY);
    }

    @Test
    @Transactional
    void createInspectionWithExistingId() throws Exception {
        // Create the Inspection with an existing ID
        inspection.setId(1L);
        InspectionDTO inspectionDTO = inspectionMapper.toDto(inspection);

        int databaseSizeBeforeCreate = inspectionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInspectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inspectionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInspections() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inspection.getId().intValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].occupiersName").value(hasItem(DEFAULT_OCCUPIERS_NAME)))
            .andExpect(jsonPath("$.[*].occupiersHomePhone").value(hasItem(DEFAULT_OCCUPIERS_HOME_PHONE)))
            .andExpect(jsonPath("$.[*].occupiersWorkPhone").value(hasItem(DEFAULT_OCCUPIERS_WORK_PHONE)))
            .andExpect(jsonPath("$.[*].occupiersMobilePhone").value(hasItem(DEFAULT_OCCUPIERS_MOBILE_PHONE)))
            .andExpect(jsonPath("$.[*].work").value(hasItem(DEFAULT_WORK.toString())))
            .andExpect(jsonPath("$.[*].accessInstructions").value(hasItem(DEFAULT_ACCESS_INSTRUCTIONS.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(DEFAULT_FREQUENCY.toString())));
    }

    @Test
    @Transactional
    void getInspection() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get the inspection
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL_ID, inspection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inspection.getId().intValue()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.occupiersName").value(DEFAULT_OCCUPIERS_NAME))
            .andExpect(jsonPath("$.occupiersHomePhone").value(DEFAULT_OCCUPIERS_HOME_PHONE))
            .andExpect(jsonPath("$.occupiersWorkPhone").value(DEFAULT_OCCUPIERS_WORK_PHONE))
            .andExpect(jsonPath("$.occupiersMobilePhone").value(DEFAULT_OCCUPIERS_MOBILE_PHONE))
            .andExpect(jsonPath("$.work").value(DEFAULT_WORK.toString()))
            .andExpect(jsonPath("$.accessInstructions").value(DEFAULT_ACCESS_INSTRUCTIONS.toString()))
            .andExpect(jsonPath("$.updated").value(sameInstant(DEFAULT_UPDATED)))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.frequency").value(DEFAULT_FREQUENCY.toString()));
    }

    @Test
    @Transactional
    void getInspectionsByIdFiltering() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        Long id = inspection.getId();

        defaultInspectionShouldBeFound("id.equals=" + id);
        defaultInspectionShouldNotBeFound("id.notEquals=" + id);

        defaultInspectionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInspectionShouldNotBeFound("id.greaterThan=" + id);

        defaultInspectionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInspectionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInspectionsByPriorityIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where priority equals to DEFAULT_PRIORITY
        defaultInspectionShouldBeFound("priority.equals=" + DEFAULT_PRIORITY);

        // Get all the inspectionList where priority equals to UPDATED_PRIORITY
        defaultInspectionShouldNotBeFound("priority.equals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllInspectionsByPriorityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where priority not equals to DEFAULT_PRIORITY
        defaultInspectionShouldNotBeFound("priority.notEquals=" + DEFAULT_PRIORITY);

        // Get all the inspectionList where priority not equals to UPDATED_PRIORITY
        defaultInspectionShouldBeFound("priority.notEquals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllInspectionsByPriorityIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where priority in DEFAULT_PRIORITY or UPDATED_PRIORITY
        defaultInspectionShouldBeFound("priority.in=" + DEFAULT_PRIORITY + "," + UPDATED_PRIORITY);

        // Get all the inspectionList where priority equals to UPDATED_PRIORITY
        defaultInspectionShouldNotBeFound("priority.in=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllInspectionsByPriorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where priority is not null
        defaultInspectionShouldBeFound("priority.specified=true");

        // Get all the inspectionList where priority is null
        defaultInspectionShouldNotBeFound("priority.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where created equals to DEFAULT_CREATED
        defaultInspectionShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the inspectionList where created equals to UPDATED_CREATED
        defaultInspectionShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where created not equals to DEFAULT_CREATED
        defaultInspectionShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the inspectionList where created not equals to UPDATED_CREATED
        defaultInspectionShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultInspectionShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the inspectionList where created equals to UPDATED_CREATED
        defaultInspectionShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where created is not null
        defaultInspectionShouldBeFound("created.specified=true");

        // Get all the inspectionList where created is null
        defaultInspectionShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where created is greater than or equal to DEFAULT_CREATED
        defaultInspectionShouldBeFound("created.greaterThanOrEqual=" + DEFAULT_CREATED);

        // Get all the inspectionList where created is greater than or equal to UPDATED_CREATED
        defaultInspectionShouldNotBeFound("created.greaterThanOrEqual=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where created is less than or equal to DEFAULT_CREATED
        defaultInspectionShouldBeFound("created.lessThanOrEqual=" + DEFAULT_CREATED);

        // Get all the inspectionList where created is less than or equal to SMALLER_CREATED
        defaultInspectionShouldNotBeFound("created.lessThanOrEqual=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where created is less than DEFAULT_CREATED
        defaultInspectionShouldNotBeFound("created.lessThan=" + DEFAULT_CREATED);

        // Get all the inspectionList where created is less than UPDATED_CREATED
        defaultInspectionShouldBeFound("created.lessThan=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where created is greater than DEFAULT_CREATED
        defaultInspectionShouldNotBeFound("created.greaterThan=" + DEFAULT_CREATED);

        // Get all the inspectionList where created is greater than SMALLER_CREATED
        defaultInspectionShouldBeFound("created.greaterThan=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersNameIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersName equals to DEFAULT_OCCUPIERS_NAME
        defaultInspectionShouldBeFound("occupiersName.equals=" + DEFAULT_OCCUPIERS_NAME);

        // Get all the inspectionList where occupiersName equals to UPDATED_OCCUPIERS_NAME
        defaultInspectionShouldNotBeFound("occupiersName.equals=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersName not equals to DEFAULT_OCCUPIERS_NAME
        defaultInspectionShouldNotBeFound("occupiersName.notEquals=" + DEFAULT_OCCUPIERS_NAME);

        // Get all the inspectionList where occupiersName not equals to UPDATED_OCCUPIERS_NAME
        defaultInspectionShouldBeFound("occupiersName.notEquals=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersNameIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersName in DEFAULT_OCCUPIERS_NAME or UPDATED_OCCUPIERS_NAME
        defaultInspectionShouldBeFound("occupiersName.in=" + DEFAULT_OCCUPIERS_NAME + "," + UPDATED_OCCUPIERS_NAME);

        // Get all the inspectionList where occupiersName equals to UPDATED_OCCUPIERS_NAME
        defaultInspectionShouldNotBeFound("occupiersName.in=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersName is not null
        defaultInspectionShouldBeFound("occupiersName.specified=true");

        // Get all the inspectionList where occupiersName is null
        defaultInspectionShouldNotBeFound("occupiersName.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersNameContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersName contains DEFAULT_OCCUPIERS_NAME
        defaultInspectionShouldBeFound("occupiersName.contains=" + DEFAULT_OCCUPIERS_NAME);

        // Get all the inspectionList where occupiersName contains UPDATED_OCCUPIERS_NAME
        defaultInspectionShouldNotBeFound("occupiersName.contains=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersNameNotContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersName does not contain DEFAULT_OCCUPIERS_NAME
        defaultInspectionShouldNotBeFound("occupiersName.doesNotContain=" + DEFAULT_OCCUPIERS_NAME);

        // Get all the inspectionList where occupiersName does not contain UPDATED_OCCUPIERS_NAME
        defaultInspectionShouldBeFound("occupiersName.doesNotContain=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersHomePhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersHomePhone equals to DEFAULT_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldBeFound("occupiersHomePhone.equals=" + DEFAULT_OCCUPIERS_HOME_PHONE);

        // Get all the inspectionList where occupiersHomePhone equals to UPDATED_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldNotBeFound("occupiersHomePhone.equals=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersHomePhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersHomePhone not equals to DEFAULT_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldNotBeFound("occupiersHomePhone.notEquals=" + DEFAULT_OCCUPIERS_HOME_PHONE);

        // Get all the inspectionList where occupiersHomePhone not equals to UPDATED_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldBeFound("occupiersHomePhone.notEquals=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersHomePhoneIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersHomePhone in DEFAULT_OCCUPIERS_HOME_PHONE or UPDATED_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldBeFound("occupiersHomePhone.in=" + DEFAULT_OCCUPIERS_HOME_PHONE + "," + UPDATED_OCCUPIERS_HOME_PHONE);

        // Get all the inspectionList where occupiersHomePhone equals to UPDATED_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldNotBeFound("occupiersHomePhone.in=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersHomePhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersHomePhone is not null
        defaultInspectionShouldBeFound("occupiersHomePhone.specified=true");

        // Get all the inspectionList where occupiersHomePhone is null
        defaultInspectionShouldNotBeFound("occupiersHomePhone.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersHomePhoneContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersHomePhone contains DEFAULT_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldBeFound("occupiersHomePhone.contains=" + DEFAULT_OCCUPIERS_HOME_PHONE);

        // Get all the inspectionList where occupiersHomePhone contains UPDATED_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldNotBeFound("occupiersHomePhone.contains=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersHomePhoneNotContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersHomePhone does not contain DEFAULT_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldNotBeFound("occupiersHomePhone.doesNotContain=" + DEFAULT_OCCUPIERS_HOME_PHONE);

        // Get all the inspectionList where occupiersHomePhone does not contain UPDATED_OCCUPIERS_HOME_PHONE
        defaultInspectionShouldBeFound("occupiersHomePhone.doesNotContain=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersWorkPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersWorkPhone equals to DEFAULT_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldBeFound("occupiersWorkPhone.equals=" + DEFAULT_OCCUPIERS_WORK_PHONE);

        // Get all the inspectionList where occupiersWorkPhone equals to UPDATED_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldNotBeFound("occupiersWorkPhone.equals=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersWorkPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersWorkPhone not equals to DEFAULT_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldNotBeFound("occupiersWorkPhone.notEquals=" + DEFAULT_OCCUPIERS_WORK_PHONE);

        // Get all the inspectionList where occupiersWorkPhone not equals to UPDATED_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldBeFound("occupiersWorkPhone.notEquals=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersWorkPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersWorkPhone in DEFAULT_OCCUPIERS_WORK_PHONE or UPDATED_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldBeFound("occupiersWorkPhone.in=" + DEFAULT_OCCUPIERS_WORK_PHONE + "," + UPDATED_OCCUPIERS_WORK_PHONE);

        // Get all the inspectionList where occupiersWorkPhone equals to UPDATED_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldNotBeFound("occupiersWorkPhone.in=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersWorkPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersWorkPhone is not null
        defaultInspectionShouldBeFound("occupiersWorkPhone.specified=true");

        // Get all the inspectionList where occupiersWorkPhone is null
        defaultInspectionShouldNotBeFound("occupiersWorkPhone.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersWorkPhoneContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersWorkPhone contains DEFAULT_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldBeFound("occupiersWorkPhone.contains=" + DEFAULT_OCCUPIERS_WORK_PHONE);

        // Get all the inspectionList where occupiersWorkPhone contains UPDATED_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldNotBeFound("occupiersWorkPhone.contains=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersWorkPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersWorkPhone does not contain DEFAULT_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldNotBeFound("occupiersWorkPhone.doesNotContain=" + DEFAULT_OCCUPIERS_WORK_PHONE);

        // Get all the inspectionList where occupiersWorkPhone does not contain UPDATED_OCCUPIERS_WORK_PHONE
        defaultInspectionShouldBeFound("occupiersWorkPhone.doesNotContain=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersMobilePhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersMobilePhone equals to DEFAULT_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldBeFound("occupiersMobilePhone.equals=" + DEFAULT_OCCUPIERS_MOBILE_PHONE);

        // Get all the inspectionList where occupiersMobilePhone equals to UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldNotBeFound("occupiersMobilePhone.equals=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersMobilePhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersMobilePhone not equals to DEFAULT_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldNotBeFound("occupiersMobilePhone.notEquals=" + DEFAULT_OCCUPIERS_MOBILE_PHONE);

        // Get all the inspectionList where occupiersMobilePhone not equals to UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldBeFound("occupiersMobilePhone.notEquals=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersMobilePhoneIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersMobilePhone in DEFAULT_OCCUPIERS_MOBILE_PHONE or UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldBeFound("occupiersMobilePhone.in=" + DEFAULT_OCCUPIERS_MOBILE_PHONE + "," + UPDATED_OCCUPIERS_MOBILE_PHONE);

        // Get all the inspectionList where occupiersMobilePhone equals to UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldNotBeFound("occupiersMobilePhone.in=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersMobilePhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersMobilePhone is not null
        defaultInspectionShouldBeFound("occupiersMobilePhone.specified=true");

        // Get all the inspectionList where occupiersMobilePhone is null
        defaultInspectionShouldNotBeFound("occupiersMobilePhone.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersMobilePhoneContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersMobilePhone contains DEFAULT_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldBeFound("occupiersMobilePhone.contains=" + DEFAULT_OCCUPIERS_MOBILE_PHONE);

        // Get all the inspectionList where occupiersMobilePhone contains UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldNotBeFound("occupiersMobilePhone.contains=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByOccupiersMobilePhoneNotContainsSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where occupiersMobilePhone does not contain DEFAULT_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldNotBeFound("occupiersMobilePhone.doesNotContain=" + DEFAULT_OCCUPIERS_MOBILE_PHONE);

        // Get all the inspectionList where occupiersMobilePhone does not contain UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultInspectionShouldBeFound("occupiersMobilePhone.doesNotContain=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllInspectionsByUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where updated equals to DEFAULT_UPDATED
        defaultInspectionShouldBeFound("updated.equals=" + DEFAULT_UPDATED);

        // Get all the inspectionList where updated equals to UPDATED_UPDATED
        defaultInspectionShouldNotBeFound("updated.equals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByUpdatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where updated not equals to DEFAULT_UPDATED
        defaultInspectionShouldNotBeFound("updated.notEquals=" + DEFAULT_UPDATED);

        // Get all the inspectionList where updated not equals to UPDATED_UPDATED
        defaultInspectionShouldBeFound("updated.notEquals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where updated in DEFAULT_UPDATED or UPDATED_UPDATED
        defaultInspectionShouldBeFound("updated.in=" + DEFAULT_UPDATED + "," + UPDATED_UPDATED);

        // Get all the inspectionList where updated equals to UPDATED_UPDATED
        defaultInspectionShouldNotBeFound("updated.in=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where updated is not null
        defaultInspectionShouldBeFound("updated.specified=true");

        // Get all the inspectionList where updated is null
        defaultInspectionShouldNotBeFound("updated.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByUpdatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where updated is greater than or equal to DEFAULT_UPDATED
        defaultInspectionShouldBeFound("updated.greaterThanOrEqual=" + DEFAULT_UPDATED);

        // Get all the inspectionList where updated is greater than or equal to UPDATED_UPDATED
        defaultInspectionShouldNotBeFound("updated.greaterThanOrEqual=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByUpdatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where updated is less than or equal to DEFAULT_UPDATED
        defaultInspectionShouldBeFound("updated.lessThanOrEqual=" + DEFAULT_UPDATED);

        // Get all the inspectionList where updated is less than or equal to SMALLER_UPDATED
        defaultInspectionShouldNotBeFound("updated.lessThanOrEqual=" + SMALLER_UPDATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByUpdatedIsLessThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where updated is less than DEFAULT_UPDATED
        defaultInspectionShouldNotBeFound("updated.lessThan=" + DEFAULT_UPDATED);

        // Get all the inspectionList where updated is less than UPDATED_UPDATED
        defaultInspectionShouldBeFound("updated.lessThan=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByUpdatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where updated is greater than DEFAULT_UPDATED
        defaultInspectionShouldNotBeFound("updated.greaterThan=" + DEFAULT_UPDATED);

        // Get all the inspectionList where updated is greater than SMALLER_UPDATED
        defaultInspectionShouldBeFound("updated.greaterThan=" + SMALLER_UPDATED);
    }

    @Test
    @Transactional
    void getAllInspectionsByStartIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where start equals to DEFAULT_START
        defaultInspectionShouldBeFound("start.equals=" + DEFAULT_START);

        // Get all the inspectionList where start equals to UPDATED_START
        defaultInspectionShouldNotBeFound("start.equals=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllInspectionsByStartIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where start not equals to DEFAULT_START
        defaultInspectionShouldNotBeFound("start.notEquals=" + DEFAULT_START);

        // Get all the inspectionList where start not equals to UPDATED_START
        defaultInspectionShouldBeFound("start.notEquals=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllInspectionsByStartIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where start in DEFAULT_START or UPDATED_START
        defaultInspectionShouldBeFound("start.in=" + DEFAULT_START + "," + UPDATED_START);

        // Get all the inspectionList where start equals to UPDATED_START
        defaultInspectionShouldNotBeFound("start.in=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllInspectionsByStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where start is not null
        defaultInspectionShouldBeFound("start.specified=true");

        // Get all the inspectionList where start is null
        defaultInspectionShouldNotBeFound("start.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByStartIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where start is greater than or equal to DEFAULT_START
        defaultInspectionShouldBeFound("start.greaterThanOrEqual=" + DEFAULT_START);

        // Get all the inspectionList where start is greater than or equal to UPDATED_START
        defaultInspectionShouldNotBeFound("start.greaterThanOrEqual=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllInspectionsByStartIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where start is less than or equal to DEFAULT_START
        defaultInspectionShouldBeFound("start.lessThanOrEqual=" + DEFAULT_START);

        // Get all the inspectionList where start is less than or equal to SMALLER_START
        defaultInspectionShouldNotBeFound("start.lessThanOrEqual=" + SMALLER_START);
    }

    @Test
    @Transactional
    void getAllInspectionsByStartIsLessThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where start is less than DEFAULT_START
        defaultInspectionShouldNotBeFound("start.lessThan=" + DEFAULT_START);

        // Get all the inspectionList where start is less than UPDATED_START
        defaultInspectionShouldBeFound("start.lessThan=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllInspectionsByStartIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where start is greater than DEFAULT_START
        defaultInspectionShouldNotBeFound("start.greaterThan=" + DEFAULT_START);

        // Get all the inspectionList where start is greater than SMALLER_START
        defaultInspectionShouldBeFound("start.greaterThan=" + SMALLER_START);
    }

    @Test
    @Transactional
    void getAllInspectionsByFrequencyIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where frequency equals to DEFAULT_FREQUENCY
        defaultInspectionShouldBeFound("frequency.equals=" + DEFAULT_FREQUENCY);

        // Get all the inspectionList where frequency equals to UPDATED_FREQUENCY
        defaultInspectionShouldNotBeFound("frequency.equals=" + UPDATED_FREQUENCY);
    }

    @Test
    @Transactional
    void getAllInspectionsByFrequencyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where frequency not equals to DEFAULT_FREQUENCY
        defaultInspectionShouldNotBeFound("frequency.notEquals=" + DEFAULT_FREQUENCY);

        // Get all the inspectionList where frequency not equals to UPDATED_FREQUENCY
        defaultInspectionShouldBeFound("frequency.notEquals=" + UPDATED_FREQUENCY);
    }

    @Test
    @Transactional
    void getAllInspectionsByFrequencyIsInShouldWork() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where frequency in DEFAULT_FREQUENCY or UPDATED_FREQUENCY
        defaultInspectionShouldBeFound("frequency.in=" + DEFAULT_FREQUENCY + "," + UPDATED_FREQUENCY);

        // Get all the inspectionList where frequency equals to UPDATED_FREQUENCY
        defaultInspectionShouldNotBeFound("frequency.in=" + UPDATED_FREQUENCY);
    }

    @Test
    @Transactional
    void getAllInspectionsByFrequencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where frequency is not null
        defaultInspectionShouldBeFound("frequency.specified=true");

        // Get all the inspectionList where frequency is null
        defaultInspectionShouldNotBeFound("frequency.specified=false");
    }

    @Test
    @Transactional
    void getAllInspectionsByFrequencyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where frequency is greater than or equal to DEFAULT_FREQUENCY
        defaultInspectionShouldBeFound("frequency.greaterThanOrEqual=" + DEFAULT_FREQUENCY);

        // Get all the inspectionList where frequency is greater than or equal to UPDATED_FREQUENCY
        defaultInspectionShouldNotBeFound("frequency.greaterThanOrEqual=" + UPDATED_FREQUENCY);
    }

    @Test
    @Transactional
    void getAllInspectionsByFrequencyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where frequency is less than or equal to DEFAULT_FREQUENCY
        defaultInspectionShouldBeFound("frequency.lessThanOrEqual=" + DEFAULT_FREQUENCY);

        // Get all the inspectionList where frequency is less than or equal to SMALLER_FREQUENCY
        defaultInspectionShouldNotBeFound("frequency.lessThanOrEqual=" + SMALLER_FREQUENCY);
    }

    @Test
    @Transactional
    void getAllInspectionsByFrequencyIsLessThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where frequency is less than DEFAULT_FREQUENCY
        defaultInspectionShouldNotBeFound("frequency.lessThan=" + DEFAULT_FREQUENCY);

        // Get all the inspectionList where frequency is less than UPDATED_FREQUENCY
        defaultInspectionShouldBeFound("frequency.lessThan=" + UPDATED_FREQUENCY);
    }

    @Test
    @Transactional
    void getAllInspectionsByFrequencyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        // Get all the inspectionList where frequency is greater than DEFAULT_FREQUENCY
        defaultInspectionShouldNotBeFound("frequency.greaterThan=" + DEFAULT_FREQUENCY);

        // Get all the inspectionList where frequency is greater than SMALLER_FREQUENCY
        defaultInspectionShouldBeFound("frequency.greaterThan=" + SMALLER_FREQUENCY);
    }

    @Test
    @Transactional
    void getAllInspectionsByJobsIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);
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
        inspection.setJobs(jobs);
        inspectionRepository.saveAndFlush(inspection);
        Long jobsId = jobs.getId();

        // Get all the inspectionList where jobs equals to jobsId
        defaultInspectionShouldBeFound("jobsId.equals=" + jobsId);

        // Get all the inspectionList where jobs equals to (jobsId + 1)
        defaultInspectionShouldNotBeFound("jobsId.equals=" + (jobsId + 1));
    }

    @Test
    @Transactional
    void getAllInspectionsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);
        ClientUser createdBy;
        if (TestUtil.findAll(em, ClientUser.class).isEmpty()) {
            createdBy = ClientUserResourceIT.createEntity(em);
            em.persist(createdBy);
            em.flush();
        } else {
            createdBy = TestUtil.findAll(em, ClientUser.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        inspection.setCreatedBy(createdBy);
        inspectionRepository.saveAndFlush(inspection);
        Long createdById = createdBy.getId();

        // Get all the inspectionList where createdBy equals to createdById
        defaultInspectionShouldBeFound("createdById.equals=" + createdById);

        // Get all the inspectionList where createdBy equals to (createdById + 1)
        defaultInspectionShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllInspectionsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);
        Address address;
        if (TestUtil.findAll(em, Address.class).isEmpty()) {
            address = AddressResourceIT.createEntity(em);
            em.persist(address);
            em.flush();
        } else {
            address = TestUtil.findAll(em, Address.class).get(0);
        }
        em.persist(address);
        em.flush();
        inspection.setAddress(address);
        inspectionRepository.saveAndFlush(inspection);
        Long addressId = address.getId();

        // Get all the inspectionList where address equals to addressId
        defaultInspectionShouldBeFound("addressId.equals=" + addressId);

        // Get all the inspectionList where address equals to (addressId + 1)
        defaultInspectionShouldNotBeFound("addressId.equals=" + (addressId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInspectionShouldBeFound(String filter) throws Exception {
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inspection.getId().intValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].occupiersName").value(hasItem(DEFAULT_OCCUPIERS_NAME)))
            .andExpect(jsonPath("$.[*].occupiersHomePhone").value(hasItem(DEFAULT_OCCUPIERS_HOME_PHONE)))
            .andExpect(jsonPath("$.[*].occupiersWorkPhone").value(hasItem(DEFAULT_OCCUPIERS_WORK_PHONE)))
            .andExpect(jsonPath("$.[*].occupiersMobilePhone").value(hasItem(DEFAULT_OCCUPIERS_MOBILE_PHONE)))
            .andExpect(jsonPath("$.[*].work").value(hasItem(DEFAULT_WORK.toString())))
            .andExpect(jsonPath("$.[*].accessInstructions").value(hasItem(DEFAULT_ACCESS_INSTRUCTIONS.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(DEFAULT_FREQUENCY.toString())));

        // Check, that the count call also returns 1
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInspectionShouldNotBeFound(String filter) throws Exception {
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInspectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInspection() throws Exception {
        // Get the inspection
        restInspectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewInspection() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();

        // Update the inspection
        Inspection updatedInspection = inspectionRepository.findById(inspection.getId()).get();
        // Disconnect from session so that the updates on updatedInspection are not directly saved in db
        em.detach(updatedInspection);
        updatedInspection
            .priority(UPDATED_PRIORITY)
            .created(UPDATED_CREATED)
            .occupiersName(UPDATED_OCCUPIERS_NAME)
            .occupiersHomePhone(UPDATED_OCCUPIERS_HOME_PHONE)
            .occupiersWorkPhone(UPDATED_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(UPDATED_OCCUPIERS_MOBILE_PHONE)
            .work(UPDATED_WORK)
            .accessInstructions(UPDATED_ACCESS_INSTRUCTIONS)
            .updated(UPDATED_UPDATED)
            .start(UPDATED_START)
            .frequency(UPDATED_FREQUENCY);
        InspectionDTO inspectionDTO = inspectionMapper.toDto(updatedInspection);

        restInspectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inspectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inspectionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
        Inspection testInspection = inspectionList.get(inspectionList.size() - 1);
        assertThat(testInspection.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testInspection.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testInspection.getOccupiersName()).isEqualTo(UPDATED_OCCUPIERS_NAME);
        assertThat(testInspection.getOccupiersHomePhone()).isEqualTo(UPDATED_OCCUPIERS_HOME_PHONE);
        assertThat(testInspection.getOccupiersWorkPhone()).isEqualTo(UPDATED_OCCUPIERS_WORK_PHONE);
        assertThat(testInspection.getOccupiersMobilePhone()).isEqualTo(UPDATED_OCCUPIERS_MOBILE_PHONE);
        assertThat(testInspection.getWork()).isEqualTo(UPDATED_WORK);
        assertThat(testInspection.getAccessInstructions()).isEqualTo(UPDATED_ACCESS_INSTRUCTIONS);
        assertThat(testInspection.getUpdated()).isEqualTo(UPDATED_UPDATED);
        assertThat(testInspection.getStart()).isEqualTo(UPDATED_START);
        assertThat(testInspection.getFrequency()).isEqualTo(UPDATED_FREQUENCY);
    }

    @Test
    @Transactional
    void putNonExistingInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(count.incrementAndGet());

        // Create the Inspection
        InspectionDTO inspectionDTO = inspectionMapper.toDto(inspection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inspectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inspectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(count.incrementAndGet());

        // Create the Inspection
        InspectionDTO inspectionDTO = inspectionMapper.toDto(inspection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inspectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(count.incrementAndGet());

        // Create the Inspection
        InspectionDTO inspectionDTO = inspectionMapper.toDto(inspection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inspectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInspectionWithPatch() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();

        // Update the inspection using partial update
        Inspection partialUpdatedInspection = new Inspection();
        partialUpdatedInspection.setId(inspection.getId());

        partialUpdatedInspection
            .priority(UPDATED_PRIORITY)
            .occupiersName(UPDATED_OCCUPIERS_NAME)
            .occupiersHomePhone(UPDATED_OCCUPIERS_HOME_PHONE)
            .occupiersWorkPhone(UPDATED_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(UPDATED_OCCUPIERS_MOBILE_PHONE)
            .accessInstructions(UPDATED_ACCESS_INSTRUCTIONS)
            .start(UPDATED_START)
            .frequency(UPDATED_FREQUENCY);

        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInspection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInspection))
            )
            .andExpect(status().isOk());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
        Inspection testInspection = inspectionList.get(inspectionList.size() - 1);
        assertThat(testInspection.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testInspection.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testInspection.getOccupiersName()).isEqualTo(UPDATED_OCCUPIERS_NAME);
        assertThat(testInspection.getOccupiersHomePhone()).isEqualTo(UPDATED_OCCUPIERS_HOME_PHONE);
        assertThat(testInspection.getOccupiersWorkPhone()).isEqualTo(UPDATED_OCCUPIERS_WORK_PHONE);
        assertThat(testInspection.getOccupiersMobilePhone()).isEqualTo(UPDATED_OCCUPIERS_MOBILE_PHONE);
        assertThat(testInspection.getWork()).isEqualTo(DEFAULT_WORK);
        assertThat(testInspection.getAccessInstructions()).isEqualTo(UPDATED_ACCESS_INSTRUCTIONS);
        assertThat(testInspection.getUpdated()).isEqualTo(DEFAULT_UPDATED);
        assertThat(testInspection.getStart()).isEqualTo(UPDATED_START);
        assertThat(testInspection.getFrequency()).isEqualTo(UPDATED_FREQUENCY);
    }

    @Test
    @Transactional
    void fullUpdateInspectionWithPatch() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();

        // Update the inspection using partial update
        Inspection partialUpdatedInspection = new Inspection();
        partialUpdatedInspection.setId(inspection.getId());

        partialUpdatedInspection
            .priority(UPDATED_PRIORITY)
            .created(UPDATED_CREATED)
            .occupiersName(UPDATED_OCCUPIERS_NAME)
            .occupiersHomePhone(UPDATED_OCCUPIERS_HOME_PHONE)
            .occupiersWorkPhone(UPDATED_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(UPDATED_OCCUPIERS_MOBILE_PHONE)
            .work(UPDATED_WORK)
            .accessInstructions(UPDATED_ACCESS_INSTRUCTIONS)
            .updated(UPDATED_UPDATED)
            .start(UPDATED_START)
            .frequency(UPDATED_FREQUENCY);

        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInspection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInspection))
            )
            .andExpect(status().isOk());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
        Inspection testInspection = inspectionList.get(inspectionList.size() - 1);
        assertThat(testInspection.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testInspection.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testInspection.getOccupiersName()).isEqualTo(UPDATED_OCCUPIERS_NAME);
        assertThat(testInspection.getOccupiersHomePhone()).isEqualTo(UPDATED_OCCUPIERS_HOME_PHONE);
        assertThat(testInspection.getOccupiersWorkPhone()).isEqualTo(UPDATED_OCCUPIERS_WORK_PHONE);
        assertThat(testInspection.getOccupiersMobilePhone()).isEqualTo(UPDATED_OCCUPIERS_MOBILE_PHONE);
        assertThat(testInspection.getWork()).isEqualTo(UPDATED_WORK);
        assertThat(testInspection.getAccessInstructions()).isEqualTo(UPDATED_ACCESS_INSTRUCTIONS);
        assertThat(testInspection.getUpdated()).isEqualTo(UPDATED_UPDATED);
        assertThat(testInspection.getStart()).isEqualTo(UPDATED_START);
        assertThat(testInspection.getFrequency()).isEqualTo(UPDATED_FREQUENCY);
    }

    @Test
    @Transactional
    void patchNonExistingInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(count.incrementAndGet());

        // Create the Inspection
        InspectionDTO inspectionDTO = inspectionMapper.toDto(inspection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inspectionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inspectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(count.incrementAndGet());

        // Create the Inspection
        InspectionDTO inspectionDTO = inspectionMapper.toDto(inspection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inspectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInspection() throws Exception {
        int databaseSizeBeforeUpdate = inspectionRepository.findAll().size();
        inspection.setId(count.incrementAndGet());

        // Create the Inspection
        InspectionDTO inspectionDTO = inspectionMapper.toDto(inspection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInspectionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(inspectionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inspection in the database
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInspection() throws Exception {
        // Initialize the database
        inspectionRepository.saveAndFlush(inspection);

        int databaseSizeBeforeDelete = inspectionRepository.findAll().size();

        // Delete the inspection
        restInspectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, inspection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Inspection> inspectionList = inspectionRepository.findAll();
        assertThat(inspectionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
