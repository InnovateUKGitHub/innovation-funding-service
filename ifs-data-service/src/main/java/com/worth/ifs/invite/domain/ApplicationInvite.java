package com.worth.ifs.invite.domain;


import com.worth.ifs.application.domain.Application;
import com.worth.ifs.invite.constant.InviteStatusConstants;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
//@DiscriminatorValue("APPLICATION")
public class ApplicationInvite extends Invite<InviteOrganisation, Application> {

    @ManyToOne
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "inviteOrganisationId", referencedColumnName = "id")
    private InviteOrganisation inviteOrganisation;

    public ApplicationInvite() {
        // no-arg constructor
//        application = null;
//        inviteOrganisation = null;
    }

    public ApplicationInvite(String name, String email, Application application, InviteOrganisation inviteOrganisation, String hash, InviteStatusConstants status) {
        super(name, email, hash, status);
        this.application = application;
        this.inviteOrganisation = inviteOrganisation;
    }

    @Override
    public InviteOrganisation getOwner() {
        return inviteOrganisation;
    }

    @Override
    public void setOwner(InviteOrganisation inviteOrganisation) {
        this.inviteOrganisation = inviteOrganisation;
    }

    @Override
    public Application getTarget() {
        return application;
    }

    @Override
    public void setTarget(Application application) {
        this.application = application;
    }
}
