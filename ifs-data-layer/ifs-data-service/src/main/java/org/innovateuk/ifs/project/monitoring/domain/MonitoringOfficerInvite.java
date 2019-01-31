package org.innovateuk.ifs.project.monitoring.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("MONITORING_OFFICER")
public class MonitoringOfficerInvite extends ProjectInvite<MonitoringOfficerInvite> {

    public MonitoringOfficerInvite() {
    }

    public MonitoringOfficerInvite(final String name, final String email, final String hash, final Project project, final InviteStatus status) {
        super(name, email, hash, project, status);
    }
}