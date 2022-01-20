package com.awsptm.web.rest;

import static com.awsptm.web.rest.TestUtil.sameInstant;
import static com.awsptm.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.Certificate;
import com.awsptm.domain.Evidence;
import com.awsptm.domain.Job;
import com.awsptm.domain.Material;
import com.awsptm.domain.Visit;
import com.awsptm.repository.VisitRepository;
import com.awsptm.service.criteria.VisitCriteria;
import com.awsptm.service.dto.VisitDTO;
import com.awsptm.service.mapper.VisitMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link VisitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VisitResourceIT {

    private static final ZonedDateTime DEFAULT_ARRIVED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ARRIVED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ARRIVED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_DEPARTED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DEPARTED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DEPARTED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ACTIONS = "AAAAAAAAAA";
    private static final String UPDATED_ACTIONS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_LABOUR = new BigDecimal(1);
    private static final BigDecimal UPDATED_LABOUR = new BigDecimal(2);
    private static final BigDecimal SMALLER_LABOUR = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/visits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private VisitMapper visitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVisitMockMvc;

    private Visit visit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visit createEntity(EntityManager em) {
        Visit visit = new Visit()
            .arrived(DEFAULT_ARRIVED)
            .departed(DEFAULT_DEPARTED)
            .description(DEFAULT_DESCRIPTION)
            .actions(DEFAULT_ACTIONS)
            .labour(DEFAULT_LABOUR);
        return visit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visit createUpdatedEntity(EntityManager em) {
        Visit visit = new Visit()
            .arrived(UPDATED_ARRIVED)
            .departed(UPDATED_DEPARTED)
            .description(UPDATED_DESCRIPTION)
            .actions(UPDATED_ACTIONS)
            .labour(UPDATED_LABOUR);
        return visit;
    }

    @BeforeEach
    public void initTest() {
        visit = createEntity(em);
    }

    @Test
    @Transactional
    void createVisit() throws Exception {
        int databaseSizeBeforeCreate = visitRepository.findAll().size();
        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);
        restVisitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isCreated());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeCreate + 1);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getArrived()).isEqualTo(DEFAULT_ARRIVED);
        assertThat(testVisit.getDeparted()).isEqualTo(DEFAULT_DEPARTED);
        assertThat(testVisit.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testVisit.getActions()).isEqualTo(DEFAULT_ACTIONS);
        assertThat(testVisit.getLabour()).isEqualByComparingTo(DEFAULT_LABOUR);
    }

    @Test
    @Transactional
    void createVisitWithExistingId() throws Exception {
        // Create the Visit with an existing ID
        visit.setId(1L);
        VisitDTO visitDTO = visitMapper.toDto(visit);

        int databaseSizeBeforeCreate = visitRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVisitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVisits() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visit.getId().intValue())))
            .andExpect(jsonPath("$.[*].arrived").value(hasItem(sameInstant(DEFAULT_ARRIVED))))
            .andExpect(jsonPath("$.[*].departed").value(hasItem(sameInstant(DEFAULT_DEPARTED))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].actions").value(hasItem(DEFAULT_ACTIONS.toString())))
            .andExpect(jsonPath("$.[*].labour").value(hasItem(sameNumber(DEFAULT_LABOUR))));
    }

    @Test
    @Transactional
    void getVisit() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get the visit
        restVisitMockMvc
            .perform(get(ENTITY_API_URL_ID, visit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(visit.getId().intValue()))
            .andExpect(jsonPath("$.arrived").value(sameInstant(DEFAULT_ARRIVED)))
            .andExpect(jsonPath("$.departed").value(sameInstant(DEFAULT_DEPARTED)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.actions").value(DEFAULT_ACTIONS.toString()))
            .andExpect(jsonPath("$.labour").value(sameNumber(DEFAULT_LABOUR)));
    }

    @Test
    @Transactional
    void getVisitsByIdFiltering() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        Long id = visit.getId();

        defaultVisitShouldBeFound("id.equals=" + id);
        defaultVisitShouldNotBeFound("id.notEquals=" + id);

        defaultVisitShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultVisitShouldNotBeFound("id.greaterThan=" + id);

        defaultVisitShouldBeFound("id.lessThanOrEqual=" + id);
        defaultVisitShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVisitsByArrivedIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where arrived equals to DEFAULT_ARRIVED
        defaultVisitShouldBeFound("arrived.equals=" + DEFAULT_ARRIVED);

        // Get all the visitList where arrived equals to UPDATED_ARRIVED
        defaultVisitShouldNotBeFound("arrived.equals=" + UPDATED_ARRIVED);
    }

    @Test
    @Transactional
    void getAllVisitsByArrivedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where arrived not equals to DEFAULT_ARRIVED
        defaultVisitShouldNotBeFound("arrived.notEquals=" + DEFAULT_ARRIVED);

        // Get all the visitList where arrived not equals to UPDATED_ARRIVED
        defaultVisitShouldBeFound("arrived.notEquals=" + UPDATED_ARRIVED);
    }

    @Test
    @Transactional
    void getAllVisitsByArrivedIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where arrived in DEFAULT_ARRIVED or UPDATED_ARRIVED
        defaultVisitShouldBeFound("arrived.in=" + DEFAULT_ARRIVED + "," + UPDATED_ARRIVED);

        // Get all the visitList where arrived equals to UPDATED_ARRIVED
        defaultVisitShouldNotBeFound("arrived.in=" + UPDATED_ARRIVED);
    }

    @Test
    @Transactional
    void getAllVisitsByArrivedIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where arrived is not null
        defaultVisitShouldBeFound("arrived.specified=true");

        // Get all the visitList where arrived is null
        defaultVisitShouldNotBeFound("arrived.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByArrivedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where arrived is greater than or equal to DEFAULT_ARRIVED
        defaultVisitShouldBeFound("arrived.greaterThanOrEqual=" + DEFAULT_ARRIVED);

        // Get all the visitList where arrived is greater than or equal to UPDATED_ARRIVED
        defaultVisitShouldNotBeFound("arrived.greaterThanOrEqual=" + UPDATED_ARRIVED);
    }

    @Test
    @Transactional
    void getAllVisitsByArrivedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where arrived is less than or equal to DEFAULT_ARRIVED
        defaultVisitShouldBeFound("arrived.lessThanOrEqual=" + DEFAULT_ARRIVED);

        // Get all the visitList where arrived is less than or equal to SMALLER_ARRIVED
        defaultVisitShouldNotBeFound("arrived.lessThanOrEqual=" + SMALLER_ARRIVED);
    }

    @Test
    @Transactional
    void getAllVisitsByArrivedIsLessThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where arrived is less than DEFAULT_ARRIVED
        defaultVisitShouldNotBeFound("arrived.lessThan=" + DEFAULT_ARRIVED);

        // Get all the visitList where arrived is less than UPDATED_ARRIVED
        defaultVisitShouldBeFound("arrived.lessThan=" + UPDATED_ARRIVED);
    }

    @Test
    @Transactional
    void getAllVisitsByArrivedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where arrived is greater than DEFAULT_ARRIVED
        defaultVisitShouldNotBeFound("arrived.greaterThan=" + DEFAULT_ARRIVED);

        // Get all the visitList where arrived is greater than SMALLER_ARRIVED
        defaultVisitShouldBeFound("arrived.greaterThan=" + SMALLER_ARRIVED);
    }

    @Test
    @Transactional
    void getAllVisitsByDepartedIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where departed equals to DEFAULT_DEPARTED
        defaultVisitShouldBeFound("departed.equals=" + DEFAULT_DEPARTED);

        // Get all the visitList where departed equals to UPDATED_DEPARTED
        defaultVisitShouldNotBeFound("departed.equals=" + UPDATED_DEPARTED);
    }

    @Test
    @Transactional
    void getAllVisitsByDepartedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where departed not equals to DEFAULT_DEPARTED
        defaultVisitShouldNotBeFound("departed.notEquals=" + DEFAULT_DEPARTED);

        // Get all the visitList where departed not equals to UPDATED_DEPARTED
        defaultVisitShouldBeFound("departed.notEquals=" + UPDATED_DEPARTED);
    }

    @Test
    @Transactional
    void getAllVisitsByDepartedIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where departed in DEFAULT_DEPARTED or UPDATED_DEPARTED
        defaultVisitShouldBeFound("departed.in=" + DEFAULT_DEPARTED + "," + UPDATED_DEPARTED);

        // Get all the visitList where departed equals to UPDATED_DEPARTED
        defaultVisitShouldNotBeFound("departed.in=" + UPDATED_DEPARTED);
    }

    @Test
    @Transactional
    void getAllVisitsByDepartedIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where departed is not null
        defaultVisitShouldBeFound("departed.specified=true");

        // Get all the visitList where departed is null
        defaultVisitShouldNotBeFound("departed.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByDepartedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where departed is greater than or equal to DEFAULT_DEPARTED
        defaultVisitShouldBeFound("departed.greaterThanOrEqual=" + DEFAULT_DEPARTED);

        // Get all the visitList where departed is greater than or equal to UPDATED_DEPARTED
        defaultVisitShouldNotBeFound("departed.greaterThanOrEqual=" + UPDATED_DEPARTED);
    }

    @Test
    @Transactional
    void getAllVisitsByDepartedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where departed is less than or equal to DEFAULT_DEPARTED
        defaultVisitShouldBeFound("departed.lessThanOrEqual=" + DEFAULT_DEPARTED);

        // Get all the visitList where departed is less than or equal to SMALLER_DEPARTED
        defaultVisitShouldNotBeFound("departed.lessThanOrEqual=" + SMALLER_DEPARTED);
    }

    @Test
    @Transactional
    void getAllVisitsByDepartedIsLessThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where departed is less than DEFAULT_DEPARTED
        defaultVisitShouldNotBeFound("departed.lessThan=" + DEFAULT_DEPARTED);

        // Get all the visitList where departed is less than UPDATED_DEPARTED
        defaultVisitShouldBeFound("departed.lessThan=" + UPDATED_DEPARTED);
    }

    @Test
    @Transactional
    void getAllVisitsByDepartedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where departed is greater than DEFAULT_DEPARTED
        defaultVisitShouldNotBeFound("departed.greaterThan=" + DEFAULT_DEPARTED);

        // Get all the visitList where departed is greater than SMALLER_DEPARTED
        defaultVisitShouldBeFound("departed.greaterThan=" + SMALLER_DEPARTED);
    }

    @Test
    @Transactional
    void getAllVisitsByLabourIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where labour equals to DEFAULT_LABOUR
        defaultVisitShouldBeFound("labour.equals=" + DEFAULT_LABOUR);

        // Get all the visitList where labour equals to UPDATED_LABOUR
        defaultVisitShouldNotBeFound("labour.equals=" + UPDATED_LABOUR);
    }

    @Test
    @Transactional
    void getAllVisitsByLabourIsNotEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where labour not equals to DEFAULT_LABOUR
        defaultVisitShouldNotBeFound("labour.notEquals=" + DEFAULT_LABOUR);

        // Get all the visitList where labour not equals to UPDATED_LABOUR
        defaultVisitShouldBeFound("labour.notEquals=" + UPDATED_LABOUR);
    }

    @Test
    @Transactional
    void getAllVisitsByLabourIsInShouldWork() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where labour in DEFAULT_LABOUR or UPDATED_LABOUR
        defaultVisitShouldBeFound("labour.in=" + DEFAULT_LABOUR + "," + UPDATED_LABOUR);

        // Get all the visitList where labour equals to UPDATED_LABOUR
        defaultVisitShouldNotBeFound("labour.in=" + UPDATED_LABOUR);
    }

    @Test
    @Transactional
    void getAllVisitsByLabourIsNullOrNotNull() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where labour is not null
        defaultVisitShouldBeFound("labour.specified=true");

        // Get all the visitList where labour is null
        defaultVisitShouldNotBeFound("labour.specified=false");
    }

    @Test
    @Transactional
    void getAllVisitsByLabourIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where labour is greater than or equal to DEFAULT_LABOUR
        defaultVisitShouldBeFound("labour.greaterThanOrEqual=" + DEFAULT_LABOUR);

        // Get all the visitList where labour is greater than or equal to UPDATED_LABOUR
        defaultVisitShouldNotBeFound("labour.greaterThanOrEqual=" + UPDATED_LABOUR);
    }

    @Test
    @Transactional
    void getAllVisitsByLabourIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where labour is less than or equal to DEFAULT_LABOUR
        defaultVisitShouldBeFound("labour.lessThanOrEqual=" + DEFAULT_LABOUR);

        // Get all the visitList where labour is less than or equal to SMALLER_LABOUR
        defaultVisitShouldNotBeFound("labour.lessThanOrEqual=" + SMALLER_LABOUR);
    }

    @Test
    @Transactional
    void getAllVisitsByLabourIsLessThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where labour is less than DEFAULT_LABOUR
        defaultVisitShouldNotBeFound("labour.lessThan=" + DEFAULT_LABOUR);

        // Get all the visitList where labour is less than UPDATED_LABOUR
        defaultVisitShouldBeFound("labour.lessThan=" + UPDATED_LABOUR);
    }

    @Test
    @Transactional
    void getAllVisitsByLabourIsGreaterThanSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        // Get all the visitList where labour is greater than DEFAULT_LABOUR
        defaultVisitShouldNotBeFound("labour.greaterThan=" + DEFAULT_LABOUR);

        // Get all the visitList where labour is greater than SMALLER_LABOUR
        defaultVisitShouldBeFound("labour.greaterThan=" + SMALLER_LABOUR);
    }

    @Test
    @Transactional
    void getAllVisitsByMaterialsIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);
        Material materials;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            materials = MaterialResourceIT.createEntity(em);
            em.persist(materials);
            em.flush();
        } else {
            materials = TestUtil.findAll(em, Material.class).get(0);
        }
        em.persist(materials);
        em.flush();
        visit.addMaterials(materials);
        visitRepository.saveAndFlush(visit);
        Long materialsId = materials.getId();

        // Get all the visitList where materials equals to materialsId
        defaultVisitShouldBeFound("materialsId.equals=" + materialsId);

        // Get all the visitList where materials equals to (materialsId + 1)
        defaultVisitShouldNotBeFound("materialsId.equals=" + (materialsId + 1));
    }

    @Test
    @Transactional
    void getAllVisitsByCertificatesIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);
        Certificate certificates;
        if (TestUtil.findAll(em, Certificate.class).isEmpty()) {
            certificates = CertificateResourceIT.createEntity(em);
            em.persist(certificates);
            em.flush();
        } else {
            certificates = TestUtil.findAll(em, Certificate.class).get(0);
        }
        em.persist(certificates);
        em.flush();
        visit.addCertificates(certificates);
        visitRepository.saveAndFlush(visit);
        Long certificatesId = certificates.getId();

        // Get all the visitList where certificates equals to certificatesId
        defaultVisitShouldBeFound("certificatesId.equals=" + certificatesId);

        // Get all the visitList where certificates equals to (certificatesId + 1)
        defaultVisitShouldNotBeFound("certificatesId.equals=" + (certificatesId + 1));
    }

    @Test
    @Transactional
    void getAllVisitsByEvidencesIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);
        Evidence evidences;
        if (TestUtil.findAll(em, Evidence.class).isEmpty()) {
            evidences = EvidenceResourceIT.createEntity(em);
            em.persist(evidences);
            em.flush();
        } else {
            evidences = TestUtil.findAll(em, Evidence.class).get(0);
        }
        em.persist(evidences);
        em.flush();
        visit.addEvidences(evidences);
        visitRepository.saveAndFlush(visit);
        Long evidencesId = evidences.getId();

        // Get all the visitList where evidences equals to evidencesId
        defaultVisitShouldBeFound("evidencesId.equals=" + evidencesId);

        // Get all the visitList where evidences equals to (evidencesId + 1)
        defaultVisitShouldNotBeFound("evidencesId.equals=" + (evidencesId + 1));
    }

    @Test
    @Transactional
    void getAllVisitsByJobIsEqualToSomething() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);
        Job job;
        if (TestUtil.findAll(em, Job.class).isEmpty()) {
            job = JobResourceIT.createEntity(em);
            em.persist(job);
            em.flush();
        } else {
            job = TestUtil.findAll(em, Job.class).get(0);
        }
        em.persist(job);
        em.flush();
        visit.setJob(job);
        visitRepository.saveAndFlush(visit);
        Long jobId = job.getId();

        // Get all the visitList where job equals to jobId
        defaultVisitShouldBeFound("jobId.equals=" + jobId);

        // Get all the visitList where job equals to (jobId + 1)
        defaultVisitShouldNotBeFound("jobId.equals=" + (jobId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVisitShouldBeFound(String filter) throws Exception {
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visit.getId().intValue())))
            .andExpect(jsonPath("$.[*].arrived").value(hasItem(sameInstant(DEFAULT_ARRIVED))))
            .andExpect(jsonPath("$.[*].departed").value(hasItem(sameInstant(DEFAULT_DEPARTED))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].actions").value(hasItem(DEFAULT_ACTIONS.toString())))
            .andExpect(jsonPath("$.[*].labour").value(hasItem(sameNumber(DEFAULT_LABOUR))));

        // Check, that the count call also returns 1
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVisitShouldNotBeFound(String filter) throws Exception {
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVisit() throws Exception {
        // Get the visit
        restVisitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVisit() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        int databaseSizeBeforeUpdate = visitRepository.findAll().size();

        // Update the visit
        Visit updatedVisit = visitRepository.findById(visit.getId()).get();
        // Disconnect from session so that the updates on updatedVisit are not directly saved in db
        em.detach(updatedVisit);
        updatedVisit
            .arrived(UPDATED_ARRIVED)
            .departed(UPDATED_DEPARTED)
            .description(UPDATED_DESCRIPTION)
            .actions(UPDATED_ACTIONS)
            .labour(UPDATED_LABOUR);
        VisitDTO visitDTO = visitMapper.toDto(updatedVisit);

        restVisitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isOk());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getArrived()).isEqualTo(UPDATED_ARRIVED);
        assertThat(testVisit.getDeparted()).isEqualTo(UPDATED_DEPARTED);
        assertThat(testVisit.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testVisit.getActions()).isEqualTo(UPDATED_ACTIONS);
        assertThat(testVisit.getLabour()).isEqualTo(UPDATED_LABOUR);
    }

    @Test
    @Transactional
    void putNonExistingVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVisitWithPatch() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        int databaseSizeBeforeUpdate = visitRepository.findAll().size();

        // Update the visit using partial update
        Visit partialUpdatedVisit = new Visit();
        partialUpdatedVisit.setId(visit.getId());

        partialUpdatedVisit.departed(UPDATED_DEPARTED).description(UPDATED_DESCRIPTION);

        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVisit))
            )
            .andExpect(status().isOk());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getArrived()).isEqualTo(DEFAULT_ARRIVED);
        assertThat(testVisit.getDeparted()).isEqualTo(UPDATED_DEPARTED);
        assertThat(testVisit.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testVisit.getActions()).isEqualTo(DEFAULT_ACTIONS);
        assertThat(testVisit.getLabour()).isEqualByComparingTo(DEFAULT_LABOUR);
    }

    @Test
    @Transactional
    void fullUpdateVisitWithPatch() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        int databaseSizeBeforeUpdate = visitRepository.findAll().size();

        // Update the visit using partial update
        Visit partialUpdatedVisit = new Visit();
        partialUpdatedVisit.setId(visit.getId());

        partialUpdatedVisit
            .arrived(UPDATED_ARRIVED)
            .departed(UPDATED_DEPARTED)
            .description(UPDATED_DESCRIPTION)
            .actions(UPDATED_ACTIONS)
            .labour(UPDATED_LABOUR);

        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVisit))
            )
            .andExpect(status().isOk());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
        Visit testVisit = visitList.get(visitList.size() - 1);
        assertThat(testVisit.getArrived()).isEqualTo(UPDATED_ARRIVED);
        assertThat(testVisit.getDeparted()).isEqualTo(UPDATED_DEPARTED);
        assertThat(testVisit.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testVisit.getActions()).isEqualTo(UPDATED_ACTIONS);
        assertThat(testVisit.getLabour()).isEqualByComparingTo(UPDATED_LABOUR);
    }

    @Test
    @Transactional
    void patchNonExistingVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, visitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVisit() throws Exception {
        int databaseSizeBeforeUpdate = visitRepository.findAll().size();
        visit.setId(count.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(visitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visit in the database
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVisit() throws Exception {
        // Initialize the database
        visitRepository.saveAndFlush(visit);

        int databaseSizeBeforeDelete = visitRepository.findAll().size();

        // Delete the visit
        restVisitMockMvc
            .perform(delete(ENTITY_API_URL_ID, visit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Visit> visitList = visitRepository.findAll();
        assertThat(visitList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
