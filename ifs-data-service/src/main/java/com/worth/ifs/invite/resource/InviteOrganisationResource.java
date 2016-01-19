package com.worth.ifs.invite.resource;

import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.user.domain.Organisation;

import java.util.List;
import java.util.stream.Collectors;

/*
* InviteOrganisationResource is a DTO which enables the application to transfer InviteOrganisation entities.
* */

public class InviteOrganisationResource {
    private Long id;
    private String organisationName;
    private Long organisationId;

    List<InviteResource> inviteResources;

    public InviteOrganisationResource() {

    }

    public InviteOrganisationResource(Long id, String organisationName, Organisation organisation, List<InviteResource> inviteResources) {
        this.id = id;
        this.organisationName = organisationName;
        this.organisationId = organisation.getId();
        this.inviteResources = inviteResources;
    }

    public InviteOrganisationResource(InviteOrganisation invite) {
        this.id = invite.getId();
        this.organisationName = invite.getOrganisationName();
        if(invite.getOrganisation() != null && invite.getOrganisation().getId() != null){
            this.organisationId = invite.getOrganisation().getId();
        }
        this.setInviteResources(invite.getInvites().stream().map(i -> new InviteResource(i)).collect(Collectors.toList()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public List<InviteResource> getInviteResources() {
        return inviteResources;
    }

    public void setInviteResources(List<InviteResource> inviteResources) {
        this.inviteResources = inviteResources;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }
}
