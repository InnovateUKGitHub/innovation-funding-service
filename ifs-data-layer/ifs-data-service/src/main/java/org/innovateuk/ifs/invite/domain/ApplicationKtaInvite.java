package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.constant.InviteStatus;

import javax.persistence.*;

@Entity
@DiscriminatorValue("KTA")
public class ApplicationKtaInvite extends Invite<Application, ApplicationKtaInvite> {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application application;

    public ApplicationKtaInvite() {
    }

    public ApplicationKtaInvite(String name, String email, Application application, String hash, InviteStatus status) {
        super(name, email, hash, status);
        this.application = application;
    }

    public ApplicationKtaInvite(Long id, String name, String email, Application application, String hash, InviteStatus status) {
        super(id, name, email, hash, status);
        this.application = application;
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
