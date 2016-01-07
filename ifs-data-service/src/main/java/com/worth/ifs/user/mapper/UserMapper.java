package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
        OrganisationMapper.class,
        RoleMapper.class
    }
)
public abstract class UserMapper {

    @Autowired
    private UserRepository repository;

    public abstract UserResource mapUserToResource(User object);

    public abstract User resourceToUser(UserResource resource);

    public Long mapUserToId(User object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public User mapIdToUser(Long id) {
        return repository.findOne(id);
    }

    public Long mapUserResourceToId(UserResource object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}