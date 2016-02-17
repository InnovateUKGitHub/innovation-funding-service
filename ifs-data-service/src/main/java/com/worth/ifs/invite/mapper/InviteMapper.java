package com.worth.ifs.invite.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.resource.InviteResource;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {
        ApplicationMapper.class,
        InviteOrganisationMapper.class
    }
)
public abstract class InviteMapper extends BaseMapper<Invite, InviteResource, Long> {

    public Long mapInviteToId(Invite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}