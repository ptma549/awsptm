package com.awsptm.service.mapper;

import com.awsptm.domain.Address;
import com.awsptm.service.dto.AddressDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring", uses = { GoogleAddressMapper.class })
public interface AddressMapper extends EntityMapper<AddressDTO, Address> {
    @Mapping(target = "googleAddress", source = "googleAddress", qualifiedByName = "id")
    AddressDTO toDto(Address s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "googleAddress", source = "googleAddress")
    AddressDTO toDtoId(Address address);
}
