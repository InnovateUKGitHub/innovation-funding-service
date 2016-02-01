package com.worth.ifs.application.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OrganisationInviteForm implements Serializable, Cloneable {

    @NotEmpty
    String organisationName;
    Long organisationId;
    Long organisationInviteId;
    @Valid
    List<InviteeForm> invites;

    public OrganisationInviteForm(String organisationName, Long organisationId, Long organisationInviteId) {
        this.organisationName = organisationName;
        this.organisationId = organisationId;
        this.organisationInviteId = organisationInviteId;
        this.invites = new LinkedList<>();
    }

    public OrganisationInviteForm() {
        organisationName = "";
        this.invites = new LinkedList<>();
    }

    public OrganisationInviteForm clone(){
        return new OrganisationInviteForm(this.getOrganisationName(), this.organisationId, this.organisationInviteId);
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

    public void setInvites(List<InviteeForm> invites) {
        this.invites = invites;
    }

    public Long getOrganisationInviteId() {
        return organisationInviteId;
    }

    public void setOrganisationInviteId(Long organisationInviteId) {
        this.organisationInviteId = organisationInviteId;
    }
}
