package org.innovateuk.ifs.project.monitor.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.MONITORING_OFFICER;

/**
 * A monitoring officer on a project.
 */
@Entity
@DiscriminatorValue("PROJECT_MONITORING_OFFICER")
public class ProjectMonitoringOfficer extends ProjectParticipant {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", referencedColumnName = "id")
    private Project project;

    public ProjectMonitoringOfficer() {
        super(null, MONITORING_OFFICER);
    }

    public ProjectMonitoringOfficer(User user, Project project) {
        super(user, MONITORING_OFFICER);
        this.project = project;
    }

    @Override
    public Project getProcess() {
        return project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectMonitoringOfficer that = (ProjectMonitoringOfficer) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(project, that.project)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(project)
                .toHashCode();
    }
}