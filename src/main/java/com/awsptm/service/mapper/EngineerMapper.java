package com.awsptm.service.mapper;

import com.awsptm.domain.Engineer;
import com.awsptm.service.dto.EngineerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Engineer} and its DTO {@link EngineerDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface EngineerMapper extends EntityMapper<EngineerDTO, Engineer> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    EngineerDTO toDto(Engineer s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EngineerDTO toDtoId(Engineer engineer);
}
