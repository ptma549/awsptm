package com.awsptm.service;

import com.awsptm.domain.Engineer;
import com.awsptm.repository.EngineerRepository;
import com.awsptm.service.dto.EngineerDTO;
import com.awsptm.service.mapper.EngineerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Engineer}.
 */
@Service
@Transactional
public class EngineerService {

    private final Logger log = LoggerFactory.getLogger(EngineerService.class);

    private final EngineerRepository engineerRepository;

    private final EngineerMapper engineerMapper;

    public EngineerService(EngineerRepository engineerRepository, EngineerMapper engineerMapper) {
        this.engineerRepository = engineerRepository;
        this.engineerMapper = engineerMapper;
    }

    /**
     * Save a engineer.
     *
     * @param engineerDTO the entity to save.
     * @return the persisted entity.
     */
    public EngineerDTO save(EngineerDTO engineerDTO) {
        log.debug("Request to save Engineer : {}", engineerDTO);
        Engineer engineer = engineerMapper.toEntity(engineerDTO);
        engineer = engineerRepository.save(engineer);
        return engineerMapper.toDto(engineer);
    }

    /**
     * Partially update a engineer.
     *
     * @param engineerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EngineerDTO> partialUpdate(EngineerDTO engineerDTO) {
        log.debug("Request to partially update Engineer : {}", engineerDTO);

        return engineerRepository
            .findById(engineerDTO.getId())
            .map(existingEngineer -> {
                engineerMapper.partialUpdate(existingEngineer, engineerDTO);

                return existingEngineer;
            })
            .map(engineerRepository::save)
            .map(engineerMapper::toDto);
    }

    /**
     * Get all the engineers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EngineerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Engineers");
        return engineerRepository.findAll(pageable).map(engineerMapper::toDto);
    }

    /**
     * Get one engineer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EngineerDTO> findOne(Long id) {
        log.debug("Request to get Engineer : {}", id);
        return engineerRepository.findById(id).map(engineerMapper::toDto);
    }

    /**
     * Delete the engineer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Engineer : {}", id);
        engineerRepository.deleteById(id);
    }
}
