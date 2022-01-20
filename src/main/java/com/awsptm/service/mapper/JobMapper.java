package com.awsptm.service.mapper;

import com.awsptm.domain.Job;
import com.awsptm.service.dto.JobDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Job} and its DTO {@link JobDTO}.
 */
@Mapper(componentModel = "spring", uses = { ClientUserMapper.class, EngineerMapper.class, AddressMapper.class })
public interface JobMapper extends EntityMapper<JobDTO, Job> {
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "id")
    @Mapping(target = "assignedTo", source = "assignedTo", qualifiedByName = "id")
    @Mapping(target = "address", source = "address", qualifiedByName = "id")
    JobDTO toDto(Job s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    JobDTO toDtoId(Job job);
}
