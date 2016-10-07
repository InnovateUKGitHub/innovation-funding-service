package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
            OrganisationMapper.class,
            RoleMapper.class,
            ProcessRoleMapper.class,
            EthnicityMapper.class,
            ProfileMapper.class
        }
)
public abstract class UserMapper extends BaseMapper<User, UserResource, Long> {

    @Mappings({
            @Mapping(target = "affiliations", ignore = true)
    })
    @Override
    public abstract User mapToDomain(UserResource resource);

    @Mappings({
            @Mapping(target = "password", ignore = true)
    })
    @Override
    public abstract UserResource mapToResource(User domain);

    public Long mapUserToId(User object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}