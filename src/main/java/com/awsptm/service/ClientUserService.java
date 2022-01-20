package com.awsptm.service;

import com.awsptm.domain.ClientUser;
import com.awsptm.repository.ClientUserRepository;
import com.awsptm.service.dto.ClientUserDTO;
import com.awsptm.service.mapper.ClientUserMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ClientUser}.
 */
@Service
@Transactional
public class ClientUserService {

    private final Logger log = LoggerFactory.getLogger(ClientUserService.class);

    private final ClientUserRepository clientUserRepository;

    private final ClientUserMapper clientUserMapper;

    public ClientUserService(ClientUserRepository clientUserRepository, ClientUserMapper clientUserMapper) {
        this.clientUserRepository = clientUserRepository;
        this.clientUserMapper = clientUserMapper;
    }

    /**
     * Save a clientUser.
     *
     * @param clientUserDTO the entity to save.
     * @return the persisted entity.
     */
    public ClientUserDTO save(ClientUserDTO clientUserDTO) {
        log.debug("Request to save ClientUser : {}", clientUserDTO);
        ClientUser clientUser = clientUserMapper.toEntity(clientUserDTO);
        clientUser = clientUserRepository.save(clientUser);
        return clientUserMapper.toDto(clientUser);
    }

    /**
     * Partially update a clientUser.
     *
     * @param clientUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClientUserDTO> partialUpdate(ClientUserDTO clientUserDTO) {
        log.debug("Request to partially update ClientUser : {}", clientUserDTO);

        return clientUserRepository
            .findById(clientUserDTO.getId())
            .map(existingClientUser -> {
                clientUserMapper.partialUpdate(existingClientUser, clientUserDTO);

                return existingClientUser;
            })
            .map(clientUserRepository::save)
            .map(clientUserMapper::toDto);
    }

    /**
     * Get all the clientUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClientUserDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ClientUsers");
        return clientUserRepository.findAll(pageable).map(clientUserMapper::toDto);
    }

    /**
     * Get one clientUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClientUserDTO> findOne(Long id) {
        log.debug("Request to get ClientUser : {}", id);
        return clientUserRepository.findById(id).map(clientUserMapper::toDto);
    }

    /**
     * Delete the clientUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ClientUser : {}", id);
        clientUserRepository.deleteById(id);
    }
}
