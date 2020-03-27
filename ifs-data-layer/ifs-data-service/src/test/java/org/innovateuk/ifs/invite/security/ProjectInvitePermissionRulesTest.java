package org.innovateuk.ifs.invite.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectInvitePermissionRulesTest extends BasePermissionRulesTest<ProjectInvitePermissionRules> {

    private UserResource userOnProjectForOrganisationOne;
    private UserResource userOnProjectForOrganisationTwo;
    private UserResource userNotOnProject;
    private ProjectUserInviteResource projectUserInviteResourceForOrganisationOne;
    private ProjectUserInviteResource projectUserInviteResourceForOrganisationTwo;

    @Mock
    private ProjectProcessRepository projectProcessRepository;

    @Override
    protected ProjectInvitePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectInvitePermissionRules();
    }

    @Before
    public void setup() {
        Project project = newProject().build();
        Organisation organisationOne = newOrganisation().build();
        Organisation organisationTwo = newOrganisation().build();
        userOnProjectForOrganisationOne = newUserResource().build();
        userOnProjectForOrganisationTwo = newUserResource().build();
        userNotOnProject = newUserResource().build();

        ProjectUser projectUserForUserOnOrganisationOne = newProjectUser()
                .withOrganisation(organisationOne)
                .withProject(project)
                .withUser(newUser().withId(userOnProjectForOrganisationOne.getId()).build())
                .build();
        ProjectUser projectUserForUserOnOrganisationTwo = newProjectUser()
                .withOrganisation(organisationTwo)
                .withProject(project)
                .withUser(newUser().withId(userOnProjectForOrganisationTwo.getId()).build())
                .build();
        projectUserInviteResourceForOrganisationOne = newProjectUserInviteResource()
                .withProject(project.getId())
                .withOrganisation(organisationOne.getId())
                .build();
        projectUserInviteResourceForOrganisationTwo = newProjectUserInviteResource()
                .withProject(project.getId())
                .withOrganisation(organisationTwo.getId())
                .build();
        ProjectProcess projectProcess = newProjectProcess()
                .withProject(project)
                .withActivityState(ProjectState.SETUP)
                .build();

        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), userOnProjectForOrganisationOne.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(singletonList(projectUserForUserOnOrganisationOne));
        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), userOnProjectForOrganisationTwo.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(singletonList(projectUserForUserOnOrganisationTwo));
        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(project.getId(), userNotOnProject.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(emptyList());
        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(project.getId(), userOnProjectForOrganisationOne.getId(), organisationOne.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(projectUserForUserOnOrganisationOne);
        when(projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(project.getId(), userOnProjectForOrganisationTwo.getId(), organisationTwo.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(projectUserForUserOnOrganisationTwo);
        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);
    }

    @Test
    public void partnersOnProjectCanSaveInvite() {
        assertTrue(rules.partnersOnProjectCanSaveInvite(projectUserInviteResourceForOrganisationOne, userOnProjectForOrganisationOne));
        assertTrue(rules.partnersOnProjectCanSaveInvite(projectUserInviteResourceForOrganisationTwo, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanSaveInvite(projectUserInviteResourceForOrganisationOne, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanSaveInvite(projectUserInviteResourceForOrganisationOne, userNotOnProject));
    }

    @Test
    public void partnersOnProjectCanSendInvite() {
        assertTrue(rules.partnersOnProjectCanSendInvite(projectUserInviteResourceForOrganisationOne, userOnProjectForOrganisationOne));
        assertTrue(rules.partnersOnProjectCanSendInvite(projectUserInviteResourceForOrganisationTwo, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanSendInvite(projectUserInviteResourceForOrganisationOne, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanSendInvite(projectUserInviteResourceForOrganisationOne, userNotOnProject));

    }

    @Test
    public void partnersOnProjectCanViewInvite(){
        assertTrue(rules.partnersOnProjectCanViewInvite(projectUserInviteResourceForOrganisationOne, userOnProjectForOrganisationOne));
        assertTrue(rules.partnersOnProjectCanViewInvite(projectUserInviteResourceForOrganisationOne, userOnProjectForOrganisationTwo));
        assertFalse(rules.partnersOnProjectCanViewInvite(projectUserInviteResourceForOrganisationOne, userNotOnProject));
    }
}