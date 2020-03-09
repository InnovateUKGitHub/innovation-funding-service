package org.innovateuk.ifs.acc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;

/**
 * A monitoring officer on a project.
 */
@Entity
@DiscriminatorValue("ACC_MONITORING_OFFICER")
public class AccMonitoringOfficer extends ProjectParticipant {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projectId", referencedColumnName = "id")
    private Project project;

    public AccMonitoringOfficer() {
        super(null, ACC_MONITORING_OFFICER);
    }

    public AccMonitoringOfficer(User user, Project project) {
        super(user, ACC_MONITORING_OFFICER);
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

        AccMonitoringOfficer that = (AccMonitoringOfficer) o;

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