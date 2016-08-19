package com.worth.ifs.invite.security;


import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static com.worth.ifs.invite.builder.ProjectInviteResourceBuilder.newInviteProjectResource;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectInvitePermissionRulesTest extends BasePermissionRulesTest<ProjectInvitePermissionRules> {


    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private Project project;
    private Organisation organisationOne;
    private Organisation organisationTwo;
    private User userOnProjectForOrganisationOne;
    private User userOnProjectForOrganisationTwo;
    private User userNotOnProject;
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
        userOnProjectForOrganisationOne = newUser().build();
        userOnProjectForOrganisationTwo = newUser().build();
        userNotOnProject = newUser().build();
        projectUserForUserOnOgranisationOne = newProjectUser().withOrganisation(organisationOne).withProject(project).withUser(userOnProjectForOrganisationOne).build();
        projectUserForUserOnOgranisationTwo = newProjectUser().withOrganisation(organisationTwo).withProject(project).withUser(userOnProjectForOrganisationTwo).build();
        inviteProjectResourceForOrganisationOne = newInviteProjectResource().withProject(project.getId()).withOrganisation(organisationOne.getId()).build();
        inviteProjectResourceForOrganisationTwo = newInviteProjectResource().withProject(project.getId()).withOrganisation(organisationTwo.getId()).build();
        partnerRole = getRole(PARTNER);

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), userOnProjectForOrganisationOne.getId(), PROJECT_PARTNER)).thenReturn(asList(projectUserForUserOnOgranisationOne));
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), userOnProjectForOrganisationTwo.getId(), PROJECT_PARTNER)).thenReturn(asList(projectUserForUserOnOgranisationTwo));
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), userNotOnProject.getId(), PROJECT_PARTNER)).thenReturn(emptyList());
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), userOnProjectForOrganisationOne.getId(), organisationOne.getId(), PROJECT_PARTNER)).thenReturn(projectUserForUserOnOgranisationOne);
        when(projectUserRepositoryMock.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(project.getId(), userOnProjectForOrganisationTwo.getId(), organisationTwo.getId(), PROJECT_PARTNER)).thenReturn(projectUserForUserOnOgranisationTwo);
    }

    @Test
    public void testPartnersOnProjectCanSaveInvite() {
        assertTrue(rules.partnersOnProjectCanSaveInvite(inviteProjectResourceForOrganisationOne, userMapper.mapToResource(userOnProjectForOrganisationOne)));
        assertTrue(rules.partnersOnProjectCanSaveInvite(inviteProjectResourceForOrganisationTwo, userMapper.mapToResource(userOnProjectForOrganisationTwo)));
        assertFalse(rules.partnersOnProjectCanSaveInvite(inviteProjectResourceForOrganisationOne, userMapper.mapToResource(userOnProjectForOrganisationTwo)));
        assertFalse(rules.partnersOnProjectCanSaveInvite(inviteProjectResourceForOrganisationOne, userMapper.mapToResource(userNotOnProject)));
    }

    @Test
    public void testPartnersOnProjectCanSendInvite() {
        assertTrue(rules.partnersOnProjectCanSendInvite(inviteProjectResourceForOrganisationOne, userMapper.mapToResource(userOnProjectForOrganisationOne)));
        assertTrue(rules.partnersOnProjectCanSendInvite(inviteProjectResourceForOrganisationTwo, userMapper.mapToResource(userOnProjectForOrganisationTwo)));
        assertFalse(rules.partnersOnProjectCanSendInvite(inviteProjectResourceForOrganisationOne, userMapper.mapToResource(userOnProjectForOrganisationTwo)));
        assertFalse(rules.partnersOnProjectCanSendInvite(inviteProjectResourceForOrganisationOne, userMapper.mapToResource(userNotOnProject)));

    }

    @Test
    public void testPartnersOnProjectCanViewInvite(){
        assertTrue(rules.partnersOnProjectCanViewInvite(inviteProjectResourceForOrganisationOne, userMapper.mapToResource(userOnProjectForOrganisationOne)));
        assertTrue(rules.partnersOnProjectCanViewInvite(inviteProjectResourceForOrganisationOne, userMapper.mapToResource(userOnProjectForOrganisationTwo)));
        assertFalse(rules.partnersOnProjectCanViewInvite(inviteProjectResourceForOrganisationOne, userMapper.mapToResource(userNotOnProject)));
    }

}
