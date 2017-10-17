package org.innovateuk.ifs.invite.domain;


import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.constant.InviteStatus;

import javax.persistence.*;

/**
 * An {@link Invite} for a person at an organisation to participate in an {@link Application}.
 */
@Entity
@DiscriminatorValue("APPLICATION")
public class ApplicationInvite extends Invite<Application, ApplicationInvite> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application application;

    @ManyToOne(fetch = FetchType.EAGER)
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
