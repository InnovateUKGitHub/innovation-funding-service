package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
//import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
            RoleMapper.class,
            EthnicityMapper.class,
        }
)
public abstract class UserMapper extends BaseMapper<User, UserResource, Long> {

    @Mappings({
            @Mapping(target = "affiliations", ignore = true)
    })
    @Override
    public abstract User mapToDomain(UserResource resource);

    @Mappings({
            @Mapping(target = "password", ignore = true)    })
    @Override
    public abstract UserResource mapToResource(User domain);

    public Long mapUserToId(User object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
