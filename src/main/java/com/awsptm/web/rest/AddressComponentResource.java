package com.awsptm.web.rest;

import com.awsptm.repository.AddressComponentRepository;
import com.awsptm.service.AddressComponentQueryService;
import com.awsptm.service.AddressComponentService;
import com.awsptm.service.criteria.AddressComponentCriteria;
import com.awsptm.service.dto.AddressComponentDTO;
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
 * REST controller for managing {@link com.awsptm.domain.AddressComponent}.
 */
@RestController
@RequestMapping("/api")
public class AddressComponentResource {

    private final Logger log = LoggerFactory.getLogger(AddressComponentResource.class);

    private static final String ENTITY_NAME = "addressComponent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AddressComponentService addressComponentService;

    private final AddressComponentRepository addressComponentRepository;

    private final AddressComponentQueryService addressComponentQueryService;

    public AddressComponentResource(
        AddressComponentService addressComponentService,
        AddressComponentRepository addressComponentRepository,
        AddressComponentQueryService addressComponentQueryService
    ) {
        this.addressComponentService = addressComponentService;
        this.addressComponentRepository = addressComponentRepository;
        this.addressComponentQueryService = addressComponentQueryService;
    }

    /**
     * {@code POST  /address-components} : Create a new addressComponent.
     *
     * @param addressComponentDTO the addressComponentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new addressComponentDTO, or with status {@code 400 (Bad Request)} if the addressComponent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/address-components")
    public ResponseEntity<AddressComponentDTO> createAddressComponent(@RequestBody AddressComponentDTO addressComponentDTO)
        throws URISyntaxException {
        log.debug("REST request to save AddressComponent : {}", addressComponentDTO);
        if (addressComponentDTO.getId() != null) {
            throw new BadRequestAlertException("A new addressComponent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AddressComponentDTO result = addressComponentService.save(addressComponentDTO);
        return ResponseEntity
            .created(new URI("/api/address-components/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /address-components/:id} : Updates an existing addressComponent.
     *
     * @param id the id of the addressComponentDTO to save.
     * @param addressComponentDTO the addressComponentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addressComponentDTO,
     * or with status {@code 400 (Bad Request)} if the addressComponentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the addressComponentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/address-components/{id}")
    public ResponseEntity<AddressComponentDTO> updateAddressComponent(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AddressComponentDTO addressComponentDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AddressComponent : {}, {}", id, addressComponentDTO);
        if (addressComponentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addressComponentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressComponentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AddressComponentDTO result = addressComponentService.save(addressComponentDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, addressComponentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /address-components/:id} : Partial updates given fields of an existing addressComponent, field will ignore if it is null
     *
     * @param id the id of the addressComponentDTO to save.
     * @param addressComponentDTO the addressComponentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated addressComponentDTO,
     * or with status {@code 400 (Bad Request)} if the addressComponentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the addressComponentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the addressComponentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/address-components/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AddressComponentDTO> partialUpdateAddressComponent(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AddressComponentDTO addressComponentDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AddressComponent partially : {}, {}", id, addressComponentDTO);
        if (addressComponentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, addressComponentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressComponentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AddressComponentDTO> result = addressComponentService.partialUpdate(addressComponentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, addressComponentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /address-components} : get all the addressComponents.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of addressComponents in body.
     */
    @GetMapping("/address-components")
    public ResponseEntity<List<AddressComponentDTO>> getAllAddressComponents(
        AddressComponentCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get AddressComponents by criteria: {}", criteria);
        Page<AddressComponentDTO> page = addressComponentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /address-components/count} : count all the addressComponents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/address-components/count")
    public ResponseEntity<Long> countAddressComponents(AddressComponentCriteria criteria) {
        log.debug("REST request to count AddressComponents by criteria: {}", criteria);
        return ResponseEntity.ok().body(addressComponentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /address-components/:id} : get the "id" addressComponent.
     *
     * @param id the id of the addressComponentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the addressComponentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/address-components/{id}")
    public ResponseEntity<AddressComponentDTO> getAddressComponent(@PathVariable Long id) {
        log.debug("REST request to get AddressComponent : {}", id);
        Optional<AddressComponentDTO> addressComponentDTO = addressComponentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(addressComponentDTO);
    }

    /**
     * {@code DELETE  /address-components/:id} : delete the "id" addressComponent.
     *
     * @param id the id of the addressComponentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/address-components/{id}")
    public ResponseEntity<Void> deleteAddressComponent(@PathVariable Long id) {
        log.debug("REST request to delete AddressComponent : {}", id);
        addressComponentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
