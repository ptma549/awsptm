package com.awsptm.service;

import com.awsptm.domain.GoogleAddress;
import com.awsptm.repository.GoogleAddressRepository;
import com.awsptm.service.dto.GoogleAddressDTO;
import com.awsptm.service.mapper.GoogleAddressMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link GoogleAddress}.
 */
@Service
@Transactional
public class GoogleAddressService {

    private final Logger log = LoggerFactory.getLogger(GoogleAddressService.class);

    private final GoogleAddressRepository googleAddressRepository;

    private final GoogleAddressMapper googleAddressMapper;

    public GoogleAddressService(GoogleAddressRepository googleAddressRepository, GoogleAddressMapper googleAddressMapper) {
        this.googleAddressRepository = googleAddressRepository;
        this.googleAddressMapper = googleAddressMapper;
    }

    /**
     * Save a googleAddress.
     *
     * @param googleAddressDTO the entity to save.
     * @return the persisted entity.
     */
    public GoogleAddressDTO save(GoogleAddressDTO googleAddressDTO) {
        log.debug("Request to save GoogleAddress : {}", googleAddressDTO);
        GoogleAddress googleAddress = googleAddressMapper.toEntity(googleAddressDTO);
        googleAddress = googleAddressRepository.save(googleAddress);
        return googleAddressMapper.toDto(googleAddress);
    }

    /**
     * Partially update a googleAddress.
     *
     * @param googleAddressDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GoogleAddressDTO> partialUpdate(GoogleAddressDTO googleAddressDTO) {
        log.debug("Request to partially update GoogleAddress : {}", googleAddressDTO);

        return googleAddressRepository
            .findById(googleAddressDTO.getId())
            .map(existingGoogleAddress -> {
                googleAddressMapper.partialUpdate(existingGoogleAddress, googleAddressDTO);

                return existingGoogleAddress;
            })
            .map(googleAddressRepository::save)
            .map(googleAddressMapper::toDto);
    }

    /**
     * Get all the googleAddresses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<GoogleAddressDTO> findAll(Pageable pageable) {
        log.debug("Request to get all GoogleAddresses");
        return googleAddressRepository.findAll(pageable).map(googleAddressMapper::toDto);
    }

    /**
     *  Get all the googleAddresses where Address is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GoogleAddressDTO> findAllWhereAddressIsNull() {
        log.debug("Request to get all googleAddresses where Address is null");
        return StreamSupport
            .stream(googleAddressRepository.findAll().spliterator(), false)
            .filter(googleAddress -> googleAddress.getAddress() == null)
            .map(googleAddressMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one googleAddress by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GoogleAddressDTO> findOne(Long id) {
        log.debug("Request to get GoogleAddress : {}", id);
        return googleAddressRepository.findById(id).map(googleAddressMapper::toDto);
    }

    /**
     * Delete the googleAddress by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GoogleAddress : {}", id);
        googleAddressRepository.deleteById(id);
    }
}
