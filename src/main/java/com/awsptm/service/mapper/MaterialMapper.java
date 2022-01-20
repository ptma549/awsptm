package com.awsptm.service.mapper;

import com.awsptm.domain.Material;
import com.awsptm.service.dto.MaterialDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Material} and its DTO {@link MaterialDTO}.
 */
@Mapper(componentModel = "spring", uses = { VisitMapper.class })
public interface MaterialMapper extends EntityMapper<MaterialDTO, Material> {
    @Mapping(target = "visit", source = "visit", qualifiedByName = "id")
    MaterialDTO toDto(Material s);
}
