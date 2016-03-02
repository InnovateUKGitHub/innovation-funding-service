package com.worth.ifs.invite.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.user.mapper.OrganisationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    componentModel = "spring",
    config = GlobalMapperConfig.class,
    uses = {
        OrganisationMapper.class,
        InviteMapper.class
    }
)
public abstract class InviteOrganisationMapper extends BaseMapper<InviteOrganisation, InviteOrganisationResource, Long> {

    @Mappings({
        @Mapping(source = "invites", target = "inviteResources"),
        @Mapping(source = "organisation.name", target = "organisationNameConfirmed"),
    })
    public abstract InviteOrganisationResource mapToResource(InviteOrganisation domain);

    @Mappings({
        @Mapping(source = "inviteResources", target = "invites")
    })
    public abstract InviteOrganisation mapToDomain(InviteOrganisationResource resource);

}