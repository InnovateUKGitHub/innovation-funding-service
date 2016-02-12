package com.worth.ifs.invite.mapper;

import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.repository.InviteOrganisationRepository;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.user.mapper.OrganisationMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    uses = {
        OrganisationMapper.class
    }
)
public abstract class InviteOrganisationMapper {

    @Autowired
    private InviteOrganisationRepository repository;

    public abstract InviteOrganisationResource mapInviteOrganisationToResource(InviteOrganisation object);

    public abstract InviteOrganisation resourceToInviteOrganisation(InviteOrganisationResource resource);

    public Long mapInviteOrganisationToId(InviteOrganisation object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public InviteOrganisation mapIdToInviteOrganisation(Long id) {
        return repository.findOne(id);
    }
}