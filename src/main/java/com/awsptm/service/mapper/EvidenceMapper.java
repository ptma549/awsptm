package com.awsptm.service.mapper;

import com.awsptm.domain.Evidence;
import com.awsptm.service.dto.EvidenceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Evidence} and its DTO {@link EvidenceDTO}.
 */
@Mapper(componentModel = "spring", uses = { VisitMapper.class })
public interface EvidenceMapper extends EntityMapper<EvidenceDTO, Evidence> {
    @Mapping(target = "visit", source = "visit", qualifiedByName = "id")
    EvidenceDTO toDto(Evidence s);
}
