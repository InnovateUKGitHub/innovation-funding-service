package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.ProjectInviteBuilder.newProjectInvite;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

public class ProjectInviteBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedName = "name";
        String expectedEmail = "example@test.com";
        User expectedUser = newUser().build();
        String expectedHash = "68656c6c6f";
        InviteStatus expectedStatus = SENT;
        Project expectedProject = newProject().build();
        Organisation expectedOrganisation = newOrganisation().build();

        ProjectInvite applicationInvite = newProjectInvite()
                .withId(expectedId)
                .withName(expectedName)
                .withEmail(expectedEmail)
                .withUser(expectedUser)
                .withHash(expectedHash)
                .withStatus(expectedStatus)
                .withProject(expectedProject)
                .withOrganisation(expectedOrganisation)
                .build();

        assertEquals(expectedId, applicationInvite.getId());
        assertEquals(expectedName, applicationInvite.getName());
        assertEquals(expectedEmail, applicationInvite.getEmail());
        assertEquals(expectedUser, applicationInvite.getUser());
        assertEquals(expectedHash, applicationInvite.getHash());
        assertEquals(expectedStatus, applicationInvite.getStatus());
        assertEquals(expectedProject, applicationInvite.getTarget());
        assertEquals(expectedOrganisation, applicationInvite.getOrganisation());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {7L, 13L};
        String[] expectedNames = {"name1", "name2"};
        String[] expectedEmails = {"example1@test.com", "example2@test.com"};
        User[] expectedUsers = newUser().withId(5L, 11L).buildArray(2, User.class);
        InviteStatus[] expectedStatuses = { SENT, OPENED };
        String[] expectedHashes = { "68656c6c6f", "776f726c64" };
        Project[] expectedProjects = newProject().buildArray(2, Project.class);
        Organisation[] expectedOrganisations = newOrganisation().buildArray(2, Organisation.class);

        List<ProjectInvite> applicationInvites = newProjectInvite()
                .withId(expectedIds)
                .withName(expectedNames)
                .withEmail(expectedEmails)
                .withUser(expectedUsers)
                .withStatus(expectedStatuses)
                .withHash(expectedHashes)
                .withTarget(expectedProjects)
                .withOrganisation(expectedOrganisations)
                .build(2);

        ProjectInvite first = applicationInvites.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedStatuses[0], first.getStatus());
        assertEquals(expectedHashes[0], first.getHash());
        assertEquals(expectedProjects[0], first.getTarget());
        assertEquals(expectedOrganisations[0], first.getOrganisation());

        ProjectInvite second = applicationInvites.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedStatuses[1], second.getStatus());
        assertEquals(expectedHashes[1], second.getHash());
        assertEquals(expectedHashes[1], second.getHash());
        assertEquals(expectedProjects[1], second.getTarget());
        assertEquals(expectedOrganisations[1], second.getOrganisation());
    }
}