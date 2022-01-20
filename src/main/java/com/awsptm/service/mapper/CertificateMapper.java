package com.awsptm.service.mapper;

import com.awsptm.domain.Certificate;
import com.awsptm.service.dto.CertificateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Certificate} and its DTO {@link CertificateDTO}.
 */
@Mapper(componentModel = "spring", uses = { VisitMapper.class })
public interface CertificateMapper extends EntityMapper<CertificateDTO, Certificate> {
    @Mapping(target = "visit", source = "visit", qualifiedByName = "id")
    CertificateDTO toDto(Certificate s);
}
