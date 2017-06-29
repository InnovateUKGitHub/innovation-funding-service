package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        RoleMapper.class,
        UserMapper.class
    }
)
public abstract class ProcessRoleMapper extends BaseMapper<ProcessRole, ProcessRoleResource, Long> {

    @Mappings({
        @Mapping(source = "role.name", target = "roleName"),
        @Mapping(source = "user.name", target = "userName")
    })
    @Override
    public abstract ProcessRoleResource mapToResource(ProcessRole domain);

    public Long mapProcessRoleToId(ProcessRole object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
