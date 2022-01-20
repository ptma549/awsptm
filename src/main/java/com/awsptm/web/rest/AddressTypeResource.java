package com.awsptm.web.rest;

import com.awsptm.repository.AddressTypeRepository;
import com.awsptm.service.AddressTypeQueryService;
import com.awsptm.service.AddressTypeService;
import com.awsptm.service.criteria.AddressTypeCriteria;
import com.awsptm.service.dto.AddressTypeDTO;
import com.awsptm.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.awsptm.domain.AddressType}.
 */
@RestController
@RequestMapping("/api")
public class AddressTypeResource {

    private final Logger log = LoggerFactory.getLogger(AddressTypeResource.class);

    private static final String ENTITY_NAME = "addressType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AddressTypeService addressTypeService;

    private final AddressTypeRepository addressTypeRepository;

    private final AddressTypeQueryService addressTypeQueryService;

    public AddressTypeResource(
        AddressTypeService addressTypeService,
        AddressTypeRepository addressTypeRepository,
        AddressTypeQueryService addressTypeQueryService
    ) {
        this.addressTypeService = addressTypeService;
        this.addressTypeRepository = addressTypeRepository;
        this.addressTypeQueryService = addressTypeQueryService;
    }

    /**
     * {@code POST  /address-types} : Create a new addressType.
     *
     * @param addressTypeDTO the addressTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new addressTypeDTO, or with status {@code 400 (Bad Request)} if the addressType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/address-types")
    public ResponseEntity<AddressTypeDTO> createAddressType(@RequestBody AddressTypeDTO addressTypeDTO) throws URISyntaxException {
        log.debug("REST request to save AddressType : {}", addressTypeDTO);
        if (addressTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new addressType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AddressTypeDTO result = addressTypeService.save(addressTypeDTO);
        return ResponseEntity
            .created(new URI("/api/address-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /address-types/:id} : Updates an existing addressType.
     *
     * @param id the id of the addressTypeDTO to save.
     * @param addressTypeDTO the addressTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addressTypeDTO,
     * or with status {@code 400 (Bad Request)} if the addressTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the addressTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/address-types/{id}")
    public ResponseEntity<AddressTypeDTO> updateAddressType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AddressTypeDTO addressTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AddressType : {}, {}", id, addressTypeDTO);
        if (addressTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addressTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AddressTypeDTO result = addressTypeService.save(addressTypeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, addressTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /address-types/:id} : Partial updates given fields of an existing addressType, field will ignore if it is null
     *
     * @param id the id of the addressTypeDTO to save.
     * @param addressTypeDTO the addressTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addressTypeDTO,
     * or with status {@code 400 (Bad Request)} if the addressTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the addressTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the addressTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/address-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AddressTypeDTO> partialUpdateAddressType(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AddressTypeDTO addressTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AddressType partially : {}, {}", id, addressTypeDTO);
        if (addressTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addressTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AddressTypeDTO> result = addressTypeService.partialUpdate(addressTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, addressTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /address-types} : get all the addressTypes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of addressTypes in body.
     */
    @GetMapping("/address-types")
    public ResponseEntity<List<AddressTypeDTO>> getAllAddressTypes(
        AddressTypeCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get AddressTypes by criteria: {}", criteria);
        Page<AddressTypeDTO> page = addressTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /address-types/count} : count all the addressTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/address-types/count")
    public ResponseEntity<Long> countAddressTypes(AddressTypeCriteria criteria) {
        log.debug("REST request to count AddressTypes by criteria: {}", criteria);
        return ResponseEntity.ok().body(addressTypeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /address-types/:id} : get the "id" addressType.
     *
     * @param id the id of the addressTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the addressTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/address-types/{id}")
    public ResponseEntity<AddressTypeDTO> getAddressType(@PathVariable Long id) {
        log.debug("REST request to get AddressType : {}", id);
        Optional<AddressTypeDTO> addressTypeDTO = addressTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(addressTypeDTO);
    }

    /**
     * {@code DELETE  /address-types/:id} : delete the "id" addressType.
     *
     * @param id the id of the addressTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/address-types/{id}")
    public ResponseEntity<Void> deleteAddressType(@PathVariable Long id) {
        log.debug("REST request to delete AddressType : {}", id);
        addressTypeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
