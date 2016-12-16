package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
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
        ApplicationMapper.class,
        InviteOrganisationMapper.class,
        UserMapper.class
    }
)
public abstract class ApplicationInviteMapper extends BaseMapper<ApplicationInvite, ApplicationInviteResource, Long> {

    @Autowired
    OrganisationRepository organisationRepository;

    @Override
    public ApplicationInviteResource mapToResource(ApplicationInvite domain) {
        ApplicationInviteResource resource = new ApplicationInviteResource();

        Application application = domain.getTarget();

        Competition competition = application.getCompetition();
        resource.setCompetitionName(competition.getName());
        resource.setCompetitionId(competition.getId());

        ProcessRole leadRole = application.getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadRole.getOrganisation());
        resource.setLeadOrganisation(leadOrganisation.getName());

        User leadApplicant = application.getLeadApplicant();
        resource.setLeadApplicant(leadApplicant.getName());
        resource.setLeadApplicantEmail(leadApplicant.getEmail());

        resource.setApplicationName(application.getName());
        resource.setApplication(application.getId());

        InviteOrganisation inviteOrganisation = domain.getInviteOrganisation();
        resource.setInviteOrganisation(inviteOrganisation.getId());
        resource.setInviteOrganisationName(inviteOrganisation.getOrganisationName());
        resource.setInviteOrganisationNameConfirmed(inviteOrganisation.getOrganisation().getName());

        User user = domain.getUser();
        resource.setNameConfirmed(user.getName());
        resource.setUser(user.getId());

        return resource;
    };

    public Long mapInviteToId(ApplicationInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
