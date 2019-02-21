package org.innovateuk.ifs.project.monitor.domain;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.MONITORING_OFFICER;

/**
 * A monitoring officer on a project.
 */
@Entity
@Table(name = "project_user")
public class ProjectMonitoringOfficer extends ProjectParticipant {

    public ProjectMonitoringOfficer() {
    }

    public ProjectMonitoringOfficer(User user, Project project) {
        super(user, project, MONITORING_OFFICER);
    }
}