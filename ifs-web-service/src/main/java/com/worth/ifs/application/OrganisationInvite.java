package com.worth.ifs.application;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OrganisationInvite implements Serializable {

    @NotEmpty
    String organisationName;
    Long organisationId;
    @Valid
    LinkedList<InviteeForm> invites;

    public OrganisationInvite(String organisationName, Long organisationId) {

        this.organisationName = organisationName;
        this.organisationId = organisationId;
        this.invites = new LinkedList<>();
    }

    public OrganisationInvite() {
        organisationName = "";
        this.invites = new LinkedList<>();
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public List<InviteeForm> getInvites() {
        return invites;
    }

    public void setInvites(LinkedList<InviteeForm> invites) {
        this.invites = invites;
    }
}
