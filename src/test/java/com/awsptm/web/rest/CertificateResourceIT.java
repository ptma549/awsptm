package com.awsptm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.Certificate;
import com.awsptm.domain.Visit;
import com.awsptm.repository.CertificateRepository;
import com.awsptm.service.criteria.CertificateCriteria;
import com.awsptm.service.dto.CertificateDTO;
import com.awsptm.service.mapper.CertificateMapper;
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
 * Integration tests for the {@link CertificateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CertificateResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/certificates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateMapper certificateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCertificateMockMvc;

    private Certificate certificate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certificate createEntity(EntityManager em) {
        Certificate certificate = new Certificate().name(DEFAULT_NAME).image(DEFAULT_IMAGE).imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return certificate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certificate createUpdatedEntity(EntityManager em) {
        Certificate certificate = new Certificate().name(UPDATED_NAME).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        return certificate;
    }

    @BeforeEach
    public void initTest() {
        certificate = createEntity(em);
    }

    @Test
    @Transactional
    void createCertificate() throws Exception {
        int databaseSizeBeforeCreate = certificateRepository.findAll().size();
        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);
        restCertificateMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(certificateDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeCreate + 1);
        Certificate testCertificate = certificateList.get(certificateList.size() - 1);
        assertThat(testCertificate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCertificate.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testCertificate.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createCertificateWithExistingId() throws Exception {
        // Create the Certificate with an existing ID
        certificate.setId(1L);
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        int databaseSizeBeforeCreate = certificateRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCertificateMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(certificateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCertificates() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certificate.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    void getCertificate() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get the certificate
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL_ID, certificate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(certificate.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getCertificatesByIdFiltering() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        Long id = certificate.getId();

        defaultCertificateShouldBeFound("id.equals=" + id);
        defaultCertificateShouldNotBeFound("id.notEquals=" + id);

        defaultCertificateShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCertificateShouldNotBeFound("id.greaterThan=" + id);

        defaultCertificateShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCertificateShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCertificatesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where name equals to DEFAULT_NAME
        defaultCertificateShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the certificateList where name equals to UPDATED_NAME
        defaultCertificateShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCertificatesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where name not equals to DEFAULT_NAME
        defaultCertificateShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the certificateList where name not equals to UPDATED_NAME
        defaultCertificateShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCertificatesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCertificateShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the certificateList where name equals to UPDATED_NAME
        defaultCertificateShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCertificatesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where name is not null
        defaultCertificateShouldBeFound("name.specified=true");

        // Get all the certificateList where name is null
        defaultCertificateShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllCertificatesByNameContainsSomething() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where name contains DEFAULT_NAME
        defaultCertificateShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the certificateList where name contains UPDATED_NAME
        defaultCertificateShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCertificatesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList where name does not contain DEFAULT_NAME
        defaultCertificateShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the certificateList where name does not contain UPDATED_NAME
        defaultCertificateShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCertificatesByVisitIsEqualToSomething() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);
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
        certificate.setVisit(visit);
        certificateRepository.saveAndFlush(certificate);
        Long visitId = visit.getId();

        // Get all the certificateList where visit equals to visitId
        defaultCertificateShouldBeFound("visitId.equals=" + visitId);

        // Get all the certificateList where visit equals to (visitId + 1)
        defaultCertificateShouldNotBeFound("visitId.equals=" + (visitId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCertificateShouldBeFound(String filter) throws Exception {
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certificate.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));

        // Check, that the count call also returns 1
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCertificateShouldNotBeFound(String filter) throws Exception {
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCertificateMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCertificate() throws Exception {
        // Get the certificate
        restCertificateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCertificate() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();

        // Update the certificate
        Certificate updatedCertificate = certificateRepository.findById(certificate.getId()).get();
        // Disconnect from session so that the updates on updatedCertificate are not directly saved in db
        em.detach(updatedCertificate);
        updatedCertificate.name(UPDATED_NAME).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        CertificateDTO certificateDTO = certificateMapper.toDto(updatedCertificate);

        restCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(certificateDTO))
            )
            .andExpect(status().isOk());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
        Certificate testCertificate = certificateList.get(certificateList.size() - 1);
        assertThat(testCertificate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCertificate.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testCertificate.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingCertificate() throws Exception {
        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();
        certificate.setId(count.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, certificateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(certificateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCertificate() throws Exception {
        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();
        certificate.setId(count.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(certificateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCertificate() throws Exception {
        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();
        certificate.setId(count.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(certificateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCertificateWithPatch() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();

        // Update the certificate using partial update
        Certificate partialUpdatedCertificate = new Certificate();
        partialUpdatedCertificate.setId(certificate.getId());

        partialUpdatedCertificate.name(UPDATED_NAME);

        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCertificate))
            )
            .andExpect(status().isOk());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
        Certificate testCertificate = certificateList.get(certificateList.size() - 1);
        assertThat(testCertificate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCertificate.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testCertificate.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateCertificateWithPatch() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();

        // Update the certificate using partial update
        Certificate partialUpdatedCertificate = new Certificate();
        partialUpdatedCertificate.setId(certificate.getId());

        partialUpdatedCertificate.name(UPDATED_NAME).image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCertificate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCertificate))
            )
            .andExpect(status().isOk());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
        Certificate testCertificate = certificateList.get(certificateList.size() - 1);
        assertThat(testCertificate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCertificate.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testCertificate.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingCertificate() throws Exception {
        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();
        certificate.setId(count.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, certificateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(certificateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCertificate() throws Exception {
        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();
        certificate.setId(count.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(certificateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCertificate() throws Exception {
        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();
        certificate.setId(count.incrementAndGet());

        // Create the Certificate
        CertificateDTO certificateDTO = certificateMapper.toDto(certificate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCertificateMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(certificateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCertificate() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        int databaseSizeBeforeDelete = certificateRepository.findAll().size();

        // Delete the certificate
        restCertificateMockMvc
            .perform(delete(ENTITY_API_URL_ID, certificate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
