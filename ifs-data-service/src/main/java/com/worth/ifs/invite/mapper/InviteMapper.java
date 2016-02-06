package com.worth.ifs.invite.mapper;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.transactional.InviteService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    uses = {
        ApplicationMapper.class,
        InviteOrganisationMapper.class
    }
)
public abstract class InviteMapper {

    @Autowired
    private InviteService service;

    public abstract InviteResource mapInviteToResource(Invite object);

    public abstract Invite resourceToInvite(InviteResource resource);

    public Long mapInviteToId(Invite object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public Invite mapIdToInvite(Long id) {
        return service.findOne(id);
    }
}