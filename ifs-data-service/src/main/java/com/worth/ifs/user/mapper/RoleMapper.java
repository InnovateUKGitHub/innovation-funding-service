package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.RoleResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        UserMapper.class
    }
)
public abstract class RoleMapper {

    @Autowired
    private RoleRepository repository;

    public abstract RoleResource mapRoleToResource(Role object);

    public abstract Role resourceToRole(RoleResource resource);

    public Long mapRoleToId(Role object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Role mapIdToRole(Long id) {
        return repository.findOne(id);
    }

    public Long mapRoleResourceToId(RoleResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}