package com.awsptm.service.mapper;

import com.awsptm.domain.AddressType;
import com.awsptm.service.dto.AddressTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AddressType} and its DTO {@link AddressTypeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AddressTypeMapper extends EntityMapper<AddressTypeDTO, AddressType> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressTypeDTO toDtoId(AddressType addressType);
}
