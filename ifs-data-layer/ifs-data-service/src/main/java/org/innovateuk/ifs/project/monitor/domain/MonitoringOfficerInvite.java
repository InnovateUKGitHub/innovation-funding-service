package org.innovateuk.ifs.project.monitor.domain;

import org.innovateuk.ifs.competition.domain.ResendableInvite;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.ZonedDateTime;


/**
 * Invite a user to be a system-wide monitoring officer.
 */
@Entity
@DiscriminatorValue("MONITORING_OFFICER")
public class MonitoringOfficerInvite extends Invite<Void, MonitoringOfficerInvite> implements ResendableInvite<Void, MonitoringOfficerInvite> {

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

    @Override
    public MonitoringOfficerInvite sendOrResend(User sentBy, ZonedDateTime sentOn) {
        return doSend(sentBy, sentOn);
    }
}