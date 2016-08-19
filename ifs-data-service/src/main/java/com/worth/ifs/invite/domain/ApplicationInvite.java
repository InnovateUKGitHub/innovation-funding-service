package com.worth.ifs.invite.domain;


import com.worth.ifs.application.domain.Application;
import com.worth.ifs.invite.constant.InviteStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * An {@link Invite} for a person at an organisation to participate in an {@link Application}.
 */
@Entity
@DiscriminatorValue("APPLICATION")
public class ApplicationInvite extends Invite<Application, ApplicationInvite> {

    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private InviteOrganisation inviteOrganisation;

    public ApplicationInvite() {
        // no-arg constructor
    }

    public ApplicationInvite(String name, String email, Application application, InviteOrganisation inviteOrganisation,  String hash, InviteStatus status) {
        super(name, email, hash, status);
        this.application = application;
        this.inviteOrganisation = inviteOrganisation;
    }

    public InviteOrganisation getInviteOrganisation() {
        return inviteOrganisation;
    }

    public void setInviteOrganisation(InviteOrganisation inviteOrganisation) {
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
