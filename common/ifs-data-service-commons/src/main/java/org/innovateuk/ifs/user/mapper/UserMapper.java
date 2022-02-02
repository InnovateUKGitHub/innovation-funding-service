package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class UserMapper extends BaseMapper<User, UserResource, Long> {

    @Mappings({
            @Mapping(target = "affiliations", ignore = true),
            @Mapping(target = "roleProfileStatuses", ignore = true)
    })
    @Override
    public abstract User mapToDomain(UserResource resource);

    @Mappings({
            @Mapping(source = "createdBy.name", target = "createdBy"),
            @Mapping(source = "modifiedBy.name", target = "modifiedBy")})
    @Override
    public abstract UserResource mapToResource(User domain);

    public Long mapUserToId(User object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Role mapIdToRole(long role) {
        return Role.getById(role);
    }
}
