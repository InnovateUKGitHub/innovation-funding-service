package com.worth.ifs.invite.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.invite.constant.InviteStatusConstants;

import javax.persistence.*;

/*
* The Invite is used for saving invites into the database. Data about the Invitee and related Application and Organisation is saved through this entity.
* */

@Entity
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;

    @ManyToOne
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "inviteOrganisationId", referencedColumnName = "id")
    private InviteOrganisation inviteOrganisation;

    private String hash;

    @Enumerated(EnumType.STRING)
    private InviteStatusConstants status;

    public Invite() {

    }

    public Invite(String name, String email, Application application, InviteOrganisation inviteOrganisation, String hash, InviteStatusConstants status) {
        this.name = name;
        this.email = email;
        this.application = application;
        this.inviteOrganisation = inviteOrganisation;
        this.hash = hash;
        this.status = status;
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

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public InviteOrganisation getInviteOrganisation() {
        return inviteOrganisation;
    }

    public void setInviteOrganisation(InviteOrganisation inviteOrganisation) {
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
}
