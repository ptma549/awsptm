package com.awsptm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.AddressComponent;
import com.awsptm.domain.AddressType;
import com.awsptm.domain.GoogleAddress;
import com.awsptm.repository.AddressComponentRepository;
import com.awsptm.service.criteria.AddressComponentCriteria;
import com.awsptm.service.dto.AddressComponentDTO;
import com.awsptm.service.mapper.AddressComponentMapper;
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
 * Integration tests for the {@link AddressComponentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AddressComponentResourceIT {

    private static final String DEFAULT_LONG_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LONG_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/address-components";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AddressComponentRepository addressComponentRepository;

    @Autowired
    private AddressComponentMapper addressComponentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAddressComponentMockMvc;

    private AddressComponent addressComponent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AddressComponent createEntity(EntityManager em) {
        AddressComponent addressComponent = new AddressComponent().longName(DEFAULT_LONG_NAME).shortName(DEFAULT_SHORT_NAME);
        return addressComponent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AddressComponent createUpdatedEntity(EntityManager em) {
        AddressComponent addressComponent = new AddressComponent().longName(UPDATED_LONG_NAME).shortName(UPDATED_SHORT_NAME);
        return addressComponent;
    }

    @BeforeEach
    public void initTest() {
        addressComponent = createEntity(em);
    }

    @Test
    @Transactional
    void createAddressComponent() throws Exception {
        int databaseSizeBeforeCreate = addressComponentRepository.findAll().size();
        // Create the AddressComponent
        AddressComponentDTO addressComponentDTO = addressComponentMapper.toDto(addressComponent);
        restAddressComponentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addressComponentDTO))
            )
            .andExpect(status().isCreated());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeCreate + 1);
        AddressComponent testAddressComponent = addressComponentList.get(addressComponentList.size() - 1);
        assertThat(testAddressComponent.getLongName()).isEqualTo(DEFAULT_LONG_NAME);
        assertThat(testAddressComponent.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
    }

    @Test
    @Transactional
    void createAddressComponentWithExistingId() throws Exception {
        // Create the AddressComponent with an existing ID
        addressComponent.setId(1L);
        AddressComponentDTO addressComponentDTO = addressComponentMapper.toDto(addressComponent);

        int databaseSizeBeforeCreate = addressComponentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAddressComponentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addressComponentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAddressComponents() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList
        restAddressComponentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addressComponent.getId().intValue())))
            .andExpect(jsonPath("$.[*].longName").value(hasItem(DEFAULT_LONG_NAME)))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)));
    }

    @Test
    @Transactional
    void getAddressComponent() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get the addressComponent
        restAddressComponentMockMvc
            .perform(get(ENTITY_API_URL_ID, addressComponent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(addressComponent.getId().intValue()))
            .andExpect(jsonPath("$.longName").value(DEFAULT_LONG_NAME))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME));
    }

    @Test
    @Transactional
    void getAddressComponentsByIdFiltering() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        Long id = addressComponent.getId();

        defaultAddressComponentShouldBeFound("id.equals=" + id);
        defaultAddressComponentShouldNotBeFound("id.notEquals=" + id);

        defaultAddressComponentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAddressComponentShouldNotBeFound("id.greaterThan=" + id);

        defaultAddressComponentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAddressComponentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByLongNameIsEqualToSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where longName equals to DEFAULT_LONG_NAME
        defaultAddressComponentShouldBeFound("longName.equals=" + DEFAULT_LONG_NAME);

        // Get all the addressComponentList where longName equals to UPDATED_LONG_NAME
        defaultAddressComponentShouldNotBeFound("longName.equals=" + UPDATED_LONG_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByLongNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where longName not equals to DEFAULT_LONG_NAME
        defaultAddressComponentShouldNotBeFound("longName.notEquals=" + DEFAULT_LONG_NAME);

        // Get all the addressComponentList where longName not equals to UPDATED_LONG_NAME
        defaultAddressComponentShouldBeFound("longName.notEquals=" + UPDATED_LONG_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByLongNameIsInShouldWork() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where longName in DEFAULT_LONG_NAME or UPDATED_LONG_NAME
        defaultAddressComponentShouldBeFound("longName.in=" + DEFAULT_LONG_NAME + "," + UPDATED_LONG_NAME);

        // Get all the addressComponentList where longName equals to UPDATED_LONG_NAME
        defaultAddressComponentShouldNotBeFound("longName.in=" + UPDATED_LONG_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByLongNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where longName is not null
        defaultAddressComponentShouldBeFound("longName.specified=true");

        // Get all the addressComponentList where longName is null
        defaultAddressComponentShouldNotBeFound("longName.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressComponentsByLongNameContainsSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where longName contains DEFAULT_LONG_NAME
        defaultAddressComponentShouldBeFound("longName.contains=" + DEFAULT_LONG_NAME);

        // Get all the addressComponentList where longName contains UPDATED_LONG_NAME
        defaultAddressComponentShouldNotBeFound("longName.contains=" + UPDATED_LONG_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByLongNameNotContainsSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where longName does not contain DEFAULT_LONG_NAME
        defaultAddressComponentShouldNotBeFound("longName.doesNotContain=" + DEFAULT_LONG_NAME);

        // Get all the addressComponentList where longName does not contain UPDATED_LONG_NAME
        defaultAddressComponentShouldBeFound("longName.doesNotContain=" + UPDATED_LONG_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByShortNameIsEqualToSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where shortName equals to DEFAULT_SHORT_NAME
        defaultAddressComponentShouldBeFound("shortName.equals=" + DEFAULT_SHORT_NAME);

        // Get all the addressComponentList where shortName equals to UPDATED_SHORT_NAME
        defaultAddressComponentShouldNotBeFound("shortName.equals=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByShortNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where shortName not equals to DEFAULT_SHORT_NAME
        defaultAddressComponentShouldNotBeFound("shortName.notEquals=" + DEFAULT_SHORT_NAME);

        // Get all the addressComponentList where shortName not equals to UPDATED_SHORT_NAME
        defaultAddressComponentShouldBeFound("shortName.notEquals=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByShortNameIsInShouldWork() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where shortName in DEFAULT_SHORT_NAME or UPDATED_SHORT_NAME
        defaultAddressComponentShouldBeFound("shortName.in=" + DEFAULT_SHORT_NAME + "," + UPDATED_SHORT_NAME);

        // Get all the addressComponentList where shortName equals to UPDATED_SHORT_NAME
        defaultAddressComponentShouldNotBeFound("shortName.in=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByShortNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where shortName is not null
        defaultAddressComponentShouldBeFound("shortName.specified=true");

        // Get all the addressComponentList where shortName is null
        defaultAddressComponentShouldNotBeFound("shortName.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressComponentsByShortNameContainsSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where shortName contains DEFAULT_SHORT_NAME
        defaultAddressComponentShouldBeFound("shortName.contains=" + DEFAULT_SHORT_NAME);

        // Get all the addressComponentList where shortName contains UPDATED_SHORT_NAME
        defaultAddressComponentShouldNotBeFound("shortName.contains=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByShortNameNotContainsSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        // Get all the addressComponentList where shortName does not contain DEFAULT_SHORT_NAME
        defaultAddressComponentShouldNotBeFound("shortName.doesNotContain=" + DEFAULT_SHORT_NAME);

        // Get all the addressComponentList where shortName does not contain UPDATED_SHORT_NAME
        defaultAddressComponentShouldBeFound("shortName.doesNotContain=" + UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void getAllAddressComponentsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);
        GoogleAddress address;
        if (TestUtil.findAll(em, GoogleAddress.class).isEmpty()) {
            address = GoogleAddressResourceIT.createEntity(em);
            em.persist(address);
            em.flush();
        } else {
            address = TestUtil.findAll(em, GoogleAddress.class).get(0);
        }
        em.persist(address);
        em.flush();
        addressComponent.setAddress(address);
        addressComponentRepository.saveAndFlush(addressComponent);
        Long addressId = address.getId();

        // Get all the addressComponentList where address equals to addressId
        defaultAddressComponentShouldBeFound("addressId.equals=" + addressId);

        // Get all the addressComponentList where address equals to (addressId + 1)
        defaultAddressComponentShouldNotBeFound("addressId.equals=" + (addressId + 1));
    }

    @Test
    @Transactional
    void getAllAddressComponentsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);
        AddressType type;
        if (TestUtil.findAll(em, AddressType.class).isEmpty()) {
            type = AddressTypeResourceIT.createEntity(em);
            em.persist(type);
            em.flush();
        } else {
            type = TestUtil.findAll(em, AddressType.class).get(0);
        }
        em.persist(type);
        em.flush();
        addressComponent.setType(type);
        addressComponentRepository.saveAndFlush(addressComponent);
        Long typeId = type.getId();

        // Get all the addressComponentList where type equals to typeId
        defaultAddressComponentShouldBeFound("typeId.equals=" + typeId);

        // Get all the addressComponentList where type equals to (typeId + 1)
        defaultAddressComponentShouldNotBeFound("typeId.equals=" + (typeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAddressComponentShouldBeFound(String filter) throws Exception {
        restAddressComponentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(addressComponent.getId().intValue())))
            .andExpect(jsonPath("$.[*].longName").value(hasItem(DEFAULT_LONG_NAME)))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)));

        // Check, that the count call also returns 1
        restAddressComponentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAddressComponentShouldNotBeFound(String filter) throws Exception {
        restAddressComponentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAddressComponentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAddressComponent() throws Exception {
        // Get the addressComponent
        restAddressComponentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAddressComponent() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        int databaseSizeBeforeUpdate = addressComponentRepository.findAll().size();

        // Update the addressComponent
        AddressComponent updatedAddressComponent = addressComponentRepository.findById(addressComponent.getId()).get();
        // Disconnect from session so that the updates on updatedAddressComponent are not directly saved in db
        em.detach(updatedAddressComponent);
        updatedAddressComponent.longName(UPDATED_LONG_NAME).shortName(UPDATED_SHORT_NAME);
        AddressComponentDTO addressComponentDTO = addressComponentMapper.toDto(updatedAddressComponent);

        restAddressComponentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, addressComponentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addressComponentDTO))
            )
            .andExpect(status().isOk());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeUpdate);
        AddressComponent testAddressComponent = addressComponentList.get(addressComponentList.size() - 1);
        assertThat(testAddressComponent.getLongName()).isEqualTo(UPDATED_LONG_NAME);
        assertThat(testAddressComponent.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void putNonExistingAddressComponent() throws Exception {
        int databaseSizeBeforeUpdate = addressComponentRepository.findAll().size();
        addressComponent.setId(count.incrementAndGet());

        // Create the AddressComponent
        AddressComponentDTO addressComponentDTO = addressComponentMapper.toDto(addressComponent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressComponentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, addressComponentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addressComponentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAddressComponent() throws Exception {
        int databaseSizeBeforeUpdate = addressComponentRepository.findAll().size();
        addressComponent.setId(count.incrementAndGet());

        // Create the AddressComponent
        AddressComponentDTO addressComponentDTO = addressComponentMapper.toDto(addressComponent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressComponentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addressComponentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAddressComponent() throws Exception {
        int databaseSizeBeforeUpdate = addressComponentRepository.findAll().size();
        addressComponent.setId(count.incrementAndGet());

        // Create the AddressComponent
        AddressComponentDTO addressComponentDTO = addressComponentMapper.toDto(addressComponent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressComponentMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addressComponentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAddressComponentWithPatch() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        int databaseSizeBeforeUpdate = addressComponentRepository.findAll().size();

        // Update the addressComponent using partial update
        AddressComponent partialUpdatedAddressComponent = new AddressComponent();
        partialUpdatedAddressComponent.setId(addressComponent.getId());

        partialUpdatedAddressComponent.longName(UPDATED_LONG_NAME).shortName(UPDATED_SHORT_NAME);

        restAddressComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddressComponent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddressComponent))
            )
            .andExpect(status().isOk());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeUpdate);
        AddressComponent testAddressComponent = addressComponentList.get(addressComponentList.size() - 1);
        assertThat(testAddressComponent.getLongName()).isEqualTo(UPDATED_LONG_NAME);
        assertThat(testAddressComponent.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateAddressComponentWithPatch() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        int databaseSizeBeforeUpdate = addressComponentRepository.findAll().size();

        // Update the addressComponent using partial update
        AddressComponent partialUpdatedAddressComponent = new AddressComponent();
        partialUpdatedAddressComponent.setId(addressComponent.getId());

        partialUpdatedAddressComponent.longName(UPDATED_LONG_NAME).shortName(UPDATED_SHORT_NAME);

        restAddressComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddressComponent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddressComponent))
            )
            .andExpect(status().isOk());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeUpdate);
        AddressComponent testAddressComponent = addressComponentList.get(addressComponentList.size() - 1);
        assertThat(testAddressComponent.getLongName()).isEqualTo(UPDATED_LONG_NAME);
        assertThat(testAddressComponent.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingAddressComponent() throws Exception {
        int databaseSizeBeforeUpdate = addressComponentRepository.findAll().size();
        addressComponent.setId(count.incrementAndGet());

        // Create the AddressComponent
        AddressComponentDTO addressComponentDTO = addressComponentMapper.toDto(addressComponent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, addressComponentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addressComponentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAddressComponent() throws Exception {
        int databaseSizeBeforeUpdate = addressComponentRepository.findAll().size();
        addressComponent.setId(count.incrementAndGet());

        // Create the AddressComponent
        AddressComponentDTO addressComponentDTO = addressComponentMapper.toDto(addressComponent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addressComponentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAddressComponent() throws Exception {
        int databaseSizeBeforeUpdate = addressComponentRepository.findAll().size();
        addressComponent.setId(count.incrementAndGet());

        // Create the AddressComponent
        AddressComponentDTO addressComponentDTO = addressComponentMapper.toDto(addressComponent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressComponentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addressComponentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AddressComponent in the database
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAddressComponent() throws Exception {
        // Initialize the database
        addressComponentRepository.saveAndFlush(addressComponent);

        int databaseSizeBeforeDelete = addressComponentRepository.findAll().size();

        // Delete the addressComponent
        restAddressComponentMockMvc
            .perform(delete(ENTITY_API_URL_ID, addressComponent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AddressComponent> addressComponentList = addressComponentRepository.findAll();
        assertThat(addressComponentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
