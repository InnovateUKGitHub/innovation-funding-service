package org.innovateuk.ifs.invite.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.invite.builder.ProjectInviteResourceBuilder.newInviteProjectResource;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectInvitePermissionRulesTest extends BasePermissionRulesTest<ProjectInvitePermissionRules> {


    private Project project;
    private Organisation organisationOne;
    private Organisation organisationTwo;
    private UserResource userOnProjectForOrganisationOne;
    private UserResource userOnProjectForOrganisationTwo;
    private UserResource userNotOnProject;
    private ProjectUser projectUserForUserOnOgranisationOne;
    private ProjectUser projectUserForUserOnOgranisationTwo;
    private InviteProjectResource inviteProjectResourceForOrganisationOne;
    private InviteProjectResource inviteProjectResourceForOrganisationTwo;
    private Role partnerRole;

    @Override
    protected ProjectInvitePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectInvitePermissionRules();
    }

    @Before
    public void setup() throws Exception {
        project = newProject().build();
        organisationOne = newOrganisation().build();
        organisationTwo = newOrganisation().build();
        userOnProjectForOrganisationOne = newUserResource().build();
        userOnProjectForOrganisationTwo = newUserResource().build();
        userNotOnProject = newUserResource().build();

        projectUserForUserOnOgranisationOne = newProjectUser()
                .withOrganisation(organisationOne)
                .withProject(project)
                .withUser(newUser().withId(userOnProjectForOrganisationOne.getId()).build())
                .build();
        projectUserForUserOnOgranisationTwo = newProjectUser()
                .withOrganisation(organisationTwo)
                .withProject(project)
                .withUser(newUser().withId(userOnProjectForOrganisationTwo.getId()).build())
                .build();
        inviteProjectResourceForOrganisationOne = newInviteProjectResource()
                .withProject(project.getId())
                .withOrganisation(organisationOne.getId())
                .build();
        inviteProjectResourceForOrganisationTwo = newInviteProjectResource()
                .withProject(project.getId())
                .withOrganisation(organisationTwo.getId())
                .build();

        partnerRole = getRole(PARTNER);

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), userOnProjectForOrganisationOne.getId(), PROJECT_PARTNER)).thenReturn(asList(projectUserForUserOnOgranisationOne));
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), userOnProjectForOrganisationTwo.getId(), PROJECT_PARTNER)).thenReturn(asList(projectUserForUserOnOgranisationTwo));
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), userNotOnProject.getId(), PROJECT_PARTNER)).thenReturn(emptyList());
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), userOnProjectForOrganisationOne.getId(), organisationOne.getId(), PROJECT_PARTNER)).thenReturn(projectUserForUserOnOgranisationOne);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), userOnProjectForOrganisationTwo.getId(), organisationTwo.getId(), PROJECT_PARTNER)).thenReturn(projectUserForUserOnOgranisationTwo);
    }

    @Test
    public void testPartnersOnProjectCanSaveInvite() {
        assertTrue(rules.partnersOnProjectCanSaveInvite(inviteProjectResourceForOrganisationOne, userOnProjectForOrganisationOne));
        assertTrue(rules.partnersOnProjectCanSaveInvite(inviteProjectResourceForOrganisationTwo, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanSaveInvite(inviteProjectResourceForOrganisationOne, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanSaveInvite(inviteProjectResourceForOrganisationOne, userNotOnProject));
    }

    @Test
    public void testPartnersOnProjectCanSendInvite() {
        assertTrue(rules.partnersOnProjectCanSendInvite(inviteProjectResourceForOrganisationOne, userOnProjectForOrganisationOne));
        assertTrue(rules.partnersOnProjectCanSendInvite(inviteProjectResourceForOrganisationTwo, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanSendInvite(inviteProjectResourceForOrganisationOne, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanSendInvite(inviteProjectResourceForOrganisationOne, userNotOnProject));

    }

    @Test
    public void testPartnersOnProjectCanViewInvite(){
        assertTrue(rules.partnersOnProjectCanViewInvite(inviteProjectResourceForOrganisationOne, userOnProjectForOrganisationOne));
        assertTrue(rules.partnersOnProjectCanViewInvite(inviteProjectResourceForOrganisationOne, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanViewInvite(inviteProjectResourceForOrganisationOne, userNotOnProject));
    }
}
