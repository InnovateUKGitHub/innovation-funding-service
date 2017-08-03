package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        UserMapper.class
    }
)
public abstract class RoleMapper  extends BaseMapper<Role, RoleResource, Long> {

    @Mappings({
            @Mapping(target = "users", ignore = true)
    })
    public abstract Role mapToDomain(RoleResource resource);

    public Long mapRoleToId(Role object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
