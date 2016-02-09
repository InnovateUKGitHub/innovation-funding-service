package com.worth.ifs.invite.resource;

import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.domain.Invite;

/*
* InviteResource is a DTO which enables to application to transfer Invite entities.
* */

public class InviteResource {
    private String leadOrganisation;
    private String leadApplicant;
    private Long id;
    private String name;
    private String email;
    private Long application;
    private String competitionName;
    private String applicationName;
    private Long inviteOrganisation;
    private String hash;
    private InviteStatusConstants status;

    public InviteResource() {
    }


    public InviteResource(Long id, String name, String email, Long application, Long inviteOrganisation, String hash, InviteStatusConstants status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.application = application;
        this.inviteOrganisation = inviteOrganisation;
        this.hash = hash;
        this.status = status;
    }

    public InviteResource(String name, String email, Long application) {
        this.name = name;
        this.email = email;
        this.application = application;
    }

    public InviteResource(Invite i) {
        this.id = i.getId();
        this.name = i.getName();
        this.email = i.getEmail();
        this.application = i.getApplication().getId();
        this.applicationName = i.getApplication().getName();
        this.competitionName = i.getApplication().getCompetition().getName();
        this.leadOrganisation = i.getApplication().getLeadOrganisation().get().getName();
        this.leadApplicant = i.getApplication().getLeadApplicant().get().getName();
        this.inviteOrganisation = i.getInviteOrganisation().getId();
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

    public Long getApplication() {
        return application;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public Long getInviteOrganisation() {
        return inviteOrganisation;
    }

    public void setInviteOrganisation(Long inviteOrganisation) {
        this.inviteOrganisation = inviteOrganisation;
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

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public String getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(String leadApplicant) {
        this.leadApplicant = leadApplicant;
    }
}
