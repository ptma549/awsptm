package com.awsptm.service;

import com.awsptm.domain.AddressType;
import com.awsptm.repository.AddressTypeRepository;
import com.awsptm.service.dto.AddressTypeDTO;
import com.awsptm.service.mapper.AddressTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AddressType}.
 */
@Service
@Transactional
public class AddressTypeService {

    private final Logger log = LoggerFactory.getLogger(AddressTypeService.class);

    private final AddressTypeRepository addressTypeRepository;

    private final AddressTypeMapper addressTypeMapper;

    public AddressTypeService(AddressTypeRepository addressTypeRepository, AddressTypeMapper addressTypeMapper) {
        this.addressTypeRepository = addressTypeRepository;
        this.addressTypeMapper = addressTypeMapper;
    }

    /**
     * Save a addressType.
     *
     * @param addressTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public AddressTypeDTO save(AddressTypeDTO addressTypeDTO) {
        log.debug("Request to save AddressType : {}", addressTypeDTO);
        AddressType addressType = addressTypeMapper.toEntity(addressTypeDTO);
        addressType = addressTypeRepository.save(addressType);
        return addressTypeMapper.toDto(addressType);
    }

    /**
     * Partially update a addressType.
     *
     * @param addressTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AddressTypeDTO> partialUpdate(AddressTypeDTO addressTypeDTO) {
        log.debug("Request to partially update AddressType : {}", addressTypeDTO);

        return addressTypeRepository
            .findById(addressTypeDTO.getId())
            .map(existingAddressType -> {
                addressTypeMapper.partialUpdate(existingAddressType, addressTypeDTO);

                return existingAddressType;
            })
            .map(addressTypeRepository::save)
            .map(addressTypeMapper::toDto);
    }

    /**
     * Get all the addressTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AddressTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AddressTypes");
        return addressTypeRepository.findAll(pageable).map(addressTypeMapper::toDto);
    }

    /**
     * Get one addressType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AddressTypeDTO> findOne(Long id) {
        log.debug("Request to get AddressType : {}", id);
        return addressTypeRepository.findById(id).map(addressTypeMapper::toDto);
    }

    /**
     * Delete the addressType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete AddressType : {}", id);
        addressTypeRepository.deleteById(id);
    }
}
