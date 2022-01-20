package com.awsptm.service;

import com.awsptm.domain.Inspection;
import com.awsptm.repository.InspectionRepository;
import com.awsptm.service.dto.InspectionDTO;
import com.awsptm.service.mapper.InspectionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Inspection}.
 */
@Service
@Transactional
public class InspectionService {

    private final Logger log = LoggerFactory.getLogger(InspectionService.class);

    private final InspectionRepository inspectionRepository;

    private final InspectionMapper inspectionMapper;

    public InspectionService(InspectionRepository inspectionRepository, InspectionMapper inspectionMapper) {
        this.inspectionRepository = inspectionRepository;
        this.inspectionMapper = inspectionMapper;
    }

    /**
     * Save a inspection.
     *
     * @param inspectionDTO the entity to save.
     * @return the persisted entity.
     */
    public InspectionDTO save(InspectionDTO inspectionDTO) {
        log.debug("Request to save Inspection : {}", inspectionDTO);
        Inspection inspection = inspectionMapper.toEntity(inspectionDTO);
        inspection = inspectionRepository.save(inspection);
        return inspectionMapper.toDto(inspection);
    }

    /**
     * Partially update a inspection.
     *
     * @param inspectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InspectionDTO> partialUpdate(InspectionDTO inspectionDTO) {
        log.debug("Request to partially update Inspection : {}", inspectionDTO);

        return inspectionRepository
            .findById(inspectionDTO.getId())
            .map(existingInspection -> {
                inspectionMapper.partialUpdate(existingInspection, inspectionDTO);

                return existingInspection;
            })
            .map(inspectionRepository::save)
            .map(inspectionMapper::toDto);
    }

    /**
     * Get all the inspections.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InspectionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Inspections");
        return inspectionRepository.findAll(pageable).map(inspectionMapper::toDto);
    }

    /**
     * Get one inspection by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InspectionDTO> findOne(Long id) {
        log.debug("Request to get Inspection : {}", id);
        return inspectionRepository.findById(id).map(inspectionMapper::toDto);
    }

    /**
     * Delete the inspection by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Inspection : {}", id);
        inspectionRepository.deleteById(id);
    }
}
