package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        UserMapper.class
    }
)
public abstract class RoleMapper  extends BaseMapper<Role, RoleResource, Long> {

    public Long mapRoleToId(Role object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
