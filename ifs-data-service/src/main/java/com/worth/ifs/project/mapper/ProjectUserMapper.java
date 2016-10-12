package com.worth.ifs.project.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.invite.domain.ProjectParticipantRole;
import com.worth.ifs.invite.mapper.InviteProjectMapper;
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
                InviteProjectMapper.class,
                UserMapper.class
        }
)
public abstract class ProjectUserMapper extends BaseMapper<ProjectUser, ProjectUserResource, Long> {

    @Mappings({
            @Mapping(source = "role.name", target = "roleName"),
            @Mapping(source = "user.name", target = "userName"),
            @Mapping(source = "user.email", target = "email"),
            @Mapping(source = "user.phoneNumber", target = "phoneNumber"),
            @Mapping(source = "process", target = "project")
    })
    @Override
    public abstract ProjectUserResource mapToResource(ProjectUser domain);

    @Override
    public abstract ProjectUser mapToDomain(ProjectUserResource resource);

    public Long mapProjectUserToId(ProjectUser object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Long map(ProjectParticipantRole role) {
        if (role == null) {
            return null;
        } else {
            return role.getId();
        }
    }

    public ProjectParticipantRole mapIdToRole(Long id) {
        if (id == null) {
            return null;
        } else {
            return ProjectParticipantRole.getById(id);
        }
    }
}