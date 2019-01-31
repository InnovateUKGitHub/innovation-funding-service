package org.innovateuk.ifs.project.monitoring.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

/**
 * A monitoring officer on a project.
 */
@Entity
@Table(name = "project_user")
public class MonitoringOfficer extends ProjectParticipant<MonitoringOfficerInvite, MonitoringOfficerRole> {

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role")
    private MonitoringOfficerRole role = MonitoringOfficerRole.MONITORING_OFFICER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id", referencedColumnName = "id")
    private MonitoringOfficerInvite invite;

    public MonitoringOfficer() {
    }

    public MonitoringOfficer(User user, Project project) {
        super(user, project);
    }

    @Override
    public MonitoringOfficerInvite getInvite() {
        return invite;
    }

    @Override
    public MonitoringOfficerRole getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MonitoringOfficer that = (MonitoringOfficer) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(role, that.role)
                .append(invite, that.invite)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(role)
                .append(invite)
                .toHashCode();
    }
}