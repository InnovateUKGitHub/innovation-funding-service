package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                ApplicationMapper.class
        }
)
public abstract class ApplicationKtaInviteMapper extends BaseMapper<ApplicationKtaInvite, ApplicationKtaInviteResource, Long> {

    @Mappings({
            @Mapping(source = "target.id", target = "application"),
            @Mapping(source = "target.competition.name", target = "competitionName"),
            @Mapping(source = "target.leadOrganisationId", target = "leadOrganisationId"),
            @Mapping(source = "target.leadApplicant.name", target = "leadApplicant"),
            @Mapping(source = "target.name", target = "applicationName"),
            @Mapping(target = "leadOrganisationName", ignore = true)
    })
    @Override
    public abstract ApplicationKtaInviteResource mapToResource(ApplicationKtaInvite domain);

    public abstract List<ApplicationKtaInviteResource> mapToResource(List<ApplicationKtaInvite> domain);

    @Mappings({
            @Mapping(source = "application", target = "target"),
            @Mapping(target = "user", ignore = true)
    })
    @Override
    public abstract ApplicationKtaInvite mapToDomain(ApplicationKtaInviteResource resource);
}
