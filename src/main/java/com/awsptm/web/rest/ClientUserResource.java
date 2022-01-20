package com.awsptm.web.rest;

import com.awsptm.repository.ClientUserRepository;
import com.awsptm.service.ClientUserQueryService;
import com.awsptm.service.ClientUserService;
import com.awsptm.service.criteria.ClientUserCriteria;
import com.awsptm.service.dto.ClientUserDTO;
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
 * REST controller for managing {@link com.awsptm.domain.ClientUser}.
 */
@RestController
@RequestMapping("/api")
public class ClientUserResource {

    private final Logger log = LoggerFactory.getLogger(ClientUserResource.class);

    private static final String ENTITY_NAME = "clientUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientUserService clientUserService;

    private final ClientUserRepository clientUserRepository;

    private final ClientUserQueryService clientUserQueryService;

    public ClientUserResource(
        ClientUserService clientUserService,
        ClientUserRepository clientUserRepository,
        ClientUserQueryService clientUserQueryService
    ) {
        this.clientUserService = clientUserService;
        this.clientUserRepository = clientUserRepository;
        this.clientUserQueryService = clientUserQueryService;
    }

    /**
     * {@code POST  /client-users} : Create a new clientUser.
     *
     * @param clientUserDTO the clientUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientUserDTO, or with status {@code 400 (Bad Request)} if the clientUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/client-users")
    public ResponseEntity<ClientUserDTO> createClientUser(@RequestBody ClientUserDTO clientUserDTO) throws URISyntaxException {
        log.debug("REST request to save ClientUser : {}", clientUserDTO);
        if (clientUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new clientUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientUserDTO result = clientUserService.save(clientUserDTO);
        return ResponseEntity
            .created(new URI("/api/client-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /client-users/:id} : Updates an existing clientUser.
     *
     * @param id the id of the clientUserDTO to save.
     * @param clientUserDTO the clientUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientUserDTO,
     * or with status {@code 400 (Bad Request)} if the clientUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/client-users/{id}")
    public ResponseEntity<ClientUserDTO> updateClientUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClientUserDTO clientUserDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ClientUser : {}, {}", id, clientUserDTO);
        if (clientUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClientUserDTO result = clientUserService.save(clientUserDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientUserDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /client-users/:id} : Partial updates given fields of an existing clientUser, field will ignore if it is null
     *
     * @param id the id of the clientUserDTO to save.
     * @param clientUserDTO the clientUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientUserDTO,
     * or with status {@code 400 (Bad Request)} if the clientUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clientUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/client-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientUserDTO> partialUpdateClientUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClientUserDTO clientUserDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClientUser partially : {}, {}", id, clientUserDTO);
        if (clientUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientUserDTO> result = clientUserService.partialUpdate(clientUserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientUserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /client-users} : get all the clientUsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientUsers in body.
     */
    @GetMapping("/client-users")
    public ResponseEntity<List<ClientUserDTO>> getAllClientUsers(
        ClientUserCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ClientUsers by criteria: {}", criteria);
        Page<ClientUserDTO> page = clientUserQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /client-users/count} : count all the clientUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/client-users/count")
    public ResponseEntity<Long> countClientUsers(ClientUserCriteria criteria) {
        log.debug("REST request to count ClientUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(clientUserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /client-users/:id} : get the "id" clientUser.
     *
     * @param id the id of the clientUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/client-users/{id}")
    public ResponseEntity<ClientUserDTO> getClientUser(@PathVariable Long id) {
        log.debug("REST request to get ClientUser : {}", id);
        Optional<ClientUserDTO> clientUserDTO = clientUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clientUserDTO);
    }

    /**
     * {@code DELETE  /client-users/:id} : delete the "id" clientUser.
     *
     * @param id the id of the clientUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/client-users/{id}")
    public ResponseEntity<Void> deleteClientUser(@PathVariable Long id) {
        log.debug("REST request to delete ClientUser : {}", id);
        clientUserService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
