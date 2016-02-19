package com.worth.ifs.invite.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.user.mapper.OrganisationMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {
        OrganisationMapper.class
    }
)
public abstract class InviteOrganisationMapper extends BaseMapper<InviteOrganisation, InviteOrganisationResource, Long> {

    public Long mapInviteOrganisationToId(InviteOrganisation object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}