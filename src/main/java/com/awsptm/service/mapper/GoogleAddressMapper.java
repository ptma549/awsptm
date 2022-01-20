package com.awsptm.service.mapper;

import com.awsptm.domain.GoogleAddress;
import com.awsptm.service.dto.GoogleAddressDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GoogleAddress} and its DTO {@link GoogleAddressDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface GoogleAddressMapper extends EntityMapper<GoogleAddressDTO, GoogleAddress> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GoogleAddressDTO toDtoId(GoogleAddress googleAddress);
}
