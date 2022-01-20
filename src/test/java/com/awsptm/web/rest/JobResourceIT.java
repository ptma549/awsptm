package com.awsptm.web.rest;

import static com.awsptm.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.Address;
import com.awsptm.domain.ClientUser;
import com.awsptm.domain.Engineer;
import com.awsptm.domain.Inspection;
import com.awsptm.domain.Job;
import com.awsptm.domain.Visit;
import com.awsptm.domain.enumeration.Priority;
import com.awsptm.repository.JobRepository;
import com.awsptm.service.criteria.JobCriteria;
import com.awsptm.service.dto.JobDTO;
import com.awsptm.service.mapper.JobMapper;
import java.time.Instant;
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
 * Integration tests for the {@link JobResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JobResourceIT {

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

    private static final String DEFAULT_CLIENT_ORDER_ID = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT_ORDER_ID = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_ASSIGNED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ASSIGNED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ASSIGNED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_SCHEDULED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_SCHEDULED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_SCHEDULED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_COMPLETED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_COMPLETED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_COMPLETED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_INVOICE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_FAULT = "AAAAAAAAAA";
    private static final String UPDATED_FAULT = "BBBBBBBBBB";

    private static final String DEFAULT_ACCESS_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_ACCESS_INSTRUCTIONS = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJobMockMvc;

    private Job job;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Job createEntity(EntityManager em) {
        Job job = new Job()
            .priority(DEFAULT_PRIORITY)
            .created(DEFAULT_CREATED)
            .occupiersName(DEFAULT_OCCUPIERS_NAME)
            .occupiersHomePhone(DEFAULT_OCCUPIERS_HOME_PHONE)
            .occupiersWorkPhone(DEFAULT_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(DEFAULT_OCCUPIERS_MOBILE_PHONE)
            .clientOrderId(DEFAULT_CLIENT_ORDER_ID)
            .assignedAt(DEFAULT_ASSIGNED_AT)
            .scheduled(DEFAULT_SCHEDULED)
            .completed(DEFAULT_COMPLETED)
            .invoiceNumber(DEFAULT_INVOICE_NUMBER)
            .fault(DEFAULT_FAULT)
            .accessInstructions(DEFAULT_ACCESS_INSTRUCTIONS)
            .updated(DEFAULT_UPDATED);
        return job;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Job createUpdatedEntity(EntityManager em) {
        Job job = new Job()
            .priority(UPDATED_PRIORITY)
            .created(UPDATED_CREATED)
            .occupiersName(UPDATED_OCCUPIERS_NAME)
            .occupiersHomePhone(UPDATED_OCCUPIERS_HOME_PHONE)
            .occupiersWorkPhone(UPDATED_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(UPDATED_OCCUPIERS_MOBILE_PHONE)
            .clientOrderId(UPDATED_CLIENT_ORDER_ID)
            .assignedAt(UPDATED_ASSIGNED_AT)
            .scheduled(UPDATED_SCHEDULED)
            .completed(UPDATED_COMPLETED)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .fault(UPDATED_FAULT)
            .accessInstructions(UPDATED_ACCESS_INSTRUCTIONS)
            .updated(UPDATED_UPDATED);
        return job;
    }

    @BeforeEach
    public void initTest() {
        job = createEntity(em);
    }

    @Test
    @Transactional
    void createJob() throws Exception {
        int databaseSizeBeforeCreate = jobRepository.findAll().size();
        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);
        restJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isCreated());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeCreate + 1);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testJob.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testJob.getOccupiersName()).isEqualTo(DEFAULT_OCCUPIERS_NAME);
        assertThat(testJob.getOccupiersHomePhone()).isEqualTo(DEFAULT_OCCUPIERS_HOME_PHONE);
        assertThat(testJob.getOccupiersWorkPhone()).isEqualTo(DEFAULT_OCCUPIERS_WORK_PHONE);
        assertThat(testJob.getOccupiersMobilePhone()).isEqualTo(DEFAULT_OCCUPIERS_MOBILE_PHONE);
        assertThat(testJob.getClientOrderId()).isEqualTo(DEFAULT_CLIENT_ORDER_ID);
        assertThat(testJob.getAssignedAt()).isEqualTo(DEFAULT_ASSIGNED_AT);
        assertThat(testJob.getScheduled()).isEqualTo(DEFAULT_SCHEDULED);
        assertThat(testJob.getCompleted()).isEqualTo(DEFAULT_COMPLETED);
        assertThat(testJob.getInvoiceNumber()).isEqualTo(DEFAULT_INVOICE_NUMBER);
        assertThat(testJob.getFault()).isEqualTo(DEFAULT_FAULT);
        assertThat(testJob.getAccessInstructions()).isEqualTo(DEFAULT_ACCESS_INSTRUCTIONS);
        assertThat(testJob.getUpdated()).isEqualTo(DEFAULT_UPDATED);
    }

    @Test
    @Transactional
    void createJobWithExistingId() throws Exception {
        // Create the Job with an existing ID
        job.setId(1L);
        JobDTO jobDTO = jobMapper.toDto(job);

        int databaseSizeBeforeCreate = jobRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllJobs() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList
        restJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].occupiersName").value(hasItem(DEFAULT_OCCUPIERS_NAME)))
            .andExpect(jsonPath("$.[*].occupiersHomePhone").value(hasItem(DEFAULT_OCCUPIERS_HOME_PHONE)))
            .andExpect(jsonPath("$.[*].occupiersWorkPhone").value(hasItem(DEFAULT_OCCUPIERS_WORK_PHONE)))
            .andExpect(jsonPath("$.[*].occupiersMobilePhone").value(hasItem(DEFAULT_OCCUPIERS_MOBILE_PHONE)))
            .andExpect(jsonPath("$.[*].clientOrderId").value(hasItem(DEFAULT_CLIENT_ORDER_ID)))
            .andExpect(jsonPath("$.[*].assignedAt").value(hasItem(sameInstant(DEFAULT_ASSIGNED_AT))))
            .andExpect(jsonPath("$.[*].scheduled").value(hasItem(sameInstant(DEFAULT_SCHEDULED))))
            .andExpect(jsonPath("$.[*].completed").value(hasItem(sameInstant(DEFAULT_COMPLETED))))
            .andExpect(jsonPath("$.[*].invoiceNumber").value(hasItem(DEFAULT_INVOICE_NUMBER)))
            .andExpect(jsonPath("$.[*].fault").value(hasItem(DEFAULT_FAULT.toString())))
            .andExpect(jsonPath("$.[*].accessInstructions").value(hasItem(DEFAULT_ACCESS_INSTRUCTIONS.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))));
    }

    @Test
    @Transactional
    void getJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get the job
        restJobMockMvc
            .perform(get(ENTITY_API_URL_ID, job.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(job.getId().intValue()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.occupiersName").value(DEFAULT_OCCUPIERS_NAME))
            .andExpect(jsonPath("$.occupiersHomePhone").value(DEFAULT_OCCUPIERS_HOME_PHONE))
            .andExpect(jsonPath("$.occupiersWorkPhone").value(DEFAULT_OCCUPIERS_WORK_PHONE))
            .andExpect(jsonPath("$.occupiersMobilePhone").value(DEFAULT_OCCUPIERS_MOBILE_PHONE))
            .andExpect(jsonPath("$.clientOrderId").value(DEFAULT_CLIENT_ORDER_ID))
            .andExpect(jsonPath("$.assignedAt").value(sameInstant(DEFAULT_ASSIGNED_AT)))
            .andExpect(jsonPath("$.scheduled").value(sameInstant(DEFAULT_SCHEDULED)))
            .andExpect(jsonPath("$.completed").value(sameInstant(DEFAULT_COMPLETED)))
            .andExpect(jsonPath("$.invoiceNumber").value(DEFAULT_INVOICE_NUMBER))
            .andExpect(jsonPath("$.fault").value(DEFAULT_FAULT.toString()))
            .andExpect(jsonPath("$.accessInstructions").value(DEFAULT_ACCESS_INSTRUCTIONS.toString()))
            .andExpect(jsonPath("$.updated").value(sameInstant(DEFAULT_UPDATED)));
    }

    @Test
    @Transactional
    void getJobsByIdFiltering() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        Long id = job.getId();

        defaultJobShouldBeFound("id.equals=" + id);
        defaultJobShouldNotBeFound("id.notEquals=" + id);

        defaultJobShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultJobShouldNotBeFound("id.greaterThan=" + id);

        defaultJobShouldBeFound("id.lessThanOrEqual=" + id);
        defaultJobShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllJobsByPriorityIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where priority equals to DEFAULT_PRIORITY
        defaultJobShouldBeFound("priority.equals=" + DEFAULT_PRIORITY);

        // Get all the jobList where priority equals to UPDATED_PRIORITY
        defaultJobShouldNotBeFound("priority.equals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllJobsByPriorityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where priority not equals to DEFAULT_PRIORITY
        defaultJobShouldNotBeFound("priority.notEquals=" + DEFAULT_PRIORITY);

        // Get all the jobList where priority not equals to UPDATED_PRIORITY
        defaultJobShouldBeFound("priority.notEquals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllJobsByPriorityIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where priority in DEFAULT_PRIORITY or UPDATED_PRIORITY
        defaultJobShouldBeFound("priority.in=" + DEFAULT_PRIORITY + "," + UPDATED_PRIORITY);

        // Get all the jobList where priority equals to UPDATED_PRIORITY
        defaultJobShouldNotBeFound("priority.in=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllJobsByPriorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where priority is not null
        defaultJobShouldBeFound("priority.specified=true");

        // Get all the jobList where priority is null
        defaultJobShouldNotBeFound("priority.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where created equals to DEFAULT_CREATED
        defaultJobShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the jobList where created equals to UPDATED_CREATED
        defaultJobShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where created not equals to DEFAULT_CREATED
        defaultJobShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the jobList where created not equals to UPDATED_CREATED
        defaultJobShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultJobShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the jobList where created equals to UPDATED_CREATED
        defaultJobShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where created is not null
        defaultJobShouldBeFound("created.specified=true");

        // Get all the jobList where created is null
        defaultJobShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByCreatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where created is greater than or equal to DEFAULT_CREATED
        defaultJobShouldBeFound("created.greaterThanOrEqual=" + DEFAULT_CREATED);

        // Get all the jobList where created is greater than or equal to UPDATED_CREATED
        defaultJobShouldNotBeFound("created.greaterThanOrEqual=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where created is less than or equal to DEFAULT_CREATED
        defaultJobShouldBeFound("created.lessThanOrEqual=" + DEFAULT_CREATED);

        // Get all the jobList where created is less than or equal to SMALLER_CREATED
        defaultJobShouldNotBeFound("created.lessThanOrEqual=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedIsLessThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where created is less than DEFAULT_CREATED
        defaultJobShouldNotBeFound("created.lessThan=" + DEFAULT_CREATED);

        // Get all the jobList where created is less than UPDATED_CREATED
        defaultJobShouldBeFound("created.lessThan=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    void getAllJobsByCreatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where created is greater than DEFAULT_CREATED
        defaultJobShouldNotBeFound("created.greaterThan=" + DEFAULT_CREATED);

        // Get all the jobList where created is greater than SMALLER_CREATED
        defaultJobShouldBeFound("created.greaterThan=" + SMALLER_CREATED);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersNameIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersName equals to DEFAULT_OCCUPIERS_NAME
        defaultJobShouldBeFound("occupiersName.equals=" + DEFAULT_OCCUPIERS_NAME);

        // Get all the jobList where occupiersName equals to UPDATED_OCCUPIERS_NAME
        defaultJobShouldNotBeFound("occupiersName.equals=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersName not equals to DEFAULT_OCCUPIERS_NAME
        defaultJobShouldNotBeFound("occupiersName.notEquals=" + DEFAULT_OCCUPIERS_NAME);

        // Get all the jobList where occupiersName not equals to UPDATED_OCCUPIERS_NAME
        defaultJobShouldBeFound("occupiersName.notEquals=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersNameIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersName in DEFAULT_OCCUPIERS_NAME or UPDATED_OCCUPIERS_NAME
        defaultJobShouldBeFound("occupiersName.in=" + DEFAULT_OCCUPIERS_NAME + "," + UPDATED_OCCUPIERS_NAME);

        // Get all the jobList where occupiersName equals to UPDATED_OCCUPIERS_NAME
        defaultJobShouldNotBeFound("occupiersName.in=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersName is not null
        defaultJobShouldBeFound("occupiersName.specified=true");

        // Get all the jobList where occupiersName is null
        defaultJobShouldNotBeFound("occupiersName.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersNameContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersName contains DEFAULT_OCCUPIERS_NAME
        defaultJobShouldBeFound("occupiersName.contains=" + DEFAULT_OCCUPIERS_NAME);

        // Get all the jobList where occupiersName contains UPDATED_OCCUPIERS_NAME
        defaultJobShouldNotBeFound("occupiersName.contains=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersNameNotContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersName does not contain DEFAULT_OCCUPIERS_NAME
        defaultJobShouldNotBeFound("occupiersName.doesNotContain=" + DEFAULT_OCCUPIERS_NAME);

        // Get all the jobList where occupiersName does not contain UPDATED_OCCUPIERS_NAME
        defaultJobShouldBeFound("occupiersName.doesNotContain=" + UPDATED_OCCUPIERS_NAME);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersHomePhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersHomePhone equals to DEFAULT_OCCUPIERS_HOME_PHONE
        defaultJobShouldBeFound("occupiersHomePhone.equals=" + DEFAULT_OCCUPIERS_HOME_PHONE);

        // Get all the jobList where occupiersHomePhone equals to UPDATED_OCCUPIERS_HOME_PHONE
        defaultJobShouldNotBeFound("occupiersHomePhone.equals=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersHomePhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersHomePhone not equals to DEFAULT_OCCUPIERS_HOME_PHONE
        defaultJobShouldNotBeFound("occupiersHomePhone.notEquals=" + DEFAULT_OCCUPIERS_HOME_PHONE);

        // Get all the jobList where occupiersHomePhone not equals to UPDATED_OCCUPIERS_HOME_PHONE
        defaultJobShouldBeFound("occupiersHomePhone.notEquals=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersHomePhoneIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersHomePhone in DEFAULT_OCCUPIERS_HOME_PHONE or UPDATED_OCCUPIERS_HOME_PHONE
        defaultJobShouldBeFound("occupiersHomePhone.in=" + DEFAULT_OCCUPIERS_HOME_PHONE + "," + UPDATED_OCCUPIERS_HOME_PHONE);

        // Get all the jobList where occupiersHomePhone equals to UPDATED_OCCUPIERS_HOME_PHONE
        defaultJobShouldNotBeFound("occupiersHomePhone.in=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersHomePhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersHomePhone is not null
        defaultJobShouldBeFound("occupiersHomePhone.specified=true");

        // Get all the jobList where occupiersHomePhone is null
        defaultJobShouldNotBeFound("occupiersHomePhone.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersHomePhoneContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersHomePhone contains DEFAULT_OCCUPIERS_HOME_PHONE
        defaultJobShouldBeFound("occupiersHomePhone.contains=" + DEFAULT_OCCUPIERS_HOME_PHONE);

        // Get all the jobList where occupiersHomePhone contains UPDATED_OCCUPIERS_HOME_PHONE
        defaultJobShouldNotBeFound("occupiersHomePhone.contains=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersHomePhoneNotContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersHomePhone does not contain DEFAULT_OCCUPIERS_HOME_PHONE
        defaultJobShouldNotBeFound("occupiersHomePhone.doesNotContain=" + DEFAULT_OCCUPIERS_HOME_PHONE);

        // Get all the jobList where occupiersHomePhone does not contain UPDATED_OCCUPIERS_HOME_PHONE
        defaultJobShouldBeFound("occupiersHomePhone.doesNotContain=" + UPDATED_OCCUPIERS_HOME_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersWorkPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersWorkPhone equals to DEFAULT_OCCUPIERS_WORK_PHONE
        defaultJobShouldBeFound("occupiersWorkPhone.equals=" + DEFAULT_OCCUPIERS_WORK_PHONE);

        // Get all the jobList where occupiersWorkPhone equals to UPDATED_OCCUPIERS_WORK_PHONE
        defaultJobShouldNotBeFound("occupiersWorkPhone.equals=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersWorkPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersWorkPhone not equals to DEFAULT_OCCUPIERS_WORK_PHONE
        defaultJobShouldNotBeFound("occupiersWorkPhone.notEquals=" + DEFAULT_OCCUPIERS_WORK_PHONE);

        // Get all the jobList where occupiersWorkPhone not equals to UPDATED_OCCUPIERS_WORK_PHONE
        defaultJobShouldBeFound("occupiersWorkPhone.notEquals=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersWorkPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersWorkPhone in DEFAULT_OCCUPIERS_WORK_PHONE or UPDATED_OCCUPIERS_WORK_PHONE
        defaultJobShouldBeFound("occupiersWorkPhone.in=" + DEFAULT_OCCUPIERS_WORK_PHONE + "," + UPDATED_OCCUPIERS_WORK_PHONE);

        // Get all the jobList where occupiersWorkPhone equals to UPDATED_OCCUPIERS_WORK_PHONE
        defaultJobShouldNotBeFound("occupiersWorkPhone.in=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersWorkPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersWorkPhone is not null
        defaultJobShouldBeFound("occupiersWorkPhone.specified=true");

        // Get all the jobList where occupiersWorkPhone is null
        defaultJobShouldNotBeFound("occupiersWorkPhone.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersWorkPhoneContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersWorkPhone contains DEFAULT_OCCUPIERS_WORK_PHONE
        defaultJobShouldBeFound("occupiersWorkPhone.contains=" + DEFAULT_OCCUPIERS_WORK_PHONE);

        // Get all the jobList where occupiersWorkPhone contains UPDATED_OCCUPIERS_WORK_PHONE
        defaultJobShouldNotBeFound("occupiersWorkPhone.contains=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersWorkPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersWorkPhone does not contain DEFAULT_OCCUPIERS_WORK_PHONE
        defaultJobShouldNotBeFound("occupiersWorkPhone.doesNotContain=" + DEFAULT_OCCUPIERS_WORK_PHONE);

        // Get all the jobList where occupiersWorkPhone does not contain UPDATED_OCCUPIERS_WORK_PHONE
        defaultJobShouldBeFound("occupiersWorkPhone.doesNotContain=" + UPDATED_OCCUPIERS_WORK_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersMobilePhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersMobilePhone equals to DEFAULT_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldBeFound("occupiersMobilePhone.equals=" + DEFAULT_OCCUPIERS_MOBILE_PHONE);

        // Get all the jobList where occupiersMobilePhone equals to UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldNotBeFound("occupiersMobilePhone.equals=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersMobilePhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersMobilePhone not equals to DEFAULT_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldNotBeFound("occupiersMobilePhone.notEquals=" + DEFAULT_OCCUPIERS_MOBILE_PHONE);

        // Get all the jobList where occupiersMobilePhone not equals to UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldBeFound("occupiersMobilePhone.notEquals=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersMobilePhoneIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersMobilePhone in DEFAULT_OCCUPIERS_MOBILE_PHONE or UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldBeFound("occupiersMobilePhone.in=" + DEFAULT_OCCUPIERS_MOBILE_PHONE + "," + UPDATED_OCCUPIERS_MOBILE_PHONE);

        // Get all the jobList where occupiersMobilePhone equals to UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldNotBeFound("occupiersMobilePhone.in=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersMobilePhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersMobilePhone is not null
        defaultJobShouldBeFound("occupiersMobilePhone.specified=true");

        // Get all the jobList where occupiersMobilePhone is null
        defaultJobShouldNotBeFound("occupiersMobilePhone.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersMobilePhoneContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersMobilePhone contains DEFAULT_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldBeFound("occupiersMobilePhone.contains=" + DEFAULT_OCCUPIERS_MOBILE_PHONE);

        // Get all the jobList where occupiersMobilePhone contains UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldNotBeFound("occupiersMobilePhone.contains=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByOccupiersMobilePhoneNotContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where occupiersMobilePhone does not contain DEFAULT_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldNotBeFound("occupiersMobilePhone.doesNotContain=" + DEFAULT_OCCUPIERS_MOBILE_PHONE);

        // Get all the jobList where occupiersMobilePhone does not contain UPDATED_OCCUPIERS_MOBILE_PHONE
        defaultJobShouldBeFound("occupiersMobilePhone.doesNotContain=" + UPDATED_OCCUPIERS_MOBILE_PHONE);
    }

    @Test
    @Transactional
    void getAllJobsByClientOrderIdIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where clientOrderId equals to DEFAULT_CLIENT_ORDER_ID
        defaultJobShouldBeFound("clientOrderId.equals=" + DEFAULT_CLIENT_ORDER_ID);

        // Get all the jobList where clientOrderId equals to UPDATED_CLIENT_ORDER_ID
        defaultJobShouldNotBeFound("clientOrderId.equals=" + UPDATED_CLIENT_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllJobsByClientOrderIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where clientOrderId not equals to DEFAULT_CLIENT_ORDER_ID
        defaultJobShouldNotBeFound("clientOrderId.notEquals=" + DEFAULT_CLIENT_ORDER_ID);

        // Get all the jobList where clientOrderId not equals to UPDATED_CLIENT_ORDER_ID
        defaultJobShouldBeFound("clientOrderId.notEquals=" + UPDATED_CLIENT_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllJobsByClientOrderIdIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where clientOrderId in DEFAULT_CLIENT_ORDER_ID or UPDATED_CLIENT_ORDER_ID
        defaultJobShouldBeFound("clientOrderId.in=" + DEFAULT_CLIENT_ORDER_ID + "," + UPDATED_CLIENT_ORDER_ID);

        // Get all the jobList where clientOrderId equals to UPDATED_CLIENT_ORDER_ID
        defaultJobShouldNotBeFound("clientOrderId.in=" + UPDATED_CLIENT_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllJobsByClientOrderIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where clientOrderId is not null
        defaultJobShouldBeFound("clientOrderId.specified=true");

        // Get all the jobList where clientOrderId is null
        defaultJobShouldNotBeFound("clientOrderId.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByClientOrderIdContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where clientOrderId contains DEFAULT_CLIENT_ORDER_ID
        defaultJobShouldBeFound("clientOrderId.contains=" + DEFAULT_CLIENT_ORDER_ID);

        // Get all the jobList where clientOrderId contains UPDATED_CLIENT_ORDER_ID
        defaultJobShouldNotBeFound("clientOrderId.contains=" + UPDATED_CLIENT_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllJobsByClientOrderIdNotContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where clientOrderId does not contain DEFAULT_CLIENT_ORDER_ID
        defaultJobShouldNotBeFound("clientOrderId.doesNotContain=" + DEFAULT_CLIENT_ORDER_ID);

        // Get all the jobList where clientOrderId does not contain UPDATED_CLIENT_ORDER_ID
        defaultJobShouldBeFound("clientOrderId.doesNotContain=" + UPDATED_CLIENT_ORDER_ID);
    }

    @Test
    @Transactional
    void getAllJobsByAssignedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where assignedAt equals to DEFAULT_ASSIGNED_AT
        defaultJobShouldBeFound("assignedAt.equals=" + DEFAULT_ASSIGNED_AT);

        // Get all the jobList where assignedAt equals to UPDATED_ASSIGNED_AT
        defaultJobShouldNotBeFound("assignedAt.equals=" + UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void getAllJobsByAssignedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where assignedAt not equals to DEFAULT_ASSIGNED_AT
        defaultJobShouldNotBeFound("assignedAt.notEquals=" + DEFAULT_ASSIGNED_AT);

        // Get all the jobList where assignedAt not equals to UPDATED_ASSIGNED_AT
        defaultJobShouldBeFound("assignedAt.notEquals=" + UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void getAllJobsByAssignedAtIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where assignedAt in DEFAULT_ASSIGNED_AT or UPDATED_ASSIGNED_AT
        defaultJobShouldBeFound("assignedAt.in=" + DEFAULT_ASSIGNED_AT + "," + UPDATED_ASSIGNED_AT);

        // Get all the jobList where assignedAt equals to UPDATED_ASSIGNED_AT
        defaultJobShouldNotBeFound("assignedAt.in=" + UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void getAllJobsByAssignedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where assignedAt is not null
        defaultJobShouldBeFound("assignedAt.specified=true");

        // Get all the jobList where assignedAt is null
        defaultJobShouldNotBeFound("assignedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByAssignedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where assignedAt is greater than or equal to DEFAULT_ASSIGNED_AT
        defaultJobShouldBeFound("assignedAt.greaterThanOrEqual=" + DEFAULT_ASSIGNED_AT);

        // Get all the jobList where assignedAt is greater than or equal to UPDATED_ASSIGNED_AT
        defaultJobShouldNotBeFound("assignedAt.greaterThanOrEqual=" + UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void getAllJobsByAssignedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where assignedAt is less than or equal to DEFAULT_ASSIGNED_AT
        defaultJobShouldBeFound("assignedAt.lessThanOrEqual=" + DEFAULT_ASSIGNED_AT);

        // Get all the jobList where assignedAt is less than or equal to SMALLER_ASSIGNED_AT
        defaultJobShouldNotBeFound("assignedAt.lessThanOrEqual=" + SMALLER_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void getAllJobsByAssignedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where assignedAt is less than DEFAULT_ASSIGNED_AT
        defaultJobShouldNotBeFound("assignedAt.lessThan=" + DEFAULT_ASSIGNED_AT);

        // Get all the jobList where assignedAt is less than UPDATED_ASSIGNED_AT
        defaultJobShouldBeFound("assignedAt.lessThan=" + UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void getAllJobsByAssignedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where assignedAt is greater than DEFAULT_ASSIGNED_AT
        defaultJobShouldNotBeFound("assignedAt.greaterThan=" + DEFAULT_ASSIGNED_AT);

        // Get all the jobList where assignedAt is greater than SMALLER_ASSIGNED_AT
        defaultJobShouldBeFound("assignedAt.greaterThan=" + SMALLER_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void getAllJobsByScheduledIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where scheduled equals to DEFAULT_SCHEDULED
        defaultJobShouldBeFound("scheduled.equals=" + DEFAULT_SCHEDULED);

        // Get all the jobList where scheduled equals to UPDATED_SCHEDULED
        defaultJobShouldNotBeFound("scheduled.equals=" + UPDATED_SCHEDULED);
    }

    @Test
    @Transactional
    void getAllJobsByScheduledIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where scheduled not equals to DEFAULT_SCHEDULED
        defaultJobShouldNotBeFound("scheduled.notEquals=" + DEFAULT_SCHEDULED);

        // Get all the jobList where scheduled not equals to UPDATED_SCHEDULED
        defaultJobShouldBeFound("scheduled.notEquals=" + UPDATED_SCHEDULED);
    }

    @Test
    @Transactional
    void getAllJobsByScheduledIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where scheduled in DEFAULT_SCHEDULED or UPDATED_SCHEDULED
        defaultJobShouldBeFound("scheduled.in=" + DEFAULT_SCHEDULED + "," + UPDATED_SCHEDULED);

        // Get all the jobList where scheduled equals to UPDATED_SCHEDULED
        defaultJobShouldNotBeFound("scheduled.in=" + UPDATED_SCHEDULED);
    }

    @Test
    @Transactional
    void getAllJobsByScheduledIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where scheduled is not null
        defaultJobShouldBeFound("scheduled.specified=true");

        // Get all the jobList where scheduled is null
        defaultJobShouldNotBeFound("scheduled.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByScheduledIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where scheduled is greater than or equal to DEFAULT_SCHEDULED
        defaultJobShouldBeFound("scheduled.greaterThanOrEqual=" + DEFAULT_SCHEDULED);

        // Get all the jobList where scheduled is greater than or equal to UPDATED_SCHEDULED
        defaultJobShouldNotBeFound("scheduled.greaterThanOrEqual=" + UPDATED_SCHEDULED);
    }

    @Test
    @Transactional
    void getAllJobsByScheduledIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where scheduled is less than or equal to DEFAULT_SCHEDULED
        defaultJobShouldBeFound("scheduled.lessThanOrEqual=" + DEFAULT_SCHEDULED);

        // Get all the jobList where scheduled is less than or equal to SMALLER_SCHEDULED
        defaultJobShouldNotBeFound("scheduled.lessThanOrEqual=" + SMALLER_SCHEDULED);
    }

    @Test
    @Transactional
    void getAllJobsByScheduledIsLessThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where scheduled is less than DEFAULT_SCHEDULED
        defaultJobShouldNotBeFound("scheduled.lessThan=" + DEFAULT_SCHEDULED);

        // Get all the jobList where scheduled is less than UPDATED_SCHEDULED
        defaultJobShouldBeFound("scheduled.lessThan=" + UPDATED_SCHEDULED);
    }

    @Test
    @Transactional
    void getAllJobsByScheduledIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where scheduled is greater than DEFAULT_SCHEDULED
        defaultJobShouldNotBeFound("scheduled.greaterThan=" + DEFAULT_SCHEDULED);

        // Get all the jobList where scheduled is greater than SMALLER_SCHEDULED
        defaultJobShouldBeFound("scheduled.greaterThan=" + SMALLER_SCHEDULED);
    }

    @Test
    @Transactional
    void getAllJobsByCompletedIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where completed equals to DEFAULT_COMPLETED
        defaultJobShouldBeFound("completed.equals=" + DEFAULT_COMPLETED);

        // Get all the jobList where completed equals to UPDATED_COMPLETED
        defaultJobShouldNotBeFound("completed.equals=" + UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void getAllJobsByCompletedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where completed not equals to DEFAULT_COMPLETED
        defaultJobShouldNotBeFound("completed.notEquals=" + DEFAULT_COMPLETED);

        // Get all the jobList where completed not equals to UPDATED_COMPLETED
        defaultJobShouldBeFound("completed.notEquals=" + UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void getAllJobsByCompletedIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where completed in DEFAULT_COMPLETED or UPDATED_COMPLETED
        defaultJobShouldBeFound("completed.in=" + DEFAULT_COMPLETED + "," + UPDATED_COMPLETED);

        // Get all the jobList where completed equals to UPDATED_COMPLETED
        defaultJobShouldNotBeFound("completed.in=" + UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void getAllJobsByCompletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where completed is not null
        defaultJobShouldBeFound("completed.specified=true");

        // Get all the jobList where completed is null
        defaultJobShouldNotBeFound("completed.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByCompletedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where completed is greater than or equal to DEFAULT_COMPLETED
        defaultJobShouldBeFound("completed.greaterThanOrEqual=" + DEFAULT_COMPLETED);

        // Get all the jobList where completed is greater than or equal to UPDATED_COMPLETED
        defaultJobShouldNotBeFound("completed.greaterThanOrEqual=" + UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void getAllJobsByCompletedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where completed is less than or equal to DEFAULT_COMPLETED
        defaultJobShouldBeFound("completed.lessThanOrEqual=" + DEFAULT_COMPLETED);

        // Get all the jobList where completed is less than or equal to SMALLER_COMPLETED
        defaultJobShouldNotBeFound("completed.lessThanOrEqual=" + SMALLER_COMPLETED);
    }

    @Test
    @Transactional
    void getAllJobsByCompletedIsLessThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where completed is less than DEFAULT_COMPLETED
        defaultJobShouldNotBeFound("completed.lessThan=" + DEFAULT_COMPLETED);

        // Get all the jobList where completed is less than UPDATED_COMPLETED
        defaultJobShouldBeFound("completed.lessThan=" + UPDATED_COMPLETED);
    }

    @Test
    @Transactional
    void getAllJobsByCompletedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where completed is greater than DEFAULT_COMPLETED
        defaultJobShouldNotBeFound("completed.greaterThan=" + DEFAULT_COMPLETED);

        // Get all the jobList where completed is greater than SMALLER_COMPLETED
        defaultJobShouldBeFound("completed.greaterThan=" + SMALLER_COMPLETED);
    }

    @Test
    @Transactional
    void getAllJobsByInvoiceNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where invoiceNumber equals to DEFAULT_INVOICE_NUMBER
        defaultJobShouldBeFound("invoiceNumber.equals=" + DEFAULT_INVOICE_NUMBER);

        // Get all the jobList where invoiceNumber equals to UPDATED_INVOICE_NUMBER
        defaultJobShouldNotBeFound("invoiceNumber.equals=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllJobsByInvoiceNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where invoiceNumber not equals to DEFAULT_INVOICE_NUMBER
        defaultJobShouldNotBeFound("invoiceNumber.notEquals=" + DEFAULT_INVOICE_NUMBER);

        // Get all the jobList where invoiceNumber not equals to UPDATED_INVOICE_NUMBER
        defaultJobShouldBeFound("invoiceNumber.notEquals=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllJobsByInvoiceNumberIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where invoiceNumber in DEFAULT_INVOICE_NUMBER or UPDATED_INVOICE_NUMBER
        defaultJobShouldBeFound("invoiceNumber.in=" + DEFAULT_INVOICE_NUMBER + "," + UPDATED_INVOICE_NUMBER);

        // Get all the jobList where invoiceNumber equals to UPDATED_INVOICE_NUMBER
        defaultJobShouldNotBeFound("invoiceNumber.in=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllJobsByInvoiceNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where invoiceNumber is not null
        defaultJobShouldBeFound("invoiceNumber.specified=true");

        // Get all the jobList where invoiceNumber is null
        defaultJobShouldNotBeFound("invoiceNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByInvoiceNumberContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where invoiceNumber contains DEFAULT_INVOICE_NUMBER
        defaultJobShouldBeFound("invoiceNumber.contains=" + DEFAULT_INVOICE_NUMBER);

        // Get all the jobList where invoiceNumber contains UPDATED_INVOICE_NUMBER
        defaultJobShouldNotBeFound("invoiceNumber.contains=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllJobsByInvoiceNumberNotContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where invoiceNumber does not contain DEFAULT_INVOICE_NUMBER
        defaultJobShouldNotBeFound("invoiceNumber.doesNotContain=" + DEFAULT_INVOICE_NUMBER);

        // Get all the jobList where invoiceNumber does not contain UPDATED_INVOICE_NUMBER
        defaultJobShouldBeFound("invoiceNumber.doesNotContain=" + UPDATED_INVOICE_NUMBER);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where updated equals to DEFAULT_UPDATED
        defaultJobShouldBeFound("updated.equals=" + DEFAULT_UPDATED);

        // Get all the jobList where updated equals to UPDATED_UPDATED
        defaultJobShouldNotBeFound("updated.equals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where updated not equals to DEFAULT_UPDATED
        defaultJobShouldNotBeFound("updated.notEquals=" + DEFAULT_UPDATED);

        // Get all the jobList where updated not equals to UPDATED_UPDATED
        defaultJobShouldBeFound("updated.notEquals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where updated in DEFAULT_UPDATED or UPDATED_UPDATED
        defaultJobShouldBeFound("updated.in=" + DEFAULT_UPDATED + "," + UPDATED_UPDATED);

        // Get all the jobList where updated equals to UPDATED_UPDATED
        defaultJobShouldNotBeFound("updated.in=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where updated is not null
        defaultJobShouldBeFound("updated.specified=true");

        // Get all the jobList where updated is null
        defaultJobShouldNotBeFound("updated.specified=false");
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where updated is greater than or equal to DEFAULT_UPDATED
        defaultJobShouldBeFound("updated.greaterThanOrEqual=" + DEFAULT_UPDATED);

        // Get all the jobList where updated is greater than or equal to UPDATED_UPDATED
        defaultJobShouldNotBeFound("updated.greaterThanOrEqual=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where updated is less than or equal to DEFAULT_UPDATED
        defaultJobShouldBeFound("updated.lessThanOrEqual=" + DEFAULT_UPDATED);

        // Get all the jobList where updated is less than or equal to SMALLER_UPDATED
        defaultJobShouldNotBeFound("updated.lessThanOrEqual=" + SMALLER_UPDATED);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedIsLessThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where updated is less than DEFAULT_UPDATED
        defaultJobShouldNotBeFound("updated.lessThan=" + DEFAULT_UPDATED);

        // Get all the jobList where updated is less than UPDATED_UPDATED
        defaultJobShouldBeFound("updated.lessThan=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void getAllJobsByUpdatedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where updated is greater than DEFAULT_UPDATED
        defaultJobShouldNotBeFound("updated.greaterThan=" + DEFAULT_UPDATED);

        // Get all the jobList where updated is greater than SMALLER_UPDATED
        defaultJobShouldBeFound("updated.greaterThan=" + SMALLER_UPDATED);
    }

    @Test
    @Transactional
    void getAllJobsByInspectionIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);
        Inspection inspection;
        if (TestUtil.findAll(em, Inspection.class).isEmpty()) {
            inspection = InspectionResourceIT.createEntity(em);
            em.persist(inspection);
            em.flush();
        } else {
            inspection = TestUtil.findAll(em, Inspection.class).get(0);
        }
        em.persist(inspection);
        em.flush();
        job.addInspection(inspection);
        jobRepository.saveAndFlush(job);
        Long inspectionId = inspection.getId();

        // Get all the jobList where inspection equals to inspectionId
        defaultJobShouldBeFound("inspectionId.equals=" + inspectionId);

        // Get all the jobList where inspection equals to (inspectionId + 1)
        defaultJobShouldNotBeFound("inspectionId.equals=" + (inspectionId + 1));
    }

    @Test
    @Transactional
    void getAllJobsByVisitsIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);
        Visit visits;
        if (TestUtil.findAll(em, Visit.class).isEmpty()) {
            visits = VisitResourceIT.createEntity(em);
            em.persist(visits);
            em.flush();
        } else {
            visits = TestUtil.findAll(em, Visit.class).get(0);
        }
        em.persist(visits);
        em.flush();
        job.addVisits(visits);
        jobRepository.saveAndFlush(job);
        Long visitsId = visits.getId();

        // Get all the jobList where visits equals to visitsId
        defaultJobShouldBeFound("visitsId.equals=" + visitsId);

        // Get all the jobList where visits equals to (visitsId + 1)
        defaultJobShouldNotBeFound("visitsId.equals=" + (visitsId + 1));
    }

    @Test
    @Transactional
    void getAllJobsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);
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
        job.setCreatedBy(createdBy);
        jobRepository.saveAndFlush(job);
        Long createdById = createdBy.getId();

        // Get all the jobList where createdBy equals to createdById
        defaultJobShouldBeFound("createdById.equals=" + createdById);

        // Get all the jobList where createdBy equals to (createdById + 1)
        defaultJobShouldNotBeFound("createdById.equals=" + (createdById + 1));
    }

    @Test
    @Transactional
    void getAllJobsByAssignedToIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);
        Engineer assignedTo;
        if (TestUtil.findAll(em, Engineer.class).isEmpty()) {
            assignedTo = EngineerResourceIT.createEntity(em);
            em.persist(assignedTo);
            em.flush();
        } else {
            assignedTo = TestUtil.findAll(em, Engineer.class).get(0);
        }
        em.persist(assignedTo);
        em.flush();
        job.setAssignedTo(assignedTo);
        jobRepository.saveAndFlush(job);
        Long assignedToId = assignedTo.getId();

        // Get all the jobList where assignedTo equals to assignedToId
        defaultJobShouldBeFound("assignedToId.equals=" + assignedToId);

        // Get all the jobList where assignedTo equals to (assignedToId + 1)
        defaultJobShouldNotBeFound("assignedToId.equals=" + (assignedToId + 1));
    }

    @Test
    @Transactional
    void getAllJobsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);
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
        job.setAddress(address);
        jobRepository.saveAndFlush(job);
        Long addressId = address.getId();

        // Get all the jobList where address equals to addressId
        defaultJobShouldBeFound("addressId.equals=" + addressId);

        // Get all the jobList where address equals to (addressId + 1)
        defaultJobShouldNotBeFound("addressId.equals=" + (addressId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJobShouldBeFound(String filter) throws Exception {
        restJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].occupiersName").value(hasItem(DEFAULT_OCCUPIERS_NAME)))
            .andExpect(jsonPath("$.[*].occupiersHomePhone").value(hasItem(DEFAULT_OCCUPIERS_HOME_PHONE)))
            .andExpect(jsonPath("$.[*].occupiersWorkPhone").value(hasItem(DEFAULT_OCCUPIERS_WORK_PHONE)))
            .andExpect(jsonPath("$.[*].occupiersMobilePhone").value(hasItem(DEFAULT_OCCUPIERS_MOBILE_PHONE)))
            .andExpect(jsonPath("$.[*].clientOrderId").value(hasItem(DEFAULT_CLIENT_ORDER_ID)))
            .andExpect(jsonPath("$.[*].assignedAt").value(hasItem(sameInstant(DEFAULT_ASSIGNED_AT))))
            .andExpect(jsonPath("$.[*].scheduled").value(hasItem(sameInstant(DEFAULT_SCHEDULED))))
            .andExpect(jsonPath("$.[*].completed").value(hasItem(sameInstant(DEFAULT_COMPLETED))))
            .andExpect(jsonPath("$.[*].invoiceNumber").value(hasItem(DEFAULT_INVOICE_NUMBER)))
            .andExpect(jsonPath("$.[*].fault").value(hasItem(DEFAULT_FAULT.toString())))
            .andExpect(jsonPath("$.[*].accessInstructions").value(hasItem(DEFAULT_ACCESS_INSTRUCTIONS.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))));

        // Check, that the count call also returns 1
        restJobMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJobShouldNotBeFound(String filter) throws Exception {
        restJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJobMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingJob() throws Exception {
        // Get the job
        restJobMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Update the job
        Job updatedJob = jobRepository.findById(job.getId()).get();
        // Disconnect from session so that the updates on updatedJob are not directly saved in db
        em.detach(updatedJob);
        updatedJob
            .priority(UPDATED_PRIORITY)
            .created(UPDATED_CREATED)
            .occupiersName(UPDATED_OCCUPIERS_NAME)
            .occupiersHomePhone(UPDATED_OCCUPIERS_HOME_PHONE)
            .occupiersWorkPhone(UPDATED_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(UPDATED_OCCUPIERS_MOBILE_PHONE)
            .clientOrderId(UPDATED_CLIENT_ORDER_ID)
            .assignedAt(UPDATED_ASSIGNED_AT)
            .scheduled(UPDATED_SCHEDULED)
            .completed(UPDATED_COMPLETED)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .fault(UPDATED_FAULT)
            .accessInstructions(UPDATED_ACCESS_INSTRUCTIONS)
            .updated(UPDATED_UPDATED);
        JobDTO jobDTO = jobMapper.toDto(updatedJob);

        restJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobDTO))
            )
            .andExpect(status().isOk());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testJob.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testJob.getOccupiersName()).isEqualTo(UPDATED_OCCUPIERS_NAME);
        assertThat(testJob.getOccupiersHomePhone()).isEqualTo(UPDATED_OCCUPIERS_HOME_PHONE);
        assertThat(testJob.getOccupiersWorkPhone()).isEqualTo(UPDATED_OCCUPIERS_WORK_PHONE);
        assertThat(testJob.getOccupiersMobilePhone()).isEqualTo(UPDATED_OCCUPIERS_MOBILE_PHONE);
        assertThat(testJob.getClientOrderId()).isEqualTo(UPDATED_CLIENT_ORDER_ID);
        assertThat(testJob.getAssignedAt()).isEqualTo(UPDATED_ASSIGNED_AT);
        assertThat(testJob.getScheduled()).isEqualTo(UPDATED_SCHEDULED);
        assertThat(testJob.getCompleted()).isEqualTo(UPDATED_COMPLETED);
        assertThat(testJob.getInvoiceNumber()).isEqualTo(UPDATED_INVOICE_NUMBER);
        assertThat(testJob.getFault()).isEqualTo(UPDATED_FAULT);
        assertThat(testJob.getAccessInstructions()).isEqualTo(UPDATED_ACCESS_INSTRUCTIONS);
        assertThat(testJob.getUpdated()).isEqualTo(UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void putNonExistingJob() throws Exception {
        int databaseSizeBeforeUpdate = jobRepository.findAll().size();
        job.setId(count.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jobDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJob() throws Exception {
        int databaseSizeBeforeUpdate = jobRepository.findAll().size();
        job.setId(count.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJob() throws Exception {
        int databaseSizeBeforeUpdate = jobRepository.findAll().size();
        job.setId(count.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJobWithPatch() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Update the job using partial update
        Job partialUpdatedJob = new Job();
        partialUpdatedJob.setId(job.getId());

        partialUpdatedJob
            .created(UPDATED_CREATED)
            .occupiersWorkPhone(UPDATED_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(UPDATED_OCCUPIERS_MOBILE_PHONE)
            .clientOrderId(UPDATED_CLIENT_ORDER_ID)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .accessInstructions(UPDATED_ACCESS_INSTRUCTIONS)
            .updated(UPDATED_UPDATED);

        restJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJob))
            )
            .andExpect(status().isOk());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testJob.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testJob.getOccupiersName()).isEqualTo(DEFAULT_OCCUPIERS_NAME);
        assertThat(testJob.getOccupiersHomePhone()).isEqualTo(DEFAULT_OCCUPIERS_HOME_PHONE);
        assertThat(testJob.getOccupiersWorkPhone()).isEqualTo(UPDATED_OCCUPIERS_WORK_PHONE);
        assertThat(testJob.getOccupiersMobilePhone()).isEqualTo(UPDATED_OCCUPIERS_MOBILE_PHONE);
        assertThat(testJob.getClientOrderId()).isEqualTo(UPDATED_CLIENT_ORDER_ID);
        assertThat(testJob.getAssignedAt()).isEqualTo(DEFAULT_ASSIGNED_AT);
        assertThat(testJob.getScheduled()).isEqualTo(DEFAULT_SCHEDULED);
        assertThat(testJob.getCompleted()).isEqualTo(DEFAULT_COMPLETED);
        assertThat(testJob.getInvoiceNumber()).isEqualTo(UPDATED_INVOICE_NUMBER);
        assertThat(testJob.getFault()).isEqualTo(DEFAULT_FAULT);
        assertThat(testJob.getAccessInstructions()).isEqualTo(UPDATED_ACCESS_INSTRUCTIONS);
        assertThat(testJob.getUpdated()).isEqualTo(UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void fullUpdateJobWithPatch() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Update the job using partial update
        Job partialUpdatedJob = new Job();
        partialUpdatedJob.setId(job.getId());

        partialUpdatedJob
            .priority(UPDATED_PRIORITY)
            .created(UPDATED_CREATED)
            .occupiersName(UPDATED_OCCUPIERS_NAME)
            .occupiersHomePhone(UPDATED_OCCUPIERS_HOME_PHONE)
            .occupiersWorkPhone(UPDATED_OCCUPIERS_WORK_PHONE)
            .occupiersMobilePhone(UPDATED_OCCUPIERS_MOBILE_PHONE)
            .clientOrderId(UPDATED_CLIENT_ORDER_ID)
            .assignedAt(UPDATED_ASSIGNED_AT)
            .scheduled(UPDATED_SCHEDULED)
            .completed(UPDATED_COMPLETED)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .fault(UPDATED_FAULT)
            .accessInstructions(UPDATED_ACCESS_INSTRUCTIONS)
            .updated(UPDATED_UPDATED);

        restJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJob.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJob))
            )
            .andExpect(status().isOk());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testJob.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testJob.getOccupiersName()).isEqualTo(UPDATED_OCCUPIERS_NAME);
        assertThat(testJob.getOccupiersHomePhone()).isEqualTo(UPDATED_OCCUPIERS_HOME_PHONE);
        assertThat(testJob.getOccupiersWorkPhone()).isEqualTo(UPDATED_OCCUPIERS_WORK_PHONE);
        assertThat(testJob.getOccupiersMobilePhone()).isEqualTo(UPDATED_OCCUPIERS_MOBILE_PHONE);
        assertThat(testJob.getClientOrderId()).isEqualTo(UPDATED_CLIENT_ORDER_ID);
        assertThat(testJob.getAssignedAt()).isEqualTo(UPDATED_ASSIGNED_AT);
        assertThat(testJob.getScheduled()).isEqualTo(UPDATED_SCHEDULED);
        assertThat(testJob.getCompleted()).isEqualTo(UPDATED_COMPLETED);
        assertThat(testJob.getInvoiceNumber()).isEqualTo(UPDATED_INVOICE_NUMBER);
        assertThat(testJob.getFault()).isEqualTo(UPDATED_FAULT);
        assertThat(testJob.getAccessInstructions()).isEqualTo(UPDATED_ACCESS_INSTRUCTIONS);
        assertThat(testJob.getUpdated()).isEqualTo(UPDATED_UPDATED);
    }

    @Test
    @Transactional
    void patchNonExistingJob() throws Exception {
        int databaseSizeBeforeUpdate = jobRepository.findAll().size();
        job.setId(count.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, jobDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJob() throws Exception {
        int databaseSizeBeforeUpdate = jobRepository.findAll().size();
        job.setId(count.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jobDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJob() throws Exception {
        int databaseSizeBeforeUpdate = jobRepository.findAll().size();
        job.setId(count.incrementAndGet());

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJobMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        int databaseSizeBeforeDelete = jobRepository.findAll().size();

        // Delete the job
        restJobMockMvc.perform(delete(ENTITY_API_URL_ID, job.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
