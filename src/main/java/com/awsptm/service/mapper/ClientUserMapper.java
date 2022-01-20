package com.awsptm.service.mapper;

import com.awsptm.domain.ClientUser;
import com.awsptm.service.dto.ClientUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ClientUser} and its DTO {@link ClientUserDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, ClientOrganisationMapper.class })
public interface ClientUserMapper extends EntityMapper<ClientUserDTO, ClientUser> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    @Mapping(target = "client", source = "client", qualifiedByName = "id")
    ClientUserDTO toDto(ClientUser s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientUserDTO toDtoId(ClientUser clientUser);
}
