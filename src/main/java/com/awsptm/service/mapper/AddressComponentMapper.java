package com.awsptm.service.mapper;

import com.awsptm.domain.AddressComponent;
import com.awsptm.service.dto.AddressComponentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AddressComponent} and its DTO {@link AddressComponentDTO}.
 */
@Mapper(componentModel = "spring", uses = { GoogleAddressMapper.class, AddressTypeMapper.class })
public interface AddressComponentMapper extends EntityMapper<AddressComponentDTO, AddressComponent> {
    @Mapping(target = "address", source = "address", qualifiedByName = "id")
    @Mapping(target = "type", source = "type", qualifiedByName = "id")
    AddressComponentDTO toDto(AddressComponent s);
}
