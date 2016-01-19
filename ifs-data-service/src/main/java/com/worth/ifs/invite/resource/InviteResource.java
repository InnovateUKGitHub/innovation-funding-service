package com.worth.ifs.invite.resource;

import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.constant.InviteStatusConstants;

/*
* InviteResource is a DTO which enables to application to transfer Invite entities.
* */

public class InviteResource {
    private Long id;
    private String name;
    private String email;
    private Long applicationId;
    private Long inviteOrganisationId;
    private String hash;
    private InviteStatusConstants status;

    public InviteResource() {}


    public InviteResource(Long id, String name, String email, Long applicationId, Long inviteOrganisationId, String hash, InviteStatusConstants status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.applicationId = applicationId;
        this.inviteOrganisationId = inviteOrganisationId;
        this.hash = hash;
        this.status = status;
    }

    public InviteResource(String name, String email, Long applicationId) {
        this.name = name;
        this.email = email;
        this.applicationId = applicationId;
    }

    public InviteResource(Invite i) {
        this.name = i.getName();
        this.email = i.getEmail();
        this.applicationId = i.getApplication().getId();
        this.inviteOrganisationId = i.getInviteOrganisation().getId();
        this.hash = i.getHash();
        this.status = i.getStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getInviteOrganisationId() {
        return inviteOrganisationId;
    }

    public void setInviteOrganisationId(Long inviteOrganisationId) {
        this.inviteOrganisationId = inviteOrganisationId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public InviteStatusConstants getStatus() {
        return status;
    }

    public void setStatus(InviteStatusConstants status) {
        this.status = status;
    }
}
