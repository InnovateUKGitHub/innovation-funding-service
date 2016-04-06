package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.RoleResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProcessRoleMapper.class,
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