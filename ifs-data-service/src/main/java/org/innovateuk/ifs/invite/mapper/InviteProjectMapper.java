package org.innovateuk.ifs.invite.mapper;


import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
   componentModel = "spring",
   uses = {
          ProjectMapper.class,
          OrganisationMapper.class,
          UserMapper.class
   }
)
public abstract class InviteProjectMapper extends BaseMapper<ProjectInvite, InviteProjectResource, Long> {

    @Autowired
    OrganisationRepository organisationRepository;

    @Override
    public InviteProjectResource mapToResource(ProjectInvite domain) {
        InviteProjectResource resource = new InviteProjectResource();

        Project target = domain.getTarget();

        Application application = target.getApplication();

        resource.setCompetitionName(application.getCompetition().getName());
        resource.setLeadApplicant(application.getLeadApplicant().getName());

        ProcessRole leadRole = application.getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadRole.getOrganisation());
        resource.setLeadOrganisation(leadOrganisation.getName());

        Organisation organisation = domain.getOrganisation();
        resource.setOrganisation(organisation.getId());
        resource.setOrganisationName(organisation.getName());

        resource.setProject(target.getId());
        resource.setProjectName(target.getName());

        User user = domain.getUser();
        resource.setNameConfirmed(user.getName());
        resource.setUser(user.getId());

        return resource;

    };

    public Long mapInviteToId(ProjectInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
