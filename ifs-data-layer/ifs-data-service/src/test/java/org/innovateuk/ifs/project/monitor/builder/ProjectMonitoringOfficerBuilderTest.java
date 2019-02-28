package org.innovateuk.ifs.project.monitor.builder;

import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.monitor.domain.ProjectMonitoringOfficer;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.monitor.builder.ProjectMonitoringOfficerBuilder.newProjectMonitoringOfficer;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

public class ProjectMonitoringOfficerBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        User expectedUser = newUser().build();
        Project expectedProject = newProject().build();
        ParticipantStatus expectedParticipantStatus = ACCEPTED;

        ProjectMonitoringOfficer projectMonitoringOfficer = newProjectMonitoringOfficer()
                .withId(expectedId)
                .withUser(expectedUser)
                .withProject(expectedProject)
                .withStatus(expectedParticipantStatus)
                .build();

        assertEquals(expectedId, projectMonitoringOfficer.getId());
        assertEquals(expectedUser, projectMonitoringOfficer.getUser());
        assertEquals(expectedProject, projectMonitoringOfficer.getProject());
        assertEquals(expectedParticipantStatus, projectMonitoringOfficer.getStatus());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {3L, 5L};
        User[] expectedUsers = newUser().buildArray(2, User.class);
        Project[] expectedProjects = newProject().buildArray(2, Project.class);
        ParticipantStatus[] expectedParticipantStatuses = { PENDING, REJECTED };

        List<ProjectMonitoringOfficer> projectMonitoringOfficers = newProjectMonitoringOfficer()
                .withId(expectedIds)
                .withUser(expectedUsers)
                .withProject(expectedProjects)
                .withStatus(expectedParticipantStatuses)
                .build(2);

        for (int i = 0; i < 2; i++) {
            assertEquals(expectedIds[i], projectMonitoringOfficers.get(i).getId());
            assertEquals(expectedUsers[i], projectMonitoringOfficers.get(i).getUser());
            assertEquals(expectedProjects[i], projectMonitoringOfficers.get(i).getProject());
            assertEquals(expectedParticipantStatuses[i], projectMonitoringOfficers.get(i).getStatus());
        }
    }
}