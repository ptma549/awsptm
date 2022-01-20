package com.awsptm.web.rest;

import com.awsptm.repository.ClientOrganisationRepository;
import com.awsptm.service.ClientOrganisationQueryService;
import com.awsptm.service.ClientOrganisationService;
import com.awsptm.service.criteria.ClientOrganisationCriteria;
import com.awsptm.service.dto.ClientOrganisationDTO;
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
 * REST controller for managing {@link com.awsptm.domain.ClientOrganisation}.
 */
@RestController
@RequestMapping("/api")
public class ClientOrganisationResource {

    private final Logger log = LoggerFactory.getLogger(ClientOrganisationResource.class);

    private static final String ENTITY_NAME = "clientOrganisation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientOrganisationService clientOrganisationService;

    private final ClientOrganisationRepository clientOrganisationRepository;

    private final ClientOrganisationQueryService clientOrganisationQueryService;

    public ClientOrganisationResource(
        ClientOrganisationService clientOrganisationService,
        ClientOrganisationRepository clientOrganisationRepository,
        ClientOrganisationQueryService clientOrganisationQueryService
    ) {
        this.clientOrganisationService = clientOrganisationService;
        this.clientOrganisationRepository = clientOrganisationRepository;
        this.clientOrganisationQueryService = clientOrganisationQueryService;
    }

    /**
     * {@code POST  /client-organisations} : Create a new clientOrganisation.
     *
     * @param clientOrganisationDTO the clientOrganisationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientOrganisationDTO, or with status {@code 400 (Bad Request)} if the clientOrganisation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/client-organisations")
    public ResponseEntity<ClientOrganisationDTO> createClientOrganisation(@RequestBody ClientOrganisationDTO clientOrganisationDTO)
        throws URISyntaxException {
        log.debug("REST request to save ClientOrganisation : {}", clientOrganisationDTO);
        if (clientOrganisationDTO.getId() != null) {
            throw new BadRequestAlertException("A new clientOrganisation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientOrganisationDTO result = clientOrganisationService.save(clientOrganisationDTO);
        return ResponseEntity
            .created(new URI("/api/client-organisations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /client-organisations/:id} : Updates an existing clientOrganisation.
     *
     * @param id the id of the clientOrganisationDTO to save.
     * @param clientOrganisationDTO the clientOrganisationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientOrganisationDTO,
     * or with status {@code 400 (Bad Request)} if the clientOrganisationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientOrganisationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/client-organisations/{id}")
    public ResponseEntity<ClientOrganisationDTO> updateClientOrganisation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClientOrganisationDTO clientOrganisationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ClientOrganisation : {}, {}", id, clientOrganisationDTO);
        if (clientOrganisationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientOrganisationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientOrganisationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClientOrganisationDTO result = clientOrganisationService.save(clientOrganisationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientOrganisationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /client-organisations/:id} : Partial updates given fields of an existing clientOrganisation, field will ignore if it is null
     *
     * @param id the id of the clientOrganisationDTO to save.
     * @param clientOrganisationDTO the clientOrganisationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientOrganisationDTO,
     * or with status {@code 400 (Bad Request)} if the clientOrganisationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clientOrganisationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientOrganisationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/client-organisations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientOrganisationDTO> partialUpdateClientOrganisation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClientOrganisationDTO clientOrganisationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClientOrganisation partially : {}, {}", id, clientOrganisationDTO);
        if (clientOrganisationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientOrganisationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientOrganisationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientOrganisationDTO> result = clientOrganisationService.partialUpdate(clientOrganisationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientOrganisationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /client-organisations} : get all the clientOrganisations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientOrganisations in body.
     */
    @GetMapping("/client-organisations")
    public ResponseEntity<List<ClientOrganisationDTO>> getAllClientOrganisations(
        ClientOrganisationCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ClientOrganisations by criteria: {}", criteria);
        Page<ClientOrganisationDTO> page = clientOrganisationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /client-organisations/count} : count all the clientOrganisations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/client-organisations/count")
    public ResponseEntity<Long> countClientOrganisations(ClientOrganisationCriteria criteria) {
        log.debug("REST request to count ClientOrganisations by criteria: {}", criteria);
        return ResponseEntity.ok().body(clientOrganisationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /client-organisations/:id} : get the "id" clientOrganisation.
     *
     * @param id the id of the clientOrganisationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientOrganisationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/client-organisations/{id}")
    public ResponseEntity<ClientOrganisationDTO> getClientOrganisation(@PathVariable Long id) {
        log.debug("REST request to get ClientOrganisation : {}", id);
        Optional<ClientOrganisationDTO> clientOrganisationDTO = clientOrganisationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clientOrganisationDTO);
    }

    /**
     * {@code DELETE  /client-organisations/:id} : delete the "id" clientOrganisation.
     *
     * @param id the id of the clientOrganisationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/client-organisations/{id}")
    public ResponseEntity<Void> deleteClientOrganisation(@PathVariable Long id) {
        log.debug("REST request to delete ClientOrganisation : {}", id);
        clientOrganisationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
