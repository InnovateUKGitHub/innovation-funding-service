package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    componentModel = "spring",
    config = GlobalMapperConfig.class,
    uses = {
        OrganisationMapper.class,
        ApplicationInviteMapper.class
    }
)
public abstract class InviteOrganisationMapper extends BaseMapper<InviteOrganisation, InviteOrganisationResource, Long> {

    @Mappings({
        @Mapping(source = "invites", target = "inviteResources"),
        @Mapping(source = "organisation.name", target = "organisationNameConfirmed"),
    })
    @Override
    public abstract InviteOrganisationResource mapToResource(InviteOrganisation domain);

    @Mappings({
        @Mapping(source = "inviteResources", target = "invites")
    })
    @Override
    public abstract InviteOrganisation mapToDomain(InviteOrganisationResource resource);

}
