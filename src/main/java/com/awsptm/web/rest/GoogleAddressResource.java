package com.awsptm.web.rest;

import com.awsptm.repository.GoogleAddressRepository;
import com.awsptm.service.GoogleAddressQueryService;
import com.awsptm.service.GoogleAddressService;
import com.awsptm.service.criteria.GoogleAddressCriteria;
import com.awsptm.service.dto.GoogleAddressDTO;
import com.awsptm.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
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
 * REST controller for managing {@link com.awsptm.domain.GoogleAddress}.
 */
@RestController
@RequestMapping("/api")
public class GoogleAddressResource {

    private final Logger log = LoggerFactory.getLogger(GoogleAddressResource.class);

    private static final String ENTITY_NAME = "googleAddress";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GoogleAddressService googleAddressService;

    private final GoogleAddressRepository googleAddressRepository;

    private final GoogleAddressQueryService googleAddressQueryService;

    public GoogleAddressResource(
        GoogleAddressService googleAddressService,
        GoogleAddressRepository googleAddressRepository,
        GoogleAddressQueryService googleAddressQueryService
    ) {
        this.googleAddressService = googleAddressService;
        this.googleAddressRepository = googleAddressRepository;
        this.googleAddressQueryService = googleAddressQueryService;
    }

    /**
     * {@code POST  /google-addresses} : Create a new googleAddress.
     *
     * @param googleAddressDTO the googleAddressDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new googleAddressDTO, or with status {@code 400 (Bad Request)} if the googleAddress has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/google-addresses")
    public ResponseEntity<GoogleAddressDTO> createGoogleAddress(@RequestBody GoogleAddressDTO googleAddressDTO) throws URISyntaxException {
        log.debug("REST request to save GoogleAddress : {}", googleAddressDTO);
        if (googleAddressDTO.getId() != null) {
            throw new BadRequestAlertException("A new googleAddress cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GoogleAddressDTO result = googleAddressService.save(googleAddressDTO);
        return ResponseEntity
            .created(new URI("/api/google-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /google-addresses/:id} : Updates an existing googleAddress.
     *
     * @param id the id of the googleAddressDTO to save.
     * @param googleAddressDTO the googleAddressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated googleAddressDTO,
     * or with status {@code 400 (Bad Request)} if the googleAddressDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the googleAddressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/google-addresses/{id}")
    public ResponseEntity<GoogleAddressDTO> updateGoogleAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GoogleAddressDTO googleAddressDTO
    ) throws URISyntaxException {
        log.debug("REST request to update GoogleAddress : {}, {}", id, googleAddressDTO);
        if (googleAddressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, googleAddressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!googleAddressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GoogleAddressDTO result = googleAddressService.save(googleAddressDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, googleAddressDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /google-addresses/:id} : Partial updates given fields of an existing googleAddress, field will ignore if it is null
     *
     * @param id the id of the googleAddressDTO to save.
     * @param googleAddressDTO the googleAddressDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated googleAddressDTO,
     * or with status {@code 400 (Bad Request)} if the googleAddressDTO is not valid,
     * or with status {@code 404 (Not Found)} if the googleAddressDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the googleAddressDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/google-addresses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GoogleAddressDTO> partialUpdateGoogleAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GoogleAddressDTO googleAddressDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update GoogleAddress partially : {}, {}", id, googleAddressDTO);
        if (googleAddressDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, googleAddressDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!googleAddressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GoogleAddressDTO> result = googleAddressService.partialUpdate(googleAddressDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, googleAddressDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /google-addresses} : get all the googleAddresses.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of googleAddresses in body.
     */
    @GetMapping("/google-addresses")
    public ResponseEntity<List<GoogleAddressDTO>> getAllGoogleAddresses(
        GoogleAddressCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get GoogleAddresses by criteria: {}", criteria);
        Page<GoogleAddressDTO> page = googleAddressQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /google-addresses/count} : count all the googleAddresses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/google-addresses/count")
    public ResponseEntity<Long> countGoogleAddresses(GoogleAddressCriteria criteria) {
        log.debug("REST request to count GoogleAddresses by criteria: {}", criteria);
        return ResponseEntity.ok().body(googleAddressQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /google-addresses/:id} : get the "id" googleAddress.
     *
     * @param id the id of the googleAddressDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the googleAddressDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/google-addresses/{id}")
    public ResponseEntity<GoogleAddressDTO> getGoogleAddress(@PathVariable Long id) {
        log.debug("REST request to get GoogleAddress : {}", id);
        Optional<GoogleAddressDTO> googleAddressDTO = googleAddressService.findOne(id);
        return ResponseUtil.wrapOrNotFound(googleAddressDTO);
    }

    /**
     * {@code DELETE  /google-addresses/:id} : delete the "id" googleAddress.
     *
     * @param id the id of the googleAddressDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/google-addresses/{id}")
    public ResponseEntity<Void> deleteGoogleAddress(@PathVariable Long id) {
        log.debug("REST request to delete GoogleAddress : {}", id);
        googleAddressService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
