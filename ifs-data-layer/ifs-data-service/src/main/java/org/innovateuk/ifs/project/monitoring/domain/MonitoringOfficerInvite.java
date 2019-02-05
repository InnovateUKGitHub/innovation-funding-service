package org.innovateuk.ifs.project.monitoring.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Invite a user to be a system-wide monitoring officer.
 */
@Entity
@DiscriminatorValue("MONITORING_OFFICER")
public class MonitoringOfficerInvite extends Invite<Void, MonitoringOfficerInvite> {

    public MonitoringOfficerInvite() {
    }

    public MonitoringOfficerInvite(final String name, final String email, final String hash, final InviteStatus status) {
        super(name, email, hash, status);
    }

    @Override
    public Void getTarget() {
        return null;
    }

    @Override
    public void setTarget(Void target) {
    }
}