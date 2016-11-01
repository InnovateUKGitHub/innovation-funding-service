package com.worth.ifs.invite.mapper;


import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.user.mapper.UserMapper;
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
            @Mapping(source = "target.application.leadApplicantProcessRole.organisation.name", target = "leadOrganisation"),
            @Mapping(source = "organisation.id", target = "organisation"),
            @Mapping(source = "organisation.name", target = "organisationName"),
            @Mapping(source = "target.id", target = "project"),
            @Mapping(source = "target.name", target = "projectName"),
            @Mapping(source = "user.name", target = "nameConfirmed"),
            @Mapping(source = "user.id", target = "user"),
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
