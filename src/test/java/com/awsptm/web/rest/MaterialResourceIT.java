package com.awsptm.web.rest;

import static com.awsptm.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.Material;
import com.awsptm.domain.Visit;
import com.awsptm.repository.MaterialRepository;
import com.awsptm.service.criteria.MaterialCriteria;
import com.awsptm.service.dto.MaterialDTO;
import com.awsptm.service.mapper.MaterialMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link MaterialResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MaterialResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_UNIT_COST = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_COST = new BigDecimal(2);
    private static final BigDecimal SMALLER_UNIT_COST = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/materials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMaterialMockMvc;

    private Material material;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Material createEntity(EntityManager em) {
        Material material = new Material()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .quantity(DEFAULT_QUANTITY)
            .unitCost(DEFAULT_UNIT_COST);
        return material;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Material createUpdatedEntity(EntityManager em) {
        Material material = new Material()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .quantity(UPDATED_QUANTITY)
            .unitCost(UPDATED_UNIT_COST);
        return material;
    }

    @BeforeEach
    public void initTest() {
        material = createEntity(em);
    }

    @Test
    @Transactional
    void createMaterial() throws Exception {
        int databaseSizeBeforeCreate = materialRepository.findAll().size();
        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);
        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(materialDTO)))
            .andExpect(status().isCreated());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeCreate + 1);
        Material testMaterial = materialList.get(materialList.size() - 1);
        assertThat(testMaterial.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMaterial.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMaterial.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testMaterial.getUnitCost()).isEqualByComparingTo(DEFAULT_UNIT_COST);
    }

    @Test
    @Transactional
    void createMaterialWithExistingId() throws Exception {
        // Create the Material with an existing ID
        material.setId(1L);
        MaterialDTO materialDTO = materialMapper.toDto(material);

        int databaseSizeBeforeCreate = materialRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(materialDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMaterials() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(material.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitCost").value(hasItem(sameNumber(DEFAULT_UNIT_COST))));
    }

    @Test
    @Transactional
    void getMaterial() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get the material
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL_ID, material.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(material.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unitCost").value(sameNumber(DEFAULT_UNIT_COST)));
    }

    @Test
    @Transactional
    void getMaterialsByIdFiltering() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        Long id = material.getId();

        defaultMaterialShouldBeFound("id.equals=" + id);
        defaultMaterialShouldNotBeFound("id.notEquals=" + id);

        defaultMaterialShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMaterialShouldNotBeFound("id.greaterThan=" + id);

        defaultMaterialShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMaterialShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMaterialsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where name equals to DEFAULT_NAME
        defaultMaterialShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the materialList where name equals to UPDATED_NAME
        defaultMaterialShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where name not equals to DEFAULT_NAME
        defaultMaterialShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the materialList where name not equals to UPDATED_NAME
        defaultMaterialShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMaterialShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the materialList where name equals to UPDATED_NAME
        defaultMaterialShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where name is not null
        defaultMaterialShouldBeFound("name.specified=true");

        // Get all the materialList where name is null
        defaultMaterialShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByNameContainsSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where name contains DEFAULT_NAME
        defaultMaterialShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the materialList where name contains UPDATED_NAME
        defaultMaterialShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where name does not contain DEFAULT_NAME
        defaultMaterialShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the materialList where name does not contain UPDATED_NAME
        defaultMaterialShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMaterialsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where quantity equals to DEFAULT_QUANTITY
        defaultMaterialShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the materialList where quantity equals to UPDATED_QUANTITY
        defaultMaterialShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMaterialsByQuantityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where quantity not equals to DEFAULT_QUANTITY
        defaultMaterialShouldNotBeFound("quantity.notEquals=" + DEFAULT_QUANTITY);

        // Get all the materialList where quantity not equals to UPDATED_QUANTITY
        defaultMaterialShouldBeFound("quantity.notEquals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMaterialsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultMaterialShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the materialList where quantity equals to UPDATED_QUANTITY
        defaultMaterialShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMaterialsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where quantity is not null
        defaultMaterialShouldBeFound("quantity.specified=true");

        // Get all the materialList where quantity is null
        defaultMaterialShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultMaterialShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the materialList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultMaterialShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMaterialsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultMaterialShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the materialList where quantity is less than or equal to SMALLER_QUANTITY
        defaultMaterialShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMaterialsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where quantity is less than DEFAULT_QUANTITY
        defaultMaterialShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the materialList where quantity is less than UPDATED_QUANTITY
        defaultMaterialShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMaterialsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where quantity is greater than DEFAULT_QUANTITY
        defaultMaterialShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the materialList where quantity is greater than SMALLER_QUANTITY
        defaultMaterialShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllMaterialsByUnitCostIsEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where unitCost equals to DEFAULT_UNIT_COST
        defaultMaterialShouldBeFound("unitCost.equals=" + DEFAULT_UNIT_COST);

        // Get all the materialList where unitCost equals to UPDATED_UNIT_COST
        defaultMaterialShouldNotBeFound("unitCost.equals=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllMaterialsByUnitCostIsNotEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where unitCost not equals to DEFAULT_UNIT_COST
        defaultMaterialShouldNotBeFound("unitCost.notEquals=" + DEFAULT_UNIT_COST);

        // Get all the materialList where unitCost not equals to UPDATED_UNIT_COST
        defaultMaterialShouldBeFound("unitCost.notEquals=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllMaterialsByUnitCostIsInShouldWork() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where unitCost in DEFAULT_UNIT_COST or UPDATED_UNIT_COST
        defaultMaterialShouldBeFound("unitCost.in=" + DEFAULT_UNIT_COST + "," + UPDATED_UNIT_COST);

        // Get all the materialList where unitCost equals to UPDATED_UNIT_COST
        defaultMaterialShouldNotBeFound("unitCost.in=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllMaterialsByUnitCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where unitCost is not null
        defaultMaterialShouldBeFound("unitCost.specified=true");

        // Get all the materialList where unitCost is null
        defaultMaterialShouldNotBeFound("unitCost.specified=false");
    }

    @Test
    @Transactional
    void getAllMaterialsByUnitCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where unitCost is greater than or equal to DEFAULT_UNIT_COST
        defaultMaterialShouldBeFound("unitCost.greaterThanOrEqual=" + DEFAULT_UNIT_COST);

        // Get all the materialList where unitCost is greater than or equal to UPDATED_UNIT_COST
        defaultMaterialShouldNotBeFound("unitCost.greaterThanOrEqual=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllMaterialsByUnitCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where unitCost is less than or equal to DEFAULT_UNIT_COST
        defaultMaterialShouldBeFound("unitCost.lessThanOrEqual=" + DEFAULT_UNIT_COST);

        // Get all the materialList where unitCost is less than or equal to SMALLER_UNIT_COST
        defaultMaterialShouldNotBeFound("unitCost.lessThanOrEqual=" + SMALLER_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllMaterialsByUnitCostIsLessThanSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where unitCost is less than DEFAULT_UNIT_COST
        defaultMaterialShouldNotBeFound("unitCost.lessThan=" + DEFAULT_UNIT_COST);

        // Get all the materialList where unitCost is less than UPDATED_UNIT_COST
        defaultMaterialShouldBeFound("unitCost.lessThan=" + UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllMaterialsByUnitCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        // Get all the materialList where unitCost is greater than DEFAULT_UNIT_COST
        defaultMaterialShouldNotBeFound("unitCost.greaterThan=" + DEFAULT_UNIT_COST);

        // Get all the materialList where unitCost is greater than SMALLER_UNIT_COST
        defaultMaterialShouldBeFound("unitCost.greaterThan=" + SMALLER_UNIT_COST);
    }

    @Test
    @Transactional
    void getAllMaterialsByVisitIsEqualToSomething() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);
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
        material.setVisit(visit);
        materialRepository.saveAndFlush(material);
        Long visitId = visit.getId();

        // Get all the materialList where visit equals to visitId
        defaultMaterialShouldBeFound("visitId.equals=" + visitId);

        // Get all the materialList where visit equals to (visitId + 1)
        defaultMaterialShouldNotBeFound("visitId.equals=" + (visitId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMaterialShouldBeFound(String filter) throws Exception {
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(material.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitCost").value(hasItem(sameNumber(DEFAULT_UNIT_COST))));

        // Check, that the count call also returns 1
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMaterialShouldNotBeFound(String filter) throws Exception {
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMaterial() throws Exception {
        // Get the material
        restMaterialMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMaterial() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        int databaseSizeBeforeUpdate = materialRepository.findAll().size();

        // Update the material
        Material updatedMaterial = materialRepository.findById(material.getId()).get();
        // Disconnect from session so that the updates on updatedMaterial are not directly saved in db
        em.detach(updatedMaterial);
        updatedMaterial.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).quantity(UPDATED_QUANTITY).unitCost(UPDATED_UNIT_COST);
        MaterialDTO materialDTO = materialMapper.toDto(updatedMaterial);

        restMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, materialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(materialDTO))
            )
            .andExpect(status().isOk());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate);
        Material testMaterial = materialList.get(materialList.size() - 1);
        assertThat(testMaterial.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMaterial.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMaterial.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testMaterial.getUnitCost()).isEqualTo(UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void putNonExistingMaterial() throws Exception {
        int databaseSizeBeforeUpdate = materialRepository.findAll().size();
        material.setId(count.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, materialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(materialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMaterial() throws Exception {
        int databaseSizeBeforeUpdate = materialRepository.findAll().size();
        material.setId(count.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(materialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMaterial() throws Exception {
        int databaseSizeBeforeUpdate = materialRepository.findAll().size();
        material.setId(count.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(materialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMaterialWithPatch() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        int databaseSizeBeforeUpdate = materialRepository.findAll().size();

        // Update the material using partial update
        Material partialUpdatedMaterial = new Material();
        partialUpdatedMaterial.setId(material.getId());

        partialUpdatedMaterial.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMaterial))
            )
            .andExpect(status().isOk());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate);
        Material testMaterial = materialList.get(materialList.size() - 1);
        assertThat(testMaterial.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMaterial.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMaterial.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testMaterial.getUnitCost()).isEqualByComparingTo(DEFAULT_UNIT_COST);
    }

    @Test
    @Transactional
    void fullUpdateMaterialWithPatch() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        int databaseSizeBeforeUpdate = materialRepository.findAll().size();

        // Update the material using partial update
        Material partialUpdatedMaterial = new Material();
        partialUpdatedMaterial.setId(material.getId());

        partialUpdatedMaterial.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).quantity(UPDATED_QUANTITY).unitCost(UPDATED_UNIT_COST);

        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMaterial))
            )
            .andExpect(status().isOk());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate);
        Material testMaterial = materialList.get(materialList.size() - 1);
        assertThat(testMaterial.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMaterial.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMaterial.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testMaterial.getUnitCost()).isEqualByComparingTo(UPDATED_UNIT_COST);
    }

    @Test
    @Transactional
    void patchNonExistingMaterial() throws Exception {
        int databaseSizeBeforeUpdate = materialRepository.findAll().size();
        material.setId(count.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, materialDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(materialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMaterial() throws Exception {
        int databaseSizeBeforeUpdate = materialRepository.findAll().size();
        material.setId(count.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(materialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMaterial() throws Exception {
        int databaseSizeBeforeUpdate = materialRepository.findAll().size();
        material.setId(count.incrementAndGet());

        // Create the Material
        MaterialDTO materialDTO = materialMapper.toDto(material);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(materialDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Material in the database
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMaterial() throws Exception {
        // Initialize the database
        materialRepository.saveAndFlush(material);

        int databaseSizeBeforeDelete = materialRepository.findAll().size();

        // Delete the material
        restMaterialMockMvc
            .perform(delete(ENTITY_API_URL_ID, material.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Material> materialList = materialRepository.findAll();
        assertThat(materialList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
