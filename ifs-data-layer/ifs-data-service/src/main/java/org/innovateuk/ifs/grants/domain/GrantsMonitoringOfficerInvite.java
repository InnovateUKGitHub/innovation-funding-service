package org.innovateuk.ifs.grants.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Invite a user to be a system-wide monitoring officer.
 */
@Entity
@DiscriminatorValue("ACC_MONITORING_OFFICER")
public class GrantsMonitoringOfficerInvite extends GrantsInvite<GrantsMonitoringOfficerInvite> {

    public GrantsMonitoringOfficerInvite() {
    }

    public GrantsMonitoringOfficerInvite(final String name, final String email, final String hash, final Project project, final InviteStatus status) {
        super(name, email, hash, null, project, status);
    }
}