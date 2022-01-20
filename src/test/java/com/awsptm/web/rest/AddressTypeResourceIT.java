package com.awsptm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.AddressComponent;
import com.awsptm.domain.AddressType;
import com.awsptm.repository.AddressTypeRepository;
import com.awsptm.service.criteria.AddressTypeCriteria;
import com.awsptm.service.dto.AddressTypeDTO;
import com.awsptm.service.mapper.AddressTypeMapper;
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
 * Integration tests for the {@link AddressTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AddressTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_POSITION = 1;
    private static final Integer UPDATED_POSITION = 2;
    private static final Integer SMALLER_POSITION = 1 - 1;

    private static final String ENTITY_API_URL = "/api/address-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AddressTypeRepository addressTypeRepository;

    @Autowired
    private AddressTypeMapper addressTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAddressTypeMockMvc;

    private AddressType addressType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AddressType createEntity(EntityManager em) {
        AddressType addressType = new AddressType().name(DEFAULT_NAME).position(DEFAULT_POSITION);
        return addressType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AddressType createUpdatedEntity(EntityManager em) {
        AddressType addressType = new AddressType().name(UPDATED_NAME).position(UPDATED_POSITION);
        return addressType;
    }

    @BeforeEach
    public void initTest() {
        addressType = createEntity(em);
    }

    @Test
    @Transactional
    void createAddressType() throws Exception {
        int databaseSizeBeforeCreate = addressTypeRepository.findAll().size();
        // Create the AddressType
        AddressTypeDTO addressTypeDTO = addressTypeMapper.toDto(addressType);
        restAddressTypeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addressTypeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeCreate + 1);
        AddressType testAddressType = addressTypeList.get(addressTypeList.size() - 1);
        assertThat(testAddressType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAddressType.getPosition()).isEqualTo(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    void createAddressTypeWithExistingId() throws Exception {
        // Create the AddressType with an existing ID
        addressType.setId(1L);
        AddressTypeDTO addressTypeDTO = addressTypeMapper.toDto(addressType);

        int databaseSizeBeforeCreate = addressTypeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAddressTypeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addressTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAddressTypes() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList
        restAddressTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addressType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)));
    }

    @Test
    @Transactional
    void getAddressType() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get the addressType
        restAddressTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, addressType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(addressType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION));
    }

    @Test
    @Transactional
    void getAddressTypesByIdFiltering() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        Long id = addressType.getId();

        defaultAddressTypeShouldBeFound("id.equals=" + id);
        defaultAddressTypeShouldNotBeFound("id.notEquals=" + id);

        defaultAddressTypeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAddressTypeShouldNotBeFound("id.greaterThan=" + id);

        defaultAddressTypeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAddressTypeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAddressTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where name equals to DEFAULT_NAME
        defaultAddressTypeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the addressTypeList where name equals to UPDATED_NAME
        defaultAddressTypeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAddressTypesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where name not equals to DEFAULT_NAME
        defaultAddressTypeShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the addressTypeList where name not equals to UPDATED_NAME
        defaultAddressTypeShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAddressTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultAddressTypeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the addressTypeList where name equals to UPDATED_NAME
        defaultAddressTypeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAddressTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where name is not null
        defaultAddressTypeShouldBeFound("name.specified=true");

        // Get all the addressTypeList where name is null
        defaultAddressTypeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressTypesByNameContainsSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where name contains DEFAULT_NAME
        defaultAddressTypeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the addressTypeList where name contains UPDATED_NAME
        defaultAddressTypeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAddressTypesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where name does not contain DEFAULT_NAME
        defaultAddressTypeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the addressTypeList where name does not contain UPDATED_NAME
        defaultAddressTypeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAddressTypesByPositionIsEqualToSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where position equals to DEFAULT_POSITION
        defaultAddressTypeShouldBeFound("position.equals=" + DEFAULT_POSITION);

        // Get all the addressTypeList where position equals to UPDATED_POSITION
        defaultAddressTypeShouldNotBeFound("position.equals=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressTypesByPositionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where position not equals to DEFAULT_POSITION
        defaultAddressTypeShouldNotBeFound("position.notEquals=" + DEFAULT_POSITION);

        // Get all the addressTypeList where position not equals to UPDATED_POSITION
        defaultAddressTypeShouldBeFound("position.notEquals=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressTypesByPositionIsInShouldWork() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where position in DEFAULT_POSITION or UPDATED_POSITION
        defaultAddressTypeShouldBeFound("position.in=" + DEFAULT_POSITION + "," + UPDATED_POSITION);

        // Get all the addressTypeList where position equals to UPDATED_POSITION
        defaultAddressTypeShouldNotBeFound("position.in=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressTypesByPositionIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where position is not null
        defaultAddressTypeShouldBeFound("position.specified=true");

        // Get all the addressTypeList where position is null
        defaultAddressTypeShouldNotBeFound("position.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressTypesByPositionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where position is greater than or equal to DEFAULT_POSITION
        defaultAddressTypeShouldBeFound("position.greaterThanOrEqual=" + DEFAULT_POSITION);

        // Get all the addressTypeList where position is greater than or equal to UPDATED_POSITION
        defaultAddressTypeShouldNotBeFound("position.greaterThanOrEqual=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressTypesByPositionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where position is less than or equal to DEFAULT_POSITION
        defaultAddressTypeShouldBeFound("position.lessThanOrEqual=" + DEFAULT_POSITION);

        // Get all the addressTypeList where position is less than or equal to SMALLER_POSITION
        defaultAddressTypeShouldNotBeFound("position.lessThanOrEqual=" + SMALLER_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressTypesByPositionIsLessThanSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where position is less than DEFAULT_POSITION
        defaultAddressTypeShouldNotBeFound("position.lessThan=" + DEFAULT_POSITION);

        // Get all the addressTypeList where position is less than UPDATED_POSITION
        defaultAddressTypeShouldBeFound("position.lessThan=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressTypesByPositionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        // Get all the addressTypeList where position is greater than DEFAULT_POSITION
        defaultAddressTypeShouldNotBeFound("position.greaterThan=" + DEFAULT_POSITION);

        // Get all the addressTypeList where position is greater than SMALLER_POSITION
        defaultAddressTypeShouldBeFound("position.greaterThan=" + SMALLER_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressTypesByAddressComponentsIsEqualToSomething() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);
        AddressComponent addressComponents;
        if (TestUtil.findAll(em, AddressComponent.class).isEmpty()) {
            addressComponents = AddressComponentResourceIT.createEntity(em);
            em.persist(addressComponents);
            em.flush();
        } else {
            addressComponents = TestUtil.findAll(em, AddressComponent.class).get(0);
        }
        em.persist(addressComponents);
        em.flush();
        addressType.addAddressComponents(addressComponents);
        addressTypeRepository.saveAndFlush(addressType);
        Long addressComponentsId = addressComponents.getId();

        // Get all the addressTypeList where addressComponents equals to addressComponentsId
        defaultAddressTypeShouldBeFound("addressComponentsId.equals=" + addressComponentsId);

        // Get all the addressTypeList where addressComponents equals to (addressComponentsId + 1)
        defaultAddressTypeShouldNotBeFound("addressComponentsId.equals=" + (addressComponentsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAddressTypeShouldBeFound(String filter) throws Exception {
        restAddressTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addressType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)));

        // Check, that the count call also returns 1
        restAddressTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAddressTypeShouldNotBeFound(String filter) throws Exception {
        restAddressTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAddressTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAddressType() throws Exception {
        // Get the addressType
        restAddressTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAddressType() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();

        // Update the addressType
        AddressType updatedAddressType = addressTypeRepository.findById(addressType.getId()).get();
        // Disconnect from session so that the updates on updatedAddressType are not directly saved in db
        em.detach(updatedAddressType);
        updatedAddressType.name(UPDATED_NAME).position(UPDATED_POSITION);
        AddressTypeDTO addressTypeDTO = addressTypeMapper.toDto(updatedAddressType);

        restAddressTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, addressTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addressTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
        AddressType testAddressType = addressTypeList.get(addressTypeList.size() - 1);
        assertThat(testAddressType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAddressType.getPosition()).isEqualTo(UPDATED_POSITION);
    }

    @Test
    @Transactional
    void putNonExistingAddressType() throws Exception {
        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();
        addressType.setId(count.incrementAndGet());

        // Create the AddressType
        AddressTypeDTO addressTypeDTO = addressTypeMapper.toDto(addressType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, addressTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addressTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAddressType() throws Exception {
        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();
        addressType.setId(count.incrementAndGet());

        // Create the AddressType
        AddressTypeDTO addressTypeDTO = addressTypeMapper.toDto(addressType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addressTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAddressType() throws Exception {
        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();
        addressType.setId(count.incrementAndGet());

        // Create the AddressType
        AddressTypeDTO addressTypeDTO = addressTypeMapper.toDto(addressType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addressTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAddressTypeWithPatch() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();

        // Update the addressType using partial update
        AddressType partialUpdatedAddressType = new AddressType();
        partialUpdatedAddressType.setId(addressType.getId());

        partialUpdatedAddressType.name(UPDATED_NAME);

        restAddressTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddressType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddressType))
            )
            .andExpect(status().isOk());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
        AddressType testAddressType = addressTypeList.get(addressTypeList.size() - 1);
        assertThat(testAddressType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAddressType.getPosition()).isEqualTo(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    void fullUpdateAddressTypeWithPatch() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();

        // Update the addressType using partial update
        AddressType partialUpdatedAddressType = new AddressType();
        partialUpdatedAddressType.setId(addressType.getId());

        partialUpdatedAddressType.name(UPDATED_NAME).position(UPDATED_POSITION);

        restAddressTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddressType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddressType))
            )
            .andExpect(status().isOk());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
        AddressType testAddressType = addressTypeList.get(addressTypeList.size() - 1);
        assertThat(testAddressType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAddressType.getPosition()).isEqualTo(UPDATED_POSITION);
    }

    @Test
    @Transactional
    void patchNonExistingAddressType() throws Exception {
        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();
        addressType.setId(count.incrementAndGet());

        // Create the AddressType
        AddressTypeDTO addressTypeDTO = addressTypeMapper.toDto(addressType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, addressTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addressTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAddressType() throws Exception {
        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();
        addressType.setId(count.incrementAndGet());

        // Create the AddressType
        AddressTypeDTO addressTypeDTO = addressTypeMapper.toDto(addressType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addressTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAddressType() throws Exception {
        int databaseSizeBeforeUpdate = addressTypeRepository.findAll().size();
        addressType.setId(count.incrementAndGet());

        // Create the AddressType
        AddressTypeDTO addressTypeDTO = addressTypeMapper.toDto(addressType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressTypeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(addressTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AddressType in the database
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAddressType() throws Exception {
        // Initialize the database
        addressTypeRepository.saveAndFlush(addressType);

        int databaseSizeBeforeDelete = addressTypeRepository.findAll().size();

        // Delete the addressType
        restAddressTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, addressType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AddressType> addressTypeList = addressTypeRepository.findAll();
        assertThat(addressTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
