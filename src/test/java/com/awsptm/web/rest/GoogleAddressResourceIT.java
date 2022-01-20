package com.awsptm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.Address;
import com.awsptm.domain.AddressComponent;
import com.awsptm.domain.GoogleAddress;
import com.awsptm.repository.GoogleAddressRepository;
import com.awsptm.service.criteria.GoogleAddressCriteria;
import com.awsptm.service.dto.GoogleAddressDTO;
import com.awsptm.service.mapper.GoogleAddressMapper;
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
 * Integration tests for the {@link GoogleAddressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GoogleAddressResourceIT {

    private static final String DEFAULT_POSITION = "AAAAAAAAAA";
    private static final String UPDATED_POSITION = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_HTML = "AAAAAAAAAA";
    private static final String UPDATED_HTML = "BBBBBBBBBB";

    private static final String DEFAULT_FORMATTED = "AAAAAAAAAA";
    private static final String UPDATED_FORMATTED = "BBBBBBBBBB";

    private static final String DEFAULT_TYPES = "AAAAAAAAAA";
    private static final String UPDATED_TYPES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/google-addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GoogleAddressRepository googleAddressRepository;

    @Autowired
    private GoogleAddressMapper googleAddressMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGoogleAddressMockMvc;

    private GoogleAddress googleAddress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GoogleAddress createEntity(EntityManager em) {
        GoogleAddress googleAddress = new GoogleAddress()
            .position(DEFAULT_POSITION)
            .url(DEFAULT_URL)
            .html(DEFAULT_HTML)
            .formatted(DEFAULT_FORMATTED)
            .types(DEFAULT_TYPES);
        return googleAddress;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GoogleAddress createUpdatedEntity(EntityManager em) {
        GoogleAddress googleAddress = new GoogleAddress()
            .position(UPDATED_POSITION)
            .url(UPDATED_URL)
            .html(UPDATED_HTML)
            .formatted(UPDATED_FORMATTED)
            .types(UPDATED_TYPES);
        return googleAddress;
    }

    @BeforeEach
    public void initTest() {
        googleAddress = createEntity(em);
    }

    @Test
    @Transactional
    void createGoogleAddress() throws Exception {
        int databaseSizeBeforeCreate = googleAddressRepository.findAll().size();
        // Create the GoogleAddress
        GoogleAddressDTO googleAddressDTO = googleAddressMapper.toDto(googleAddress);
        restGoogleAddressMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(googleAddressDTO))
            )
            .andExpect(status().isCreated());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeCreate + 1);
        GoogleAddress testGoogleAddress = googleAddressList.get(googleAddressList.size() - 1);
        assertThat(testGoogleAddress.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testGoogleAddress.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testGoogleAddress.getHtml()).isEqualTo(DEFAULT_HTML);
        assertThat(testGoogleAddress.getFormatted()).isEqualTo(DEFAULT_FORMATTED);
        assertThat(testGoogleAddress.getTypes()).isEqualTo(DEFAULT_TYPES);
    }

    @Test
    @Transactional
    void createGoogleAddressWithExistingId() throws Exception {
        // Create the GoogleAddress with an existing ID
        googleAddress.setId(1L);
        GoogleAddressDTO googleAddressDTO = googleAddressMapper.toDto(googleAddress);

        int databaseSizeBeforeCreate = googleAddressRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGoogleAddressMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(googleAddressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGoogleAddresses() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList
        restGoogleAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(googleAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].html").value(hasItem(DEFAULT_HTML)))
            .andExpect(jsonPath("$.[*].formatted").value(hasItem(DEFAULT_FORMATTED)))
            .andExpect(jsonPath("$.[*].types").value(hasItem(DEFAULT_TYPES)));
    }

    @Test
    @Transactional
    void getGoogleAddress() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get the googleAddress
        restGoogleAddressMockMvc
            .perform(get(ENTITY_API_URL_ID, googleAddress.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(googleAddress.getId().intValue()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.html").value(DEFAULT_HTML))
            .andExpect(jsonPath("$.formatted").value(DEFAULT_FORMATTED))
            .andExpect(jsonPath("$.types").value(DEFAULT_TYPES));
    }

    @Test
    @Transactional
    void getGoogleAddressesByIdFiltering() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        Long id = googleAddress.getId();

        defaultGoogleAddressShouldBeFound("id.equals=" + id);
        defaultGoogleAddressShouldNotBeFound("id.notEquals=" + id);

        defaultGoogleAddressShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultGoogleAddressShouldNotBeFound("id.greaterThan=" + id);

        defaultGoogleAddressShouldBeFound("id.lessThanOrEqual=" + id);
        defaultGoogleAddressShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByPositionIsEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where position equals to DEFAULT_POSITION
        defaultGoogleAddressShouldBeFound("position.equals=" + DEFAULT_POSITION);

        // Get all the googleAddressList where position equals to UPDATED_POSITION
        defaultGoogleAddressShouldNotBeFound("position.equals=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByPositionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where position not equals to DEFAULT_POSITION
        defaultGoogleAddressShouldNotBeFound("position.notEquals=" + DEFAULT_POSITION);

        // Get all the googleAddressList where position not equals to UPDATED_POSITION
        defaultGoogleAddressShouldBeFound("position.notEquals=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByPositionIsInShouldWork() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where position in DEFAULT_POSITION or UPDATED_POSITION
        defaultGoogleAddressShouldBeFound("position.in=" + DEFAULT_POSITION + "," + UPDATED_POSITION);

        // Get all the googleAddressList where position equals to UPDATED_POSITION
        defaultGoogleAddressShouldNotBeFound("position.in=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByPositionIsNullOrNotNull() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where position is not null
        defaultGoogleAddressShouldBeFound("position.specified=true");

        // Get all the googleAddressList where position is null
        defaultGoogleAddressShouldNotBeFound("position.specified=false");
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByPositionContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where position contains DEFAULT_POSITION
        defaultGoogleAddressShouldBeFound("position.contains=" + DEFAULT_POSITION);

        // Get all the googleAddressList where position contains UPDATED_POSITION
        defaultGoogleAddressShouldNotBeFound("position.contains=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByPositionNotContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where position does not contain DEFAULT_POSITION
        defaultGoogleAddressShouldNotBeFound("position.doesNotContain=" + DEFAULT_POSITION);

        // Get all the googleAddressList where position does not contain UPDATED_POSITION
        defaultGoogleAddressShouldBeFound("position.doesNotContain=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where url equals to DEFAULT_URL
        defaultGoogleAddressShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the googleAddressList where url equals to UPDATED_URL
        defaultGoogleAddressShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where url not equals to DEFAULT_URL
        defaultGoogleAddressShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the googleAddressList where url not equals to UPDATED_URL
        defaultGoogleAddressShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where url in DEFAULT_URL or UPDATED_URL
        defaultGoogleAddressShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the googleAddressList where url equals to UPDATED_URL
        defaultGoogleAddressShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where url is not null
        defaultGoogleAddressShouldBeFound("url.specified=true");

        // Get all the googleAddressList where url is null
        defaultGoogleAddressShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByUrlContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where url contains DEFAULT_URL
        defaultGoogleAddressShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the googleAddressList where url contains UPDATED_URL
        defaultGoogleAddressShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where url does not contain DEFAULT_URL
        defaultGoogleAddressShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the googleAddressList where url does not contain UPDATED_URL
        defaultGoogleAddressShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByHtmlIsEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where html equals to DEFAULT_HTML
        defaultGoogleAddressShouldBeFound("html.equals=" + DEFAULT_HTML);

        // Get all the googleAddressList where html equals to UPDATED_HTML
        defaultGoogleAddressShouldNotBeFound("html.equals=" + UPDATED_HTML);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByHtmlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where html not equals to DEFAULT_HTML
        defaultGoogleAddressShouldNotBeFound("html.notEquals=" + DEFAULT_HTML);

        // Get all the googleAddressList where html not equals to UPDATED_HTML
        defaultGoogleAddressShouldBeFound("html.notEquals=" + UPDATED_HTML);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByHtmlIsInShouldWork() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where html in DEFAULT_HTML or UPDATED_HTML
        defaultGoogleAddressShouldBeFound("html.in=" + DEFAULT_HTML + "," + UPDATED_HTML);

        // Get all the googleAddressList where html equals to UPDATED_HTML
        defaultGoogleAddressShouldNotBeFound("html.in=" + UPDATED_HTML);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByHtmlIsNullOrNotNull() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where html is not null
        defaultGoogleAddressShouldBeFound("html.specified=true");

        // Get all the googleAddressList where html is null
        defaultGoogleAddressShouldNotBeFound("html.specified=false");
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByHtmlContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where html contains DEFAULT_HTML
        defaultGoogleAddressShouldBeFound("html.contains=" + DEFAULT_HTML);

        // Get all the googleAddressList where html contains UPDATED_HTML
        defaultGoogleAddressShouldNotBeFound("html.contains=" + UPDATED_HTML);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByHtmlNotContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where html does not contain DEFAULT_HTML
        defaultGoogleAddressShouldNotBeFound("html.doesNotContain=" + DEFAULT_HTML);

        // Get all the googleAddressList where html does not contain UPDATED_HTML
        defaultGoogleAddressShouldBeFound("html.doesNotContain=" + UPDATED_HTML);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByFormattedIsEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where formatted equals to DEFAULT_FORMATTED
        defaultGoogleAddressShouldBeFound("formatted.equals=" + DEFAULT_FORMATTED);

        // Get all the googleAddressList where formatted equals to UPDATED_FORMATTED
        defaultGoogleAddressShouldNotBeFound("formatted.equals=" + UPDATED_FORMATTED);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByFormattedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where formatted not equals to DEFAULT_FORMATTED
        defaultGoogleAddressShouldNotBeFound("formatted.notEquals=" + DEFAULT_FORMATTED);

        // Get all the googleAddressList where formatted not equals to UPDATED_FORMATTED
        defaultGoogleAddressShouldBeFound("formatted.notEquals=" + UPDATED_FORMATTED);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByFormattedIsInShouldWork() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where formatted in DEFAULT_FORMATTED or UPDATED_FORMATTED
        defaultGoogleAddressShouldBeFound("formatted.in=" + DEFAULT_FORMATTED + "," + UPDATED_FORMATTED);

        // Get all the googleAddressList where formatted equals to UPDATED_FORMATTED
        defaultGoogleAddressShouldNotBeFound("formatted.in=" + UPDATED_FORMATTED);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByFormattedIsNullOrNotNull() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where formatted is not null
        defaultGoogleAddressShouldBeFound("formatted.specified=true");

        // Get all the googleAddressList where formatted is null
        defaultGoogleAddressShouldNotBeFound("formatted.specified=false");
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByFormattedContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where formatted contains DEFAULT_FORMATTED
        defaultGoogleAddressShouldBeFound("formatted.contains=" + DEFAULT_FORMATTED);

        // Get all the googleAddressList where formatted contains UPDATED_FORMATTED
        defaultGoogleAddressShouldNotBeFound("formatted.contains=" + UPDATED_FORMATTED);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByFormattedNotContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where formatted does not contain DEFAULT_FORMATTED
        defaultGoogleAddressShouldNotBeFound("formatted.doesNotContain=" + DEFAULT_FORMATTED);

        // Get all the googleAddressList where formatted does not contain UPDATED_FORMATTED
        defaultGoogleAddressShouldBeFound("formatted.doesNotContain=" + UPDATED_FORMATTED);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByTypesIsEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where types equals to DEFAULT_TYPES
        defaultGoogleAddressShouldBeFound("types.equals=" + DEFAULT_TYPES);

        // Get all the googleAddressList where types equals to UPDATED_TYPES
        defaultGoogleAddressShouldNotBeFound("types.equals=" + UPDATED_TYPES);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByTypesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where types not equals to DEFAULT_TYPES
        defaultGoogleAddressShouldNotBeFound("types.notEquals=" + DEFAULT_TYPES);

        // Get all the googleAddressList where types not equals to UPDATED_TYPES
        defaultGoogleAddressShouldBeFound("types.notEquals=" + UPDATED_TYPES);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByTypesIsInShouldWork() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where types in DEFAULT_TYPES or UPDATED_TYPES
        defaultGoogleAddressShouldBeFound("types.in=" + DEFAULT_TYPES + "," + UPDATED_TYPES);

        // Get all the googleAddressList where types equals to UPDATED_TYPES
        defaultGoogleAddressShouldNotBeFound("types.in=" + UPDATED_TYPES);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByTypesIsNullOrNotNull() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where types is not null
        defaultGoogleAddressShouldBeFound("types.specified=true");

        // Get all the googleAddressList where types is null
        defaultGoogleAddressShouldNotBeFound("types.specified=false");
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByTypesContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where types contains DEFAULT_TYPES
        defaultGoogleAddressShouldBeFound("types.contains=" + DEFAULT_TYPES);

        // Get all the googleAddressList where types contains UPDATED_TYPES
        defaultGoogleAddressShouldNotBeFound("types.contains=" + UPDATED_TYPES);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByTypesNotContainsSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        // Get all the googleAddressList where types does not contain DEFAULT_TYPES
        defaultGoogleAddressShouldNotBeFound("types.doesNotContain=" + DEFAULT_TYPES);

        // Get all the googleAddressList where types does not contain UPDATED_TYPES
        defaultGoogleAddressShouldBeFound("types.doesNotContain=" + UPDATED_TYPES);
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByAddressComponentsIsEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);
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
        googleAddress.addAddressComponents(addressComponents);
        googleAddressRepository.saveAndFlush(googleAddress);
        Long addressComponentsId = addressComponents.getId();

        // Get all the googleAddressList where addressComponents equals to addressComponentsId
        defaultGoogleAddressShouldBeFound("addressComponentsId.equals=" + addressComponentsId);

        // Get all the googleAddressList where addressComponents equals to (addressComponentsId + 1)
        defaultGoogleAddressShouldNotBeFound("addressComponentsId.equals=" + (addressComponentsId + 1));
    }

    @Test
    @Transactional
    void getAllGoogleAddressesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);
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
        googleAddress.setAddress(address);
        address.setGoogleAddress(googleAddress);
        googleAddressRepository.saveAndFlush(googleAddress);
        Long addressId = address.getId();

        // Get all the googleAddressList where address equals to addressId
        defaultGoogleAddressShouldBeFound("addressId.equals=" + addressId);

        // Get all the googleAddressList where address equals to (addressId + 1)
        defaultGoogleAddressShouldNotBeFound("addressId.equals=" + (addressId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGoogleAddressShouldBeFound(String filter) throws Exception {
        restGoogleAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(googleAddress.getId().intValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].html").value(hasItem(DEFAULT_HTML)))
            .andExpect(jsonPath("$.[*].formatted").value(hasItem(DEFAULT_FORMATTED)))
            .andExpect(jsonPath("$.[*].types").value(hasItem(DEFAULT_TYPES)));

        // Check, that the count call also returns 1
        restGoogleAddressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGoogleAddressShouldNotBeFound(String filter) throws Exception {
        restGoogleAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGoogleAddressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGoogleAddress() throws Exception {
        // Get the googleAddress
        restGoogleAddressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGoogleAddress() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        int databaseSizeBeforeUpdate = googleAddressRepository.findAll().size();

        // Update the googleAddress
        GoogleAddress updatedGoogleAddress = googleAddressRepository.findById(googleAddress.getId()).get();
        // Disconnect from session so that the updates on updatedGoogleAddress are not directly saved in db
        em.detach(updatedGoogleAddress);
        updatedGoogleAddress
            .position(UPDATED_POSITION)
            .url(UPDATED_URL)
            .html(UPDATED_HTML)
            .formatted(UPDATED_FORMATTED)
            .types(UPDATED_TYPES);
        GoogleAddressDTO googleAddressDTO = googleAddressMapper.toDto(updatedGoogleAddress);

        restGoogleAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, googleAddressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleAddressDTO))
            )
            .andExpect(status().isOk());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeUpdate);
        GoogleAddress testGoogleAddress = googleAddressList.get(googleAddressList.size() - 1);
        assertThat(testGoogleAddress.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testGoogleAddress.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testGoogleAddress.getHtml()).isEqualTo(UPDATED_HTML);
        assertThat(testGoogleAddress.getFormatted()).isEqualTo(UPDATED_FORMATTED);
        assertThat(testGoogleAddress.getTypes()).isEqualTo(UPDATED_TYPES);
    }

    @Test
    @Transactional
    void putNonExistingGoogleAddress() throws Exception {
        int databaseSizeBeforeUpdate = googleAddressRepository.findAll().size();
        googleAddress.setId(count.incrementAndGet());

        // Create the GoogleAddress
        GoogleAddressDTO googleAddressDTO = googleAddressMapper.toDto(googleAddress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGoogleAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, googleAddressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleAddressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGoogleAddress() throws Exception {
        int databaseSizeBeforeUpdate = googleAddressRepository.findAll().size();
        googleAddress.setId(count.incrementAndGet());

        // Create the GoogleAddress
        GoogleAddressDTO googleAddressDTO = googleAddressMapper.toDto(googleAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoogleAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleAddressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGoogleAddress() throws Exception {
        int databaseSizeBeforeUpdate = googleAddressRepository.findAll().size();
        googleAddress.setId(count.incrementAndGet());

        // Create the GoogleAddress
        GoogleAddressDTO googleAddressDTO = googleAddressMapper.toDto(googleAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoogleAddressMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(googleAddressDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGoogleAddressWithPatch() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        int databaseSizeBeforeUpdate = googleAddressRepository.findAll().size();

        // Update the googleAddress using partial update
        GoogleAddress partialUpdatedGoogleAddress = new GoogleAddress();
        partialUpdatedGoogleAddress.setId(googleAddress.getId());

        partialUpdatedGoogleAddress.position(UPDATED_POSITION).url(UPDATED_URL).html(UPDATED_HTML);

        restGoogleAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGoogleAddress.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGoogleAddress))
            )
            .andExpect(status().isOk());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeUpdate);
        GoogleAddress testGoogleAddress = googleAddressList.get(googleAddressList.size() - 1);
        assertThat(testGoogleAddress.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testGoogleAddress.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testGoogleAddress.getHtml()).isEqualTo(UPDATED_HTML);
        assertThat(testGoogleAddress.getFormatted()).isEqualTo(DEFAULT_FORMATTED);
        assertThat(testGoogleAddress.getTypes()).isEqualTo(DEFAULT_TYPES);
    }

    @Test
    @Transactional
    void fullUpdateGoogleAddressWithPatch() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        int databaseSizeBeforeUpdate = googleAddressRepository.findAll().size();

        // Update the googleAddress using partial update
        GoogleAddress partialUpdatedGoogleAddress = new GoogleAddress();
        partialUpdatedGoogleAddress.setId(googleAddress.getId());

        partialUpdatedGoogleAddress
            .position(UPDATED_POSITION)
            .url(UPDATED_URL)
            .html(UPDATED_HTML)
            .formatted(UPDATED_FORMATTED)
            .types(UPDATED_TYPES);

        restGoogleAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGoogleAddress.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGoogleAddress))
            )
            .andExpect(status().isOk());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeUpdate);
        GoogleAddress testGoogleAddress = googleAddressList.get(googleAddressList.size() - 1);
        assertThat(testGoogleAddress.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testGoogleAddress.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testGoogleAddress.getHtml()).isEqualTo(UPDATED_HTML);
        assertThat(testGoogleAddress.getFormatted()).isEqualTo(UPDATED_FORMATTED);
        assertThat(testGoogleAddress.getTypes()).isEqualTo(UPDATED_TYPES);
    }

    @Test
    @Transactional
    void patchNonExistingGoogleAddress() throws Exception {
        int databaseSizeBeforeUpdate = googleAddressRepository.findAll().size();
        googleAddress.setId(count.incrementAndGet());

        // Create the GoogleAddress
        GoogleAddressDTO googleAddressDTO = googleAddressMapper.toDto(googleAddress);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGoogleAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, googleAddressDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(googleAddressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGoogleAddress() throws Exception {
        int databaseSizeBeforeUpdate = googleAddressRepository.findAll().size();
        googleAddress.setId(count.incrementAndGet());

        // Create the GoogleAddress
        GoogleAddressDTO googleAddressDTO = googleAddressMapper.toDto(googleAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoogleAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(googleAddressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGoogleAddress() throws Exception {
        int databaseSizeBeforeUpdate = googleAddressRepository.findAll().size();
        googleAddress.setId(count.incrementAndGet());

        // Create the GoogleAddress
        GoogleAddressDTO googleAddressDTO = googleAddressMapper.toDto(googleAddress);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoogleAddressMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(googleAddressDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GoogleAddress in the database
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGoogleAddress() throws Exception {
        // Initialize the database
        googleAddressRepository.saveAndFlush(googleAddress);

        int databaseSizeBeforeDelete = googleAddressRepository.findAll().size();

        // Delete the googleAddress
        restGoogleAddressMockMvc
            .perform(delete(ENTITY_API_URL_ID, googleAddress.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GoogleAddress> googleAddressList = googleAddressRepository.findAll();
        assertThat(googleAddressList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
