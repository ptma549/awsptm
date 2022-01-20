package com.awsptm.web.rest;

import com.awsptm.repository.EngineerRepository;
import com.awsptm.service.EngineerQueryService;
import com.awsptm.service.EngineerService;
import com.awsptm.service.criteria.EngineerCriteria;
import com.awsptm.service.dto.EngineerDTO;
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
 * REST controller for managing {@link com.awsptm.domain.Engineer}.
 */
@RestController
@RequestMapping("/api")
public class EngineerResource {

    private final Logger log = LoggerFactory.getLogger(EngineerResource.class);

    private static final String ENTITY_NAME = "engineer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EngineerService engineerService;

    private final EngineerRepository engineerRepository;

    private final EngineerQueryService engineerQueryService;

    public EngineerResource(
        EngineerService engineerService,
        EngineerRepository engineerRepository,
        EngineerQueryService engineerQueryService
    ) {
        this.engineerService = engineerService;
        this.engineerRepository = engineerRepository;
        this.engineerQueryService = engineerQueryService;
    }

    /**
     * {@code POST  /engineers} : Create a new engineer.
     *
     * @param engineerDTO the engineerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new engineerDTO, or with status {@code 400 (Bad Request)} if the engineer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/engineers")
    public ResponseEntity<EngineerDTO> createEngineer(@RequestBody EngineerDTO engineerDTO) throws URISyntaxException {
        log.debug("REST request to save Engineer : {}", engineerDTO);
        if (engineerDTO.getId() != null) {
            throw new BadRequestAlertException("A new engineer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EngineerDTO result = engineerService.save(engineerDTO);
        return ResponseEntity
            .created(new URI("/api/engineers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /engineers/:id} : Updates an existing engineer.
     *
     * @param id the id of the engineerDTO to save.
     * @param engineerDTO the engineerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engineerDTO,
     * or with status {@code 400 (Bad Request)} if the engineerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the engineerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/engineers/{id}")
    public ResponseEntity<EngineerDTO> updateEngineer(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EngineerDTO engineerDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Engineer : {}, {}", id, engineerDTO);
        if (engineerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engineerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!engineerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EngineerDTO result = engineerService.save(engineerDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, engineerDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /engineers/:id} : Partial updates given fields of an existing engineer, field will ignore if it is null
     *
     * @param id the id of the engineerDTO to save.
     * @param engineerDTO the engineerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engineerDTO,
     * or with status {@code 400 (Bad Request)} if the engineerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the engineerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the engineerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/engineers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EngineerDTO> partialUpdateEngineer(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EngineerDTO engineerDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Engineer partially : {}, {}", id, engineerDTO);
        if (engineerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engineerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!engineerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EngineerDTO> result = engineerService.partialUpdate(engineerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, engineerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /engineers} : get all the engineers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of engineers in body.
     */
    @GetMapping("/engineers")
    public ResponseEntity<List<EngineerDTO>> getAllEngineers(
        EngineerCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Engineers by criteria: {}", criteria);
        Page<EngineerDTO> page = engineerQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /engineers/count} : count all the engineers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/engineers/count")
    public ResponseEntity<Long> countEngineers(EngineerCriteria criteria) {
        log.debug("REST request to count Engineers by criteria: {}", criteria);
        return ResponseEntity.ok().body(engineerQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /engineers/:id} : get the "id" engineer.
     *
     * @param id the id of the engineerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the engineerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/engineers/{id}")
    public ResponseEntity<EngineerDTO> getEngineer(@PathVariable Long id) {
        log.debug("REST request to get Engineer : {}", id);
        Optional<EngineerDTO> engineerDTO = engineerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(engineerDTO);
    }

    /**
     * {@code DELETE  /engineers/:id} : delete the "id" engineer.
     *
     * @param id the id of the engineerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/engineers/{id}")
    public ResponseEntity<Void> deleteEngineer(@PathVariable Long id) {
        log.debug("REST request to delete Engineer : {}", id);
        engineerService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
