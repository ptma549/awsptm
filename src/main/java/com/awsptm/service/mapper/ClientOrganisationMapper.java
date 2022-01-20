package com.awsptm.service.mapper;

import com.awsptm.domain.ClientOrganisation;
import com.awsptm.service.dto.ClientOrganisationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClientOrganisation} and its DTO {@link ClientOrganisationDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ClientOrganisationMapper extends EntityMapper<ClientOrganisationDTO, ClientOrganisation> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientOrganisationDTO toDtoId(ClientOrganisation clientOrganisation);
}
