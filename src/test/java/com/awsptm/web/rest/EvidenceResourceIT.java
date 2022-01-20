package com.awsptm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.Evidence;
import com.awsptm.domain.Visit;
import com.awsptm.repository.EvidenceRepository;
import com.awsptm.service.criteria.EvidenceCriteria;
import com.awsptm.service.dto.EvidenceDTO;
import com.awsptm.service.mapper.EvidenceMapper;
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
 * Integration tests for the {@link EvidenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EvidenceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/evidences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private EvidenceMapper evidenceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEvidenceMockMvc;

    private Evidence evidence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evidence createEntity(EntityManager em) {
        Evidence evidence = new Evidence()
            .name(DEFAULT_NAME)
            .url(DEFAULT_URL)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return evidence;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evidence createUpdatedEntity(EntityManager em) {
        Evidence evidence = new Evidence()
            .name(UPDATED_NAME)
            .url(UPDATED_URL)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        return evidence;
    }

    @BeforeEach
    public void initTest() {
        evidence = createEntity(em);
    }

    @Test
    @Transactional
    void createEvidence() throws Exception {
        int databaseSizeBeforeCreate = evidenceRepository.findAll().size();
        // Create the Evidence
        EvidenceDTO evidenceDTO = evidenceMapper.toDto(evidence);
        restEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(evidenceDTO)))
            .andExpect(status().isCreated());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeCreate + 1);
        Evidence testEvidence = evidenceList.get(evidenceList.size() - 1);
        assertThat(testEvidence.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEvidence.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testEvidence.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testEvidence.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createEvidenceWithExistingId() throws Exception {
        // Create the Evidence with an existing ID
        evidence.setId(1L);
        EvidenceDTO evidenceDTO = evidenceMapper.toDto(evidence);

        int databaseSizeBeforeCreate = evidenceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEvidenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(evidenceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEvidences() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList
        restEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evidence.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    void getEvidence() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get the evidence
        restEvidenceMockMvc
            .perform(get(ENTITY_API_URL_ID, evidence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(evidence.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getEvidencesByIdFiltering() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        Long id = evidence.getId();

        defaultEvidenceShouldBeFound("id.equals=" + id);
        defaultEvidenceShouldNotBeFound("id.notEquals=" + id);

        defaultEvidenceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEvidenceShouldNotBeFound("id.greaterThan=" + id);

        defaultEvidenceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEvidenceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEvidencesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where name equals to DEFAULT_NAME
        defaultEvidenceShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the evidenceList where name equals to UPDATED_NAME
        defaultEvidenceShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEvidencesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where name not equals to DEFAULT_NAME
        defaultEvidenceShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the evidenceList where name not equals to UPDATED_NAME
        defaultEvidenceShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEvidencesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where name in DEFAULT_NAME or UPDATED_NAME
        defaultEvidenceShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the evidenceList where name equals to UPDATED_NAME
        defaultEvidenceShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEvidencesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where name is not null
        defaultEvidenceShouldBeFound("name.specified=true");

        // Get all the evidenceList where name is null
        defaultEvidenceShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllEvidencesByNameContainsSomething() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where name contains DEFAULT_NAME
        defaultEvidenceShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the evidenceList where name contains UPDATED_NAME
        defaultEvidenceShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEvidencesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where name does not contain DEFAULT_NAME
        defaultEvidenceShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the evidenceList where name does not contain UPDATED_NAME
        defaultEvidenceShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllEvidencesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where url equals to DEFAULT_URL
        defaultEvidenceShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the evidenceList where url equals to UPDATED_URL
        defaultEvidenceShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllEvidencesByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where url not equals to DEFAULT_URL
        defaultEvidenceShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the evidenceList where url not equals to UPDATED_URL
        defaultEvidenceShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllEvidencesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where url in DEFAULT_URL or UPDATED_URL
        defaultEvidenceShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the evidenceList where url equals to UPDATED_URL
        defaultEvidenceShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllEvidencesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where url is not null
        defaultEvidenceShouldBeFound("url.specified=true");

        // Get all the evidenceList where url is null
        defaultEvidenceShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllEvidencesByUrlContainsSomething() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where url contains DEFAULT_URL
        defaultEvidenceShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the evidenceList where url contains UPDATED_URL
        defaultEvidenceShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllEvidencesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        // Get all the evidenceList where url does not contain DEFAULT_URL
        defaultEvidenceShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the evidenceList where url does not contain UPDATED_URL
        defaultEvidenceShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllEvidencesByVisitIsEqualToSomething() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);
        Visit visit;
        if (TestUtil.findAll(em, Visit.class).isEmpty()) {
            visit = VisitResourceIT.createEntity(em);
            em.persist(visit);
            em.flush();
        } else {
            visit = TestUtil.findAll(em, Visit.class).get(0);
        }
        em.persist(visit);
        em.flush();
        evidence.setVisit(visit);
        evidenceRepository.saveAndFlush(evidence);
        Long visitId = visit.getId();

        // Get all the evidenceList where visit equals to visitId
        defaultEvidenceShouldBeFound("visitId.equals=" + visitId);

        // Get all the evidenceList where visit equals to (visitId + 1)
        defaultEvidenceShouldNotBeFound("visitId.equals=" + (visitId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEvidenceShouldBeFound(String filter) throws Exception {
        restEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evidence.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));

        // Check, that the count call also returns 1
        restEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEvidenceShouldNotBeFound(String filter) throws Exception {
        restEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEvidenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEvidence() throws Exception {
        // Get the evidence
        restEvidenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEvidence() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        int databaseSizeBeforeUpdate = evidenceRepository.findAll().size();

        // Update the evidence
        Evidence updatedEvidence = evidenceRepository.findById(evidence.getId()).get();
        // Disconnect from session so that the updates on updatedEvidence are not directly saved in db
        em.detach(updatedEvidence);
        updatedEvidence.name(UPDATED_NAME).url(UPDATED_URL).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        EvidenceDTO evidenceDTO = evidenceMapper.toDto(updatedEvidence);

        restEvidenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, evidenceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(evidenceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeUpdate);
        Evidence testEvidence = evidenceList.get(evidenceList.size() - 1);
        assertThat(testEvidence.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvidence.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testEvidence.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testEvidence.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingEvidence() throws Exception {
        int databaseSizeBeforeUpdate = evidenceRepository.findAll().size();
        evidence.setId(count.incrementAndGet());

        // Create the Evidence
        EvidenceDTO evidenceDTO = evidenceMapper.toDto(evidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEvidenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, evidenceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(evidenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEvidence() throws Exception {
        int databaseSizeBeforeUpdate = evidenceRepository.findAll().size();
        evidence.setId(count.incrementAndGet());

        // Create the Evidence
        EvidenceDTO evidenceDTO = evidenceMapper.toDto(evidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvidenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(evidenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEvidence() throws Exception {
        int databaseSizeBeforeUpdate = evidenceRepository.findAll().size();
        evidence.setId(count.incrementAndGet());

        // Create the Evidence
        EvidenceDTO evidenceDTO = evidenceMapper.toDto(evidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvidenceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(evidenceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEvidenceWithPatch() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        int databaseSizeBeforeUpdate = evidenceRepository.findAll().size();

        // Update the evidence using partial update
        Evidence partialUpdatedEvidence = new Evidence();
        partialUpdatedEvidence.setId(evidence.getId());

        partialUpdatedEvidence.url(UPDATED_URL).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restEvidenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvidence.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEvidence))
            )
            .andExpect(status().isOk());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeUpdate);
        Evidence testEvidence = evidenceList.get(evidenceList.size() - 1);
        assertThat(testEvidence.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEvidence.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testEvidence.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testEvidence.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateEvidenceWithPatch() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        int databaseSizeBeforeUpdate = evidenceRepository.findAll().size();

        // Update the evidence using partial update
        Evidence partialUpdatedEvidence = new Evidence();
        partialUpdatedEvidence.setId(evidence.getId());

        partialUpdatedEvidence.name(UPDATED_NAME).url(UPDATED_URL).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restEvidenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvidence.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEvidence))
            )
            .andExpect(status().isOk());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeUpdate);
        Evidence testEvidence = evidenceList.get(evidenceList.size() - 1);
        assertThat(testEvidence.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvidence.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testEvidence.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testEvidence.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingEvidence() throws Exception {
        int databaseSizeBeforeUpdate = evidenceRepository.findAll().size();
        evidence.setId(count.incrementAndGet());

        // Create the Evidence
        EvidenceDTO evidenceDTO = evidenceMapper.toDto(evidence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEvidenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, evidenceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(evidenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEvidence() throws Exception {
        int databaseSizeBeforeUpdate = evidenceRepository.findAll().size();
        evidence.setId(count.incrementAndGet());

        // Create the Evidence
        EvidenceDTO evidenceDTO = evidenceMapper.toDto(evidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvidenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(evidenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEvidence() throws Exception {
        int databaseSizeBeforeUpdate = evidenceRepository.findAll().size();
        evidence.setId(count.incrementAndGet());

        // Create the Evidence
        EvidenceDTO evidenceDTO = evidenceMapper.toDto(evidence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvidenceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(evidenceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evidence in the database
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEvidence() throws Exception {
        // Initialize the database
        evidenceRepository.saveAndFlush(evidence);

        int databaseSizeBeforeDelete = evidenceRepository.findAll().size();

        // Delete the evidence
        restEvidenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, evidence.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Evidence> evidenceList = evidenceRepository.findAll();
        assertThat(evidenceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
