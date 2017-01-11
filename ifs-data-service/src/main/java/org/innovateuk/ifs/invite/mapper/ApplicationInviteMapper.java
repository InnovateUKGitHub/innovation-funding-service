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

    @Mappings({
            @Mapping(source = "target.competition.name", target = "competitionName"),
            @Mapping(source = "target.competition.id", target = "competitionId"),
            @Mapping(source = "target.leadOrganisationId", target = "leadOrganisation"),
            @Mapping(source = "target.leadApplicant.name", target = "leadApplicant"),
            @Mapping(source = "target.leadApplicant.email", target = "leadApplicantEmail"),
            @Mapping(source = "target.name", target = "applicationName"),
            @Mapping(source = "target.id", target = "application"),
            @Mapping(source = "inviteOrganisation.id", target = "inviteOrganisation"),
            @Mapping(source = "inviteOrganisation.organisationName", target = "inviteOrganisationName"),
            @Mapping(source = "inviteOrganisation.organisation.name", target = "inviteOrganisationNameConfirmed"),
            @Mapping(source = "user.name", target = "nameConfirmed"),
            @Mapping(source = "user.id", target = "user"),
    })
    @Override
    public abstract ApplicationInviteResource mapToResource(ApplicationInvite domain);

    public Long mapInviteToId(ApplicationInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
