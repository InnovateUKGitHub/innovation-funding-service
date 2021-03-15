package org.innovateuk.ifs.project.monitoring.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.project.core.ProjectParticipantRole.MONITORING_OFFICER;

/**
 * A monitoring officer on a project.
 */
@Entity
@DiscriminatorValue("PROJECT_MONITORING_OFFICER")
public class MonitoringOfficer extends ProjectParticipant {

    public MonitoringOfficer() {
        super(null, MONITORING_OFFICER, null);
    }

    public MonitoringOfficer(User user, Project project) {
        super(user, MONITORING_OFFICER, project);
        this.project = project;
    }
    public MonitoringOfficer(User user, Project project, ProjectParticipantRole role) {
        super(user, role, project);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MonitoringOfficer that = (MonitoringOfficer) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .toHashCode();
    }
}