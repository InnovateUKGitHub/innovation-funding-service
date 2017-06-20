package org.innovateuk.ifs.invite.mapper;


import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
   componentModel = "spring",
   uses = {
          ProjectMapper.class,
          OrganisationMapper.class,
          UserMapper.class
   }
)
public abstract class InviteProjectMapper extends BaseMapper<ProjectInvite, InviteProjectResource, Long> {

    @Mappings({
            @Mapping(source = "target.application.competition.name", target = "competitionName"),
            @Mapping(source = "target.application.leadApplicant.name", target = "leadApplicant"),
            @Mapping(source = "target.application.leadApplicantProcessRole.organisationId", target = "leadOrganisationId"),
            @Mapping(source = "organisation.id", target = "organisation"),
            @Mapping(source = "organisation.name", target = "organisationName"),
            @Mapping(source = "target.id", target = "project"),
            @Mapping(source = "target.name", target = "projectName"),
            @Mapping(source = "user.name", target = "nameConfirmed"),
            @Mapping(source = "user.id", target = "user"),
            @Mapping(target = "leadOrganisation", ignore = true)
    })
    @Override
    public abstract InviteProjectResource mapToResource(ProjectInvite domain);

    public Long mapInviteToId(ProjectInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
