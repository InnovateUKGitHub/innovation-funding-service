package org.innovateuk.ifs.project.core.builder;

import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

public class ProjectUserBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        User expectedUser = newUser().build();
        Project expectedProject = newProject().build();
        Organisation expectedOrganisation = newOrganisation().build();
        ProjectUserInvite expectedProjectUserInvite = newProjectUserInvite().build();
        ProjectParticipantRole expectedProjectParticipantRole = PROJECT_PARTNER;
        ParticipantStatus expectedParticipantStatus = ACCEPTED;

        ProjectUser projectMonitoringOfficer = newProjectUser()
                .withId(expectedId)
                .withUser(expectedUser)
                .withProject(expectedProject)
                .withOrganisation(expectedOrganisation)
                .withInvite(expectedProjectUserInvite)
                .withRole(expectedProjectParticipantRole)
                .withStatus(expectedParticipantStatus)
                .build();

        assertEquals(expectedId, projectMonitoringOfficer.getId());
        assertEquals(expectedUser, projectMonitoringOfficer.getUser());
        assertEquals(expectedProject, projectMonitoringOfficer.getProject());
        assertEquals(expectedOrganisation, projectMonitoringOfficer.getOrganisation());
        assertEquals(expectedProjectUserInvite, projectMonitoringOfficer.getInvite());
        assertEquals(expectedProjectParticipantRole, projectMonitoringOfficer.getRole());
        assertEquals(expectedParticipantStatus, projectMonitoringOfficer.getStatus());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {3L, 5L};
        User[] expectedUsers = newUser().buildArray(2, User.class);
        Project[] expectedProjects = newProject().buildArray(2, Project.class);
        Organisation[] expectedOrganisations = newOrganisation().buildArray(2, Organisation.class);
        ProjectUserInvite[] expectedProjectUserInvites = newProjectUserInvite().buildArray(2, ProjectUserInvite.class);
        ProjectParticipantRole[] expectedProjectParticipantRoles = { PROJECT_MANAGER, PROJECT_FINANCE_CONTACT };
        ParticipantStatus[] expectedParticipantStatuss = { PENDING, REJECTED};

        List<ProjectUser> projectMonitoringOfficers = newProjectUser()
                .withId(expectedIds)
                .withUser(expectedUsers)
                .withProject(expectedProjects)
                .withOrganisation(expectedOrganisations)
                .withInvite(expectedProjectUserInvites)
                .withRole(expectedProjectParticipantRoles)
                .withStatus(expectedParticipantStatuss)
                .build(2);

        for (int i = 0; i < 2; i++) {
            assertEquals(expectedIds[i], projectMonitoringOfficers.get(i).getId());
            assertEquals(expectedUsers[i], projectMonitoringOfficers.get(i).getUser());
            assertEquals(expectedProjects[i], projectMonitoringOfficers.get(i).getProject());
            assertEquals(expectedOrganisations[i], projectMonitoringOfficers.get(i).getOrganisation());
            assertEquals(expectedProjectUserInvites[i], projectMonitoringOfficers.get(i).getInvite());
            assertEquals(expectedProjectParticipantRoles[i], projectMonitoringOfficers.get(i).getRole());
            assertEquals(expectedParticipantStatuss[i], projectMonitoringOfficers.get(i).getStatus());
        }
    }
}