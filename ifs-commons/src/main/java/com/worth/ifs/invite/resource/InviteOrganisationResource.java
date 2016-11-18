package com.worth.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/*
* InviteOrganisationResource is a DTO which enables the application to transfer InviteOrganisation entities.
* */

public class InviteOrganisationResource {
    private Long id;
    private String organisationName;
    private String organisationNameConfirmed;
    private Long organisation;

    List<ApplicationInviteResource> inviteResources;

    public InviteOrganisationResource() {
    	// no-arg constructor
    }

    public InviteOrganisationResource(Long id, String organisationName, Long organisation, List<ApplicationInviteResource> inviteResources) {
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
    public List<ApplicationInviteResource> getInviteResources() {
        return inviteResources;
    }

    public void setInviteResources(List<ApplicationInviteResource> inviteResources) {
        this.inviteResources = inviteResources;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InviteOrganisationResource that = (InviteOrganisationResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(organisationName, that.organisationName)
                .append(organisation, that.organisation)
                .append(inviteResources, that.inviteResources)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisationName)
                .append(organisation)
                .append(inviteResources)
                .toHashCode();
    }

    public String getOrganisationNameConfirmed() {
        return organisationNameConfirmed;
    }

    public void setOrganisationNameConfirmed(String organisationNameConfirmed) {
        this.organisationNameConfirmed = organisationNameConfirmed;
    }
}
