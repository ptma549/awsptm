package com.awsptm.service.mapper;

import com.awsptm.domain.Visit;
import com.awsptm.service.dto.VisitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Visit} and its DTO {@link VisitDTO}.
 */
@Mapper(componentModel = "spring", uses = { JobMapper.class })
public interface VisitMapper extends EntityMapper<VisitDTO, Visit> {
    @Mapping(target = "job", source = "job", qualifiedByName = "id")
    VisitDTO toDto(Visit s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VisitDTO toDtoId(Visit visit);
}
