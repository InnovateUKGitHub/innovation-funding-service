package com.worth.ifs.invite.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

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
            @Mapping(source = "target.leadOrganisation.name", target = "leadOrganisation"),
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