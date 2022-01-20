package com.awsptm.service;

import com.awsptm.domain.AddressComponent;
import com.awsptm.repository.AddressComponentRepository;
import com.awsptm.service.dto.AddressComponentDTO;
import com.awsptm.service.mapper.AddressComponentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AddressComponent}.
 */
@Service
@Transactional
public class AddressComponentService {

    private final Logger log = LoggerFactory.getLogger(AddressComponentService.class);

    private final AddressComponentRepository addressComponentRepository;

    private final AddressComponentMapper addressComponentMapper;

    public AddressComponentService(AddressComponentRepository addressComponentRepository, AddressComponentMapper addressComponentMapper) {
        this.addressComponentRepository = addressComponentRepository;
        this.addressComponentMapper = addressComponentMapper;
    }

    /**
     * Save a addressComponent.
     *
     * @param addressComponentDTO the entity to save.
     * @return the persisted entity.
     */
    public AddressComponentDTO save(AddressComponentDTO addressComponentDTO) {
        log.debug("Request to save AddressComponent : {}", addressComponentDTO);
        AddressComponent addressComponent = addressComponentMapper.toEntity(addressComponentDTO);
        addressComponent = addressComponentRepository.save(addressComponent);
        return addressComponentMapper.toDto(addressComponent);
    }

    /**
     * Partially update a addressComponent.
     *
     * @param addressComponentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AddressComponentDTO> partialUpdate(AddressComponentDTO addressComponentDTO) {
        log.debug("Request to partially update AddressComponent : {}", addressComponentDTO);

        return addressComponentRepository
            .findById(addressComponentDTO.getId())
            .map(existingAddressComponent -> {
                addressComponentMapper.partialUpdate(existingAddressComponent, addressComponentDTO);

                return existingAddressComponent;
            })
            .map(addressComponentRepository::save)
            .map(addressComponentMapper::toDto);
    }

    /**
     * Get all the addressComponents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AddressComponentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AddressComponents");
        return addressComponentRepository.findAll(pageable).map(addressComponentMapper::toDto);
    }

    /**
     * Get one addressComponent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AddressComponentDTO> findOne(Long id) {
        log.debug("Request to get AddressComponent : {}", id);
        return addressComponentRepository.findById(id).map(addressComponentMapper::toDto);
    }

    /**
     * Delete the addressComponent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete AddressComponent : {}", id);
        addressComponentRepository.deleteById(id);
    }
}
