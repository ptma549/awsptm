package com.awsptm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.awsptm.IntegrationTest;
import com.awsptm.domain.Address;
import com.awsptm.domain.GoogleAddress;
import com.awsptm.domain.Inspection;
import com.awsptm.domain.Job;
import com.awsptm.repository.AddressRepository;
import com.awsptm.service.criteria.AddressCriteria;
import com.awsptm.service.dto.AddressDTO;
import com.awsptm.service.mapper.AddressMapper;
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
 * Integration tests for the {@link AddressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AddressResourceIT {

    private static final String DEFAULT_POSTCODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTCODE = "BBBBBBBBBB";

    private static final String DEFAULT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_POSITION = "AAAAAAAAAA";
    private static final String UPDATED_POSITION = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_TOWN = "AAAAAAAAAA";
    private static final String UPDATED_TOWN = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAddressMockMvc;

    private Address address;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createEntity(EntityManager em) {
        Address address = new Address()
            .postcode(DEFAULT_POSTCODE)
            .number(DEFAULT_NUMBER)
            .position(DEFAULT_POSITION)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .town(DEFAULT_TOWN)
            .county(DEFAULT_COUNTY);
        // Add required entity
        GoogleAddress googleAddress;
        if (TestUtil.findAll(em, GoogleAddress.class).isEmpty()) {
            googleAddress = GoogleAddressResourceIT.createEntity(em);
            em.persist(googleAddress);
            em.flush();
        } else {
            googleAddress = TestUtil.findAll(em, GoogleAddress.class).get(0);
        }
        address.setGoogleAddress(googleAddress);
        return address;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createUpdatedEntity(EntityManager em) {
        Address address = new Address()
            .postcode(UPDATED_POSTCODE)
            .number(UPDATED_NUMBER)
            .position(UPDATED_POSITION)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .town(UPDATED_TOWN)
            .county(UPDATED_COUNTY);
        // Add required entity
        GoogleAddress googleAddress;
        if (TestUtil.findAll(em, GoogleAddress.class).isEmpty()) {
            googleAddress = GoogleAddressResourceIT.createUpdatedEntity(em);
            em.persist(googleAddress);
            em.flush();
        } else {
            googleAddress = TestUtil.findAll(em, GoogleAddress.class).get(0);
        }
        address.setGoogleAddress(googleAddress);
        return address;
    }

    @BeforeEach
    public void initTest() {
        address = createEntity(em);
    }

    @Test
    @Transactional
    void createAddress() throws Exception {
        int databaseSizeBeforeCreate = addressRepository.findAll().size();
        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);
        restAddressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addressDTO)))
            .andExpect(status().isCreated());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeCreate + 1);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getPostcode()).isEqualTo(DEFAULT_POSTCODE);
        assertThat(testAddress.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testAddress.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testAddress.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testAddress.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testAddress.getTown()).isEqualTo(DEFAULT_TOWN);
        assertThat(testAddress.getCounty()).isEqualTo(DEFAULT_COUNTY);

        // Validate the id for MapsId, the ids must be same
        assertThat(testAddress.getId()).isEqualTo(testAddress.getGoogleAddress().getId());
    }

    @Test
    @Transactional
    void createAddressWithExistingId() throws Exception {
        // Create the Address with an existing ID
        address.setId(1L);
        AddressDTO addressDTO = addressMapper.toDto(address);

        int databaseSizeBeforeCreate = addressRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAddressMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addressDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateAddressMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);
        int databaseSizeBeforeCreate = addressRepository.findAll().size();

        // Add a new parent entity
        GoogleAddress googleAddress = GoogleAddressResourceIT.createUpdatedEntity(em);
        em.persist(googleAddress);
        em.flush();

        // Load the address
        Address updatedAddress = addressRepository.findById(address.getId()).get();
        assertThat(updatedAddress).isNotNull();
        // Disconnect from session so that the updates on updatedAddress are not directly saved in db
        em.detach(updatedAddress);

        // Update the GoogleAddress with new association value
        updatedAddress.setGoogleAddress(googleAddress);
        AddressDTO updatedAddressDTO = addressMapper.toDto(updatedAddress);
        assertThat(updatedAddressDTO).isNotNull();

        // Update the entity
        restAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAddressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAddressDTO))
            )
            .andExpect(status().isOk());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeCreate);
        Address testAddress = addressList.get(addressList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testAddress.getId()).isEqualTo(testAddress.getGoogleAddress().getId());
    }

    @Test
    @Transactional
    void getAllAddresses() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList
        restAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(address.getId().intValue())))
            .andExpect(jsonPath("$.[*].postcode").value(hasItem(DEFAULT_POSTCODE)))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].town").value(hasItem(DEFAULT_TOWN)))
            .andExpect(jsonPath("$.[*].county").value(hasItem(DEFAULT_COUNTY)));
    }

    @Test
    @Transactional
    void getAddress() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get the address
        restAddressMockMvc
            .perform(get(ENTITY_API_URL_ID, address.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(address.getId().intValue()))
            .andExpect(jsonPath("$.postcode").value(DEFAULT_POSTCODE))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION))
            .andExpect(jsonPath("$.addressLine1").value(DEFAULT_ADDRESS_LINE_1))
            .andExpect(jsonPath("$.addressLine2").value(DEFAULT_ADDRESS_LINE_2))
            .andExpect(jsonPath("$.town").value(DEFAULT_TOWN))
            .andExpect(jsonPath("$.county").value(DEFAULT_COUNTY));
    }

    @Test
    @Transactional
    void getAddressesByIdFiltering() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        Long id = address.getId();

        defaultAddressShouldBeFound("id.equals=" + id);
        defaultAddressShouldNotBeFound("id.notEquals=" + id);

        defaultAddressShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAddressShouldNotBeFound("id.greaterThan=" + id);

        defaultAddressShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAddressShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAddressesByPostcodeIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where postcode equals to DEFAULT_POSTCODE
        defaultAddressShouldBeFound("postcode.equals=" + DEFAULT_POSTCODE);

        // Get all the addressList where postcode equals to UPDATED_POSTCODE
        defaultAddressShouldNotBeFound("postcode.equals=" + UPDATED_POSTCODE);
    }

    @Test
    @Transactional
    void getAllAddressesByPostcodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where postcode not equals to DEFAULT_POSTCODE
        defaultAddressShouldNotBeFound("postcode.notEquals=" + DEFAULT_POSTCODE);

        // Get all the addressList where postcode not equals to UPDATED_POSTCODE
        defaultAddressShouldBeFound("postcode.notEquals=" + UPDATED_POSTCODE);
    }

    @Test
    @Transactional
    void getAllAddressesByPostcodeIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where postcode in DEFAULT_POSTCODE or UPDATED_POSTCODE
        defaultAddressShouldBeFound("postcode.in=" + DEFAULT_POSTCODE + "," + UPDATED_POSTCODE);

        // Get all the addressList where postcode equals to UPDATED_POSTCODE
        defaultAddressShouldNotBeFound("postcode.in=" + UPDATED_POSTCODE);
    }

    @Test
    @Transactional
    void getAllAddressesByPostcodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where postcode is not null
        defaultAddressShouldBeFound("postcode.specified=true");

        // Get all the addressList where postcode is null
        defaultAddressShouldNotBeFound("postcode.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByPostcodeContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where postcode contains DEFAULT_POSTCODE
        defaultAddressShouldBeFound("postcode.contains=" + DEFAULT_POSTCODE);

        // Get all the addressList where postcode contains UPDATED_POSTCODE
        defaultAddressShouldNotBeFound("postcode.contains=" + UPDATED_POSTCODE);
    }

    @Test
    @Transactional
    void getAllAddressesByPostcodeNotContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where postcode does not contain DEFAULT_POSTCODE
        defaultAddressShouldNotBeFound("postcode.doesNotContain=" + DEFAULT_POSTCODE);

        // Get all the addressList where postcode does not contain UPDATED_POSTCODE
        defaultAddressShouldBeFound("postcode.doesNotContain=" + UPDATED_POSTCODE);
    }

    @Test
    @Transactional
    void getAllAddressesByNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where number equals to DEFAULT_NUMBER
        defaultAddressShouldBeFound("number.equals=" + DEFAULT_NUMBER);

        // Get all the addressList where number equals to UPDATED_NUMBER
        defaultAddressShouldNotBeFound("number.equals=" + UPDATED_NUMBER);
    }

    @Test
    @Transactional
    void getAllAddressesByNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where number not equals to DEFAULT_NUMBER
        defaultAddressShouldNotBeFound("number.notEquals=" + DEFAULT_NUMBER);

        // Get all the addressList where number not equals to UPDATED_NUMBER
        defaultAddressShouldBeFound("number.notEquals=" + UPDATED_NUMBER);
    }

    @Test
    @Transactional
    void getAllAddressesByNumberIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where number in DEFAULT_NUMBER or UPDATED_NUMBER
        defaultAddressShouldBeFound("number.in=" + DEFAULT_NUMBER + "," + UPDATED_NUMBER);

        // Get all the addressList where number equals to UPDATED_NUMBER
        defaultAddressShouldNotBeFound("number.in=" + UPDATED_NUMBER);
    }

    @Test
    @Transactional
    void getAllAddressesByNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where number is not null
        defaultAddressShouldBeFound("number.specified=true");

        // Get all the addressList where number is null
        defaultAddressShouldNotBeFound("number.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByNumberContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where number contains DEFAULT_NUMBER
        defaultAddressShouldBeFound("number.contains=" + DEFAULT_NUMBER);

        // Get all the addressList where number contains UPDATED_NUMBER
        defaultAddressShouldNotBeFound("number.contains=" + UPDATED_NUMBER);
    }

    @Test
    @Transactional
    void getAllAddressesByNumberNotContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where number does not contain DEFAULT_NUMBER
        defaultAddressShouldNotBeFound("number.doesNotContain=" + DEFAULT_NUMBER);

        // Get all the addressList where number does not contain UPDATED_NUMBER
        defaultAddressShouldBeFound("number.doesNotContain=" + UPDATED_NUMBER);
    }

    @Test
    @Transactional
    void getAllAddressesByPositionIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where position equals to DEFAULT_POSITION
        defaultAddressShouldBeFound("position.equals=" + DEFAULT_POSITION);

        // Get all the addressList where position equals to UPDATED_POSITION
        defaultAddressShouldNotBeFound("position.equals=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressesByPositionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where position not equals to DEFAULT_POSITION
        defaultAddressShouldNotBeFound("position.notEquals=" + DEFAULT_POSITION);

        // Get all the addressList where position not equals to UPDATED_POSITION
        defaultAddressShouldBeFound("position.notEquals=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressesByPositionIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where position in DEFAULT_POSITION or UPDATED_POSITION
        defaultAddressShouldBeFound("position.in=" + DEFAULT_POSITION + "," + UPDATED_POSITION);

        // Get all the addressList where position equals to UPDATED_POSITION
        defaultAddressShouldNotBeFound("position.in=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressesByPositionIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where position is not null
        defaultAddressShouldBeFound("position.specified=true");

        // Get all the addressList where position is null
        defaultAddressShouldNotBeFound("position.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByPositionContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where position contains DEFAULT_POSITION
        defaultAddressShouldBeFound("position.contains=" + DEFAULT_POSITION);

        // Get all the addressList where position contains UPDATED_POSITION
        defaultAddressShouldNotBeFound("position.contains=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressesByPositionNotContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where position does not contain DEFAULT_POSITION
        defaultAddressShouldNotBeFound("position.doesNotContain=" + DEFAULT_POSITION);

        // Get all the addressList where position does not contain UPDATED_POSITION
        defaultAddressShouldBeFound("position.doesNotContain=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine1IsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine1 equals to DEFAULT_ADDRESS_LINE_1
        defaultAddressShouldBeFound("addressLine1.equals=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the addressList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultAddressShouldNotBeFound("addressLine1.equals=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine1IsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine1 not equals to DEFAULT_ADDRESS_LINE_1
        defaultAddressShouldNotBeFound("addressLine1.notEquals=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the addressList where addressLine1 not equals to UPDATED_ADDRESS_LINE_1
        defaultAddressShouldBeFound("addressLine1.notEquals=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine1IsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine1 in DEFAULT_ADDRESS_LINE_1 or UPDATED_ADDRESS_LINE_1
        defaultAddressShouldBeFound("addressLine1.in=" + DEFAULT_ADDRESS_LINE_1 + "," + UPDATED_ADDRESS_LINE_1);

        // Get all the addressList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultAddressShouldNotBeFound("addressLine1.in=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine1IsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine1 is not null
        defaultAddressShouldBeFound("addressLine1.specified=true");

        // Get all the addressList where addressLine1 is null
        defaultAddressShouldNotBeFound("addressLine1.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine1ContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine1 contains DEFAULT_ADDRESS_LINE_1
        defaultAddressShouldBeFound("addressLine1.contains=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the addressList where addressLine1 contains UPDATED_ADDRESS_LINE_1
        defaultAddressShouldNotBeFound("addressLine1.contains=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine1NotContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine1 does not contain DEFAULT_ADDRESS_LINE_1
        defaultAddressShouldNotBeFound("addressLine1.doesNotContain=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the addressList where addressLine1 does not contain UPDATED_ADDRESS_LINE_1
        defaultAddressShouldBeFound("addressLine1.doesNotContain=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine2IsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine2 equals to DEFAULT_ADDRESS_LINE_2
        defaultAddressShouldBeFound("addressLine2.equals=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the addressList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultAddressShouldNotBeFound("addressLine2.equals=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine2IsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine2 not equals to DEFAULT_ADDRESS_LINE_2
        defaultAddressShouldNotBeFound("addressLine2.notEquals=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the addressList where addressLine2 not equals to UPDATED_ADDRESS_LINE_2
        defaultAddressShouldBeFound("addressLine2.notEquals=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine2IsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine2 in DEFAULT_ADDRESS_LINE_2 or UPDATED_ADDRESS_LINE_2
        defaultAddressShouldBeFound("addressLine2.in=" + DEFAULT_ADDRESS_LINE_2 + "," + UPDATED_ADDRESS_LINE_2);

        // Get all the addressList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultAddressShouldNotBeFound("addressLine2.in=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine2IsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine2 is not null
        defaultAddressShouldBeFound("addressLine2.specified=true");

        // Get all the addressList where addressLine2 is null
        defaultAddressShouldNotBeFound("addressLine2.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine2ContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine2 contains DEFAULT_ADDRESS_LINE_2
        defaultAddressShouldBeFound("addressLine2.contains=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the addressList where addressLine2 contains UPDATED_ADDRESS_LINE_2
        defaultAddressShouldNotBeFound("addressLine2.contains=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllAddressesByAddressLine2NotContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine2 does not contain DEFAULT_ADDRESS_LINE_2
        defaultAddressShouldNotBeFound("addressLine2.doesNotContain=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the addressList where addressLine2 does not contain UPDATED_ADDRESS_LINE_2
        defaultAddressShouldBeFound("addressLine2.doesNotContain=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllAddressesByTownIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where town equals to DEFAULT_TOWN
        defaultAddressShouldBeFound("town.equals=" + DEFAULT_TOWN);

        // Get all the addressList where town equals to UPDATED_TOWN
        defaultAddressShouldNotBeFound("town.equals=" + UPDATED_TOWN);
    }

    @Test
    @Transactional
    void getAllAddressesByTownIsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where town not equals to DEFAULT_TOWN
        defaultAddressShouldNotBeFound("town.notEquals=" + DEFAULT_TOWN);

        // Get all the addressList where town not equals to UPDATED_TOWN
        defaultAddressShouldBeFound("town.notEquals=" + UPDATED_TOWN);
    }

    @Test
    @Transactional
    void getAllAddressesByTownIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where town in DEFAULT_TOWN or UPDATED_TOWN
        defaultAddressShouldBeFound("town.in=" + DEFAULT_TOWN + "," + UPDATED_TOWN);

        // Get all the addressList where town equals to UPDATED_TOWN
        defaultAddressShouldNotBeFound("town.in=" + UPDATED_TOWN);
    }

    @Test
    @Transactional
    void getAllAddressesByTownIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where town is not null
        defaultAddressShouldBeFound("town.specified=true");

        // Get all the addressList where town is null
        defaultAddressShouldNotBeFound("town.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByTownContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where town contains DEFAULT_TOWN
        defaultAddressShouldBeFound("town.contains=" + DEFAULT_TOWN);

        // Get all the addressList where town contains UPDATED_TOWN
        defaultAddressShouldNotBeFound("town.contains=" + UPDATED_TOWN);
    }

    @Test
    @Transactional
    void getAllAddressesByTownNotContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where town does not contain DEFAULT_TOWN
        defaultAddressShouldNotBeFound("town.doesNotContain=" + DEFAULT_TOWN);

        // Get all the addressList where town does not contain UPDATED_TOWN
        defaultAddressShouldBeFound("town.doesNotContain=" + UPDATED_TOWN);
    }

    @Test
    @Transactional
    void getAllAddressesByCountyIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where county equals to DEFAULT_COUNTY
        defaultAddressShouldBeFound("county.equals=" + DEFAULT_COUNTY);

        // Get all the addressList where county equals to UPDATED_COUNTY
        defaultAddressShouldNotBeFound("county.equals=" + UPDATED_COUNTY);
    }

    @Test
    @Transactional
    void getAllAddressesByCountyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where county not equals to DEFAULT_COUNTY
        defaultAddressShouldNotBeFound("county.notEquals=" + DEFAULT_COUNTY);

        // Get all the addressList where county not equals to UPDATED_COUNTY
        defaultAddressShouldBeFound("county.notEquals=" + UPDATED_COUNTY);
    }

    @Test
    @Transactional
    void getAllAddressesByCountyIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where county in DEFAULT_COUNTY or UPDATED_COUNTY
        defaultAddressShouldBeFound("county.in=" + DEFAULT_COUNTY + "," + UPDATED_COUNTY);

        // Get all the addressList where county equals to UPDATED_COUNTY
        defaultAddressShouldNotBeFound("county.in=" + UPDATED_COUNTY);
    }

    @Test
    @Transactional
    void getAllAddressesByCountyIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where county is not null
        defaultAddressShouldBeFound("county.specified=true");

        // Get all the addressList where county is null
        defaultAddressShouldNotBeFound("county.specified=false");
    }

    @Test
    @Transactional
    void getAllAddressesByCountyContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where county contains DEFAULT_COUNTY
        defaultAddressShouldBeFound("county.contains=" + DEFAULT_COUNTY);

        // Get all the addressList where county contains UPDATED_COUNTY
        defaultAddressShouldNotBeFound("county.contains=" + UPDATED_COUNTY);
    }

    @Test
    @Transactional
    void getAllAddressesByCountyNotContainsSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where county does not contain DEFAULT_COUNTY
        defaultAddressShouldNotBeFound("county.doesNotContain=" + DEFAULT_COUNTY);

        // Get all the addressList where county does not contain UPDATED_COUNTY
        defaultAddressShouldBeFound("county.doesNotContain=" + UPDATED_COUNTY);
    }

    @Test
    @Transactional
    void getAllAddressesByGoogleAddressIsEqualToSomething() throws Exception {
        // Get already existing entity
        GoogleAddress googleAddress = address.getGoogleAddress();
        addressRepository.saveAndFlush(address);
        Long googleAddressId = googleAddress.getId();

        // Get all the addressList where googleAddress equals to googleAddressId
        defaultAddressShouldBeFound("googleAddressId.equals=" + googleAddressId);

        // Get all the addressList where googleAddress equals to (googleAddressId + 1)
        defaultAddressShouldNotBeFound("googleAddressId.equals=" + (googleAddressId + 1));
    }

    @Test
    @Transactional
    void getAllAddressesByJobsIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);
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
        address.addJobs(jobs);
        addressRepository.saveAndFlush(address);
        Long jobsId = jobs.getId();

        // Get all the addressList where jobs equals to jobsId
        defaultAddressShouldBeFound("jobsId.equals=" + jobsId);

        // Get all the addressList where jobs equals to (jobsId + 1)
        defaultAddressShouldNotBeFound("jobsId.equals=" + (jobsId + 1));
    }

    @Test
    @Transactional
    void getAllAddressesByInspectionsIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);
        Inspection inspections;
        if (TestUtil.findAll(em, Inspection.class).isEmpty()) {
            inspections = InspectionResourceIT.createEntity(em);
            em.persist(inspections);
            em.flush();
        } else {
            inspections = TestUtil.findAll(em, Inspection.class).get(0);
        }
        em.persist(inspections);
        em.flush();
        address.addInspections(inspections);
        addressRepository.saveAndFlush(address);
        Long inspectionsId = inspections.getId();

        // Get all the addressList where inspections equals to inspectionsId
        defaultAddressShouldBeFound("inspectionsId.equals=" + inspectionsId);

        // Get all the addressList where inspections equals to (inspectionsId + 1)
        defaultAddressShouldNotBeFound("inspectionsId.equals=" + (inspectionsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAddressShouldBeFound(String filter) throws Exception {
        restAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(address.getId().intValue())))
            .andExpect(jsonPath("$.[*].postcode").value(hasItem(DEFAULT_POSTCODE)))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].town").value(hasItem(DEFAULT_TOWN)))
            .andExpect(jsonPath("$.[*].county").value(hasItem(DEFAULT_COUNTY)));

        // Check, that the count call also returns 1
        restAddressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAddressShouldNotBeFound(String filter) throws Exception {
        restAddressMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAddressMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAddress() throws Exception {
        // Get the address
        restAddressMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAddress() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        int databaseSizeBeforeUpdate = addressRepository.findAll().size();

        // Update the address
        Address updatedAddress = addressRepository.findById(address.getId()).get();
        // Disconnect from session so that the updates on updatedAddress are not directly saved in db
        em.detach(updatedAddress);
        updatedAddress
            .postcode(UPDATED_POSTCODE)
            .number(UPDATED_NUMBER)
            .position(UPDATED_POSITION)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .town(UPDATED_TOWN)
            .county(UPDATED_COUNTY);
        AddressDTO addressDTO = addressMapper.toDto(updatedAddress);

        restAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, addressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addressDTO))
            )
            .andExpect(status().isOk());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testAddress.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testAddress.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testAddress.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testAddress.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testAddress.getTown()).isEqualTo(UPDATED_TOWN);
        assertThat(testAddress.getCounty()).isEqualTo(UPDATED_COUNTY);
    }

    @Test
    @Transactional
    void putNonExistingAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, addressDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(addressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(addressDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        int databaseSizeBeforeUpdate = addressRepository.findAll().size();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress
            .number(UPDATED_NUMBER)
            .position(UPDATED_POSITION)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .county(UPDATED_COUNTY);

        restAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddress))
            )
            .andExpect(status().isOk());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getPostcode()).isEqualTo(DEFAULT_POSTCODE);
        assertThat(testAddress.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testAddress.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testAddress.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testAddress.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testAddress.getTown()).isEqualTo(DEFAULT_TOWN);
        assertThat(testAddress.getCounty()).isEqualTo(UPDATED_COUNTY);
    }

    @Test
    @Transactional
    void fullUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        int databaseSizeBeforeUpdate = addressRepository.findAll().size();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress
            .postcode(UPDATED_POSTCODE)
            .number(UPDATED_NUMBER)
            .position(UPDATED_POSITION)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .town(UPDATED_TOWN)
            .county(UPDATED_COUNTY);

        restAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAddress))
            )
            .andExpect(status().isOk());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testAddress.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testAddress.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testAddress.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testAddress.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testAddress.getTown()).isEqualTo(UPDATED_TOWN);
        assertThat(testAddress.getCounty()).isEqualTo(UPDATED_COUNTY);
    }

    @Test
    @Transactional
    void patchNonExistingAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, addressDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(addressDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAddressMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(addressDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAddress() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        int databaseSizeBeforeDelete = addressRepository.findAll().size();

        // Delete the address
        restAddressMockMvc
            .perform(delete(ENTITY_API_URL_ID, address.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
