package com.worth.ifs.application.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OrganisationInviteForm implements Serializable {

    @NotEmpty
    String organisationName;
    Long organisationId;
    @Valid
    LinkedList<InviteeForm> invites;

    public OrganisationInviteForm(String organisationName, Long organisationId) {
        this.organisationName = organisationName;
        this.organisationId = organisationId;
        this.invites = new LinkedList<>();
    }

    public OrganisationInviteForm() {
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
