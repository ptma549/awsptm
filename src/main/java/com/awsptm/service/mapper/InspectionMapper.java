package com.awsptm.service.mapper;

import com.awsptm.domain.Inspection;
import com.awsptm.service.dto.InspectionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Inspection} and its DTO {@link InspectionDTO}.
 */
@Mapper(componentModel = "spring", uses = { JobMapper.class, ClientUserMapper.class, AddressMapper.class })
public interface InspectionMapper extends EntityMapper<InspectionDTO, Inspection> {
    @Mapping(target = "jobs", source = "jobs", qualifiedByName = "id")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "id")
    @Mapping(target = "address", source = "address", qualifiedByName = "id")
    InspectionDTO toDto(Inspection s);
}
