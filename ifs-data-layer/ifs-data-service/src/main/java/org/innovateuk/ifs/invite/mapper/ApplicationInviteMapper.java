package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

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
            @Mapping(source = "target.leadOrganisationId", target = "leadOrganisationId"),
            @Mapping(source = "target.leadApplicant.name", target = "leadApplicant"),
            @Mapping(source = "target.leadApplicant.email", target = "leadApplicantEmail"),
            @Mapping(source = "target.name", target = "applicationName"),
            @Mapping(source = "target.id", target = "application"),
            @Mapping(source = "inviteOrganisation.id", target = "inviteOrganisation"),
            @Mapping(source = "inviteOrganisation.organisationName", target = "inviteOrganisationName"),
            @Mapping(source = "inviteOrganisation.organisation.name", target = "inviteOrganisationNameConfirmed"),
            @Mapping(source = "user.name", target = "nameConfirmed"),
            @Mapping(source = "user.id", target = "user"),
            @Mapping(target = "leadOrganisation", ignore = true)
    })
    @Override
    public abstract ApplicationInviteResource mapToResource(ApplicationInvite domain);

    public abstract List<ApplicationInviteResource> mapToResource(List<ApplicationInvite> domain);

    @Mappings({
            @Mapping(source="application", target="target")
    })
    @Override
    public abstract ApplicationInvite mapToDomain(ApplicationInviteResource resource);

    public Long mapInviteToId(ApplicationInvite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
