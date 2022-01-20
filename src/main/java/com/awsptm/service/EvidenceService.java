package com.awsptm.service;

import com.awsptm.domain.Evidence;
import com.awsptm.repository.EvidenceRepository;
import com.awsptm.service.dto.EvidenceDTO;
import com.awsptm.service.mapper.EvidenceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Evidence}.
 */
@Service
@Transactional
public class EvidenceService {

    private final Logger log = LoggerFactory.getLogger(EvidenceService.class);

    private final EvidenceRepository evidenceRepository;

    private final EvidenceMapper evidenceMapper;

    public EvidenceService(EvidenceRepository evidenceRepository, EvidenceMapper evidenceMapper) {
        this.evidenceRepository = evidenceRepository;
        this.evidenceMapper = evidenceMapper;
    }

    /**
     * Save a evidence.
     *
     * @param evidenceDTO the entity to save.
     * @return the persisted entity.
     */
    public EvidenceDTO save(EvidenceDTO evidenceDTO) {
        log.debug("Request to save Evidence : {}", evidenceDTO);
        Evidence evidence = evidenceMapper.toEntity(evidenceDTO);
        evidence = evidenceRepository.save(evidence);
        return evidenceMapper.toDto(evidence);
    }

    /**
     * Partially update a evidence.
     *
     * @param evidenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EvidenceDTO> partialUpdate(EvidenceDTO evidenceDTO) {
        log.debug("Request to partially update Evidence : {}", evidenceDTO);

        return evidenceRepository
            .findById(evidenceDTO.getId())
            .map(existingEvidence -> {
                evidenceMapper.partialUpdate(existingEvidence, evidenceDTO);

                return existingEvidence;
            })
            .map(evidenceRepository::save)
            .map(evidenceMapper::toDto);
    }

    /**
     * Get all the evidences.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EvidenceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Evidences");
        return evidenceRepository.findAll(pageable).map(evidenceMapper::toDto);
    }

    /**
     * Get one evidence by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EvidenceDTO> findOne(Long id) {
        log.debug("Request to get Evidence : {}", id);
        return evidenceRepository.findById(id).map(evidenceMapper::toDto);
    }

    /**
     * Delete the evidence by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Evidence : {}", id);
        evidenceRepository.deleteById(id);
    }
}
