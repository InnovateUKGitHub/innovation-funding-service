package com.worth.ifs.project.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.mapper.RoleMapper;
import com.worth.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
        ProjectMapper.class,
        RoleMapper.class,
        OrganisationMapper.class,
        UserMapper.class
    }
)
public abstract class ProjectUserMapper extends BaseMapper<ProjectUser, ProjectUserResource, Long> {

    @Mappings({
        @Mapping(source = "role.name", target = "roleName"),
        @Mapping(source = "user.name", target = "userName"),
        @Mapping(source = "user.email", target = "email"),
        @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    })
    @Override
    public abstract ProjectUserResource mapToResource(ProjectUser domain);

    public Long mapProjectUserToId(ProjectUser object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}