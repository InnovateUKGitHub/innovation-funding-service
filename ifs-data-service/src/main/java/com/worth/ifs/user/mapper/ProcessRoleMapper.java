package com.worth.ifs.user.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.application.mapper.ResponseMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ApplicationMapper.class,
        RoleMapper.class,
        OrganisationMapper.class,
        UserMapper.class,
        ResponseMapper.class
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