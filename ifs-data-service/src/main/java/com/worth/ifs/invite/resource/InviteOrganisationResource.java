package com.worth.ifs.invite.resource;

import com.worth.ifs.user.domain.Organisation;

import java.util.List;

public class InviteOrganisationResource {
    private Long id;
    private String organisationName;
    private Organisation organisation;

    List<InviteResource> inviteResources;

    public InviteOrganisationResource() {

    }

    public InviteOrganisationResource(Long id, String organisationName, Organisation organisation, List<InviteResource> inviteResources) {
        this.id = id;
        this.organisationName = organisationName;
        this.organisation = organisation;
        this.inviteResources = inviteResources;
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

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public List<InviteResource> getInviteResources() {
        return inviteResources;
    }

    public void setInviteResources(List<InviteResource> inviteResources) {
        this.inviteResources = inviteResources;
    }
}
