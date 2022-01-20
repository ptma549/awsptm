package com.awsptm.service;

import com.awsptm.domain.ClientOrganisation;
import com.awsptm.repository.ClientOrganisationRepository;
import com.awsptm.service.dto.ClientOrganisationDTO;
import com.awsptm.service.mapper.ClientOrganisationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ClientOrganisation}.
 */
@Service
@Transactional
public class ClientOrganisationService {

    private final Logger log = LoggerFactory.getLogger(ClientOrganisationService.class);

    private final ClientOrganisationRepository clientOrganisationRepository;

    private final ClientOrganisationMapper clientOrganisationMapper;

    public ClientOrganisationService(
        ClientOrganisationRepository clientOrganisationRepository,
        ClientOrganisationMapper clientOrganisationMapper
    ) {
        this.clientOrganisationRepository = clientOrganisationRepository;
        this.clientOrganisationMapper = clientOrganisationMapper;
    }

    /**
     * Save a clientOrganisation.
     *
     * @param clientOrganisationDTO the entity to save.
     * @return the persisted entity.
     */
    public ClientOrganisationDTO save(ClientOrganisationDTO clientOrganisationDTO) {
        log.debug("Request to save ClientOrganisation : {}", clientOrganisationDTO);
        ClientOrganisation clientOrganisation = clientOrganisationMapper.toEntity(clientOrganisationDTO);
        clientOrganisation = clientOrganisationRepository.save(clientOrganisation);
        return clientOrganisationMapper.toDto(clientOrganisation);
    }

    /**
     * Partially update a clientOrganisation.
     *
     * @param clientOrganisationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClientOrganisationDTO> partialUpdate(ClientOrganisationDTO clientOrganisationDTO) {
        log.debug("Request to partially update ClientOrganisation : {}", clientOrganisationDTO);

        return clientOrganisationRepository
            .findById(clientOrganisationDTO.getId())
            .map(existingClientOrganisation -> {
                clientOrganisationMapper.partialUpdate(existingClientOrganisation, clientOrganisationDTO);

                return existingClientOrganisation;
            })
            .map(clientOrganisationRepository::save)
            .map(clientOrganisationMapper::toDto);
    }

    /**
     * Get all the clientOrganisations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClientOrganisationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ClientOrganisations");
        return clientOrganisationRepository.findAll(pageable).map(clientOrganisationMapper::toDto);
    }

    /**
     * Get one clientOrganisation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClientOrganisationDTO> findOne(Long id) {
        log.debug("Request to get ClientOrganisation : {}", id);
        return clientOrganisationRepository.findById(id).map(clientOrganisationMapper::toDto);
    }

    /**
     * Delete the clientOrganisation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ClientOrganisation : {}", id);
        clientOrganisationRepository.deleteById(id);
    }
}
