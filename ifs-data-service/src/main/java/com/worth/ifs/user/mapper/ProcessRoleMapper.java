package com.worth.ifs.user.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        UserMapper.class,
        ApplicationMapper.class,
        RoleMapper.class,
        OrganisationMapper.class
    }
)
public abstract class ProcessRoleMapper {

    @Autowired
    private ProcessRoleRepository repository;

    public abstract ProcessRoleResource mapProcessRoleToResource(ProcessRole object);

    public abstract ProcessRole resourceToProcessRole(ProcessRoleResource resource);

    public Long mapProcessRoleToId(ProcessRole object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public ProcessRole mapIdToProcessRole(Long id) {
        return repository.findOne(id);
    }

    public Long mapProcessRoleResourceToId(ProcessRoleResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}