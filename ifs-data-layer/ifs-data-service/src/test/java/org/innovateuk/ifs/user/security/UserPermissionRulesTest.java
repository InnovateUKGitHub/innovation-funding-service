package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static freemarker.template.utility.Collections12.singletonList;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests around the permissions for UserService and related services
 */
public class UserPermissionRulesTest extends BasePermissionRulesTest<UserPermissionRules> {

    @Test
    public void testAnyoneCanViewThemselves() {
        allGlobalRoleUsers.forEach(user -> {
            allGlobalRoleUsers.forEach(otherUser -> {
                if (user.equals(otherUser)) {
                    assertTrue(rules.anyUserCanViewThemselves(otherUser, user));
                } else {
                    assertFalse(rules.anyUserCanViewThemselves(otherUser, user));
                }
            });
        });
    }

    @Test
    public void testInternalUsersCanViewEveryone() {
        allGlobalRoleUsers.forEach(user -> {
            allGlobalRoleUsers.forEach(otherUser -> {
                if (allInternalUsers.contains(user)) {
                    assertTrue(rules.internalUsersCanViewEveryone(otherUser, user));
                } else {
                    assertFalse(rules.internalUsersCanViewEveryone(otherUser, user));
                }
            });
        });
    }

    @Test
    public void testSystemRegistrationUserCanViewEveryone() {
        allGlobalRoleUsers.forEach(user -> {
            allGlobalRoleUsers.forEach(otherUser -> {
                if (user.equals(systemRegistrationUser())) {
                    assertTrue(rules.systemRegistrationUserCanViewEveryone(otherUser, user));
                } else {
                    assertFalse(rules.systemRegistrationUserCanViewEveryone(otherUser, user));
                }
            });
        });
    }

    @Test
    public void testSystemRegistrationUserCanCreateUsers() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanCreateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanCreateUsers(newUserResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserCanCreateUsers_UserRegistrationResource() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanCreateUsers(newUserRegistrationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanCreateUsers(newUserRegistrationResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserCanActivateUsers() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanActivateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanActivateUsers(newUserResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserChangeUsersPasswords() {
        allGlobalRoleUsers.forEach(user -> {
            allGlobalRoleUsers.forEach(otherUser -> {
                if (user.equals(systemRegistrationUser())) {
                    assertTrue(rules.systemRegistrationUserCanChangePasswordsForUsers(otherUser, user));
                } else {
                    assertFalse(rules.systemRegistrationUserCanChangePasswordsForUsers(otherUser, user));
                }
            });
        });
    }

    @Test
    public void testAnyoneCanChangeTheirOwnPassword() {
        allGlobalRoleUsers.forEach(user -> {
            allGlobalRoleUsers.forEach(otherUser -> {
                if (user.equals(otherUser)) {
                    assertTrue(rules.usersCanChangeTheirOwnPassword(otherUser, user));
                } else {
                    assertFalse(rules.usersCanChangeTheirOwnPassword(otherUser, user));
                }
            });
        });
    }

    @Test
    public void testConsortiumMembersCanViewOtherConsortiumMembers() {

        Role leadRole = newRole().withType(LEADAPPLICANT).build();
        Role collaboratorRole = newRole().withType(COLLABORATOR).build();

        Application application1 = newApplication().build();
        when(applicationRepositoryMock.findOne(application1.getId())).thenReturn(application1);

        User application1Lead1 = newUser().build();
        User application1Lead2 = newUser().build();
        User application1Lead3AndApplication2Collaborator2 = newUser().build();
        User application1Collaborator1 = newUser().build();
        User application1Collaborator2 = newUser().build();

        List<ProcessRole> application1ConsortiumRoles = newProcessRole().withApplication(application1).
                withRole(leadRole, leadRole, leadRole, collaboratorRole, collaboratorRole).
                withUser(application1Lead1, application1Lead2, application1Lead3AndApplication2Collaborator2,
                        application1Collaborator1, application1Collaborator2).
                build(5);

        List<User> application1Consortium = simpleMap(application1ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application1ConsortiumResources = simpleMap(application1Consortium, userResourceForUser());

        when(processRoleRepositoryMock.findByUserId(application1Lead1.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(0)));
        when(processRoleRepositoryMock.findByUserId(application1Lead2.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(1)));
        when(processRoleRepositoryMock.findByUserId(application1Collaborator1.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(3)));
        when(processRoleRepositoryMock.findByUserId(application1Collaborator2.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(4)));

        Application application2 = newApplication().build();
        when(applicationRepositoryMock.findOne(application2.getId())).thenReturn(application2);

        User application2Lead = newUser().build();
        User application2Collaborator1 = newUser().build();

        List<ProcessRole> application2ConsortiumRoles = newProcessRole().withApplication(application2).
                withRole(leadRole, collaboratorRole, collaboratorRole).
                withUser(application2Lead, application2Collaborator1, application1Lead3AndApplication2Collaborator2).
                build(3);

        List<User> application2Consortium = simpleMap(application2ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application2ConsortiumResources = simpleMap(application2Consortium, userResourceForUser());

        when(processRoleRepositoryMock.findByUserId(application2Lead.getId())).
                thenReturn(singletonList(application2ConsortiumRoles.get(0)));
        when(processRoleRepositoryMock.findByUserId(application2Collaborator1.getId())).
                thenReturn(singletonList(application2ConsortiumRoles.get(1)));

        // user common to both applications
        when(processRoleRepositoryMock.findByUserId(application1Lead3AndApplication2Collaborator2.getId())).
                thenReturn(asList(application1ConsortiumRoles.get(2), application2ConsortiumRoles.get(2)));

        // assert that all members of the application 1 consortium can see all other consortium members
        application1ConsortiumResources.forEach(user -> {
            application1ConsortiumResources.forEach(otherUser -> {
                assertTrue(rules.consortiumMembersCanViewOtherConsortiumMembers(otherUser, user));
            });
        });

        // assert that all members of the application 2 consortium can see all other consortium members
        application2ConsortiumResources.forEach(user -> {
            application2ConsortiumResources.forEach(otherUser -> {
                assertTrue(rules.consortiumMembersCanViewOtherConsortiumMembers(otherUser, user));
            });
        });

        // assert that only the user with crossover between the 2 applications is able to see members of the other
        // applications
        application1ConsortiumResources.forEach(consortium1User -> {
            application2ConsortiumResources.forEach(consortium2User -> {
                if (asList(consortium1User.getId(), consortium2User.getId()).contains(application1Lead3AndApplication2Collaborator2.getId())) {
                    assertTrue(rules.consortiumMembersCanViewOtherConsortiumMembers(consortium2User, consortium1User));
                    assertTrue(rules.consortiumMembersCanViewOtherConsortiumMembers(consortium1User, consortium2User));
                } else {
                    assertFalse(rules.consortiumMembersCanViewOtherConsortiumMembers(consortium2User, consortium1User));
                    assertFalse(rules.consortiumMembersCanViewOtherConsortiumMembers(consortium1User, consortium2User));
                }
            });
        });
    }

    @Test
    public void testConsortiumMembersCanViewOtherConsortiumMembersButNotAssessors() {

        Role leadRole = newRole().withType(LEADAPPLICANT).build();
        Role collaboratorRole = newRole().withType(COLLABORATOR).build();
        Role assessorRole = newRole().withType(ASSESSOR).build();

        Application application1 = newApplication().build();

        User applicationLead = newUser().build();
        User applicationCollaborator = newUser().build();
        User applicationAssessor = newUser().build();

        List<ProcessRole> applicationConsortiumRoles = newProcessRole().withApplication(application1).
                withRole(leadRole, collaboratorRole).
                withUser(applicationLead, applicationCollaborator).
                build(2);

        ProcessRole assessorProcessRole = newProcessRole().withApplication(application1).withRole(assessorRole).
                withUser(applicationAssessor).build();

        List<User> applicationConsortium = simpleMap(applicationConsortiumRoles, ProcessRole::getUser);
        List<UserResource> applicationConsortiumResources = simpleMap(applicationConsortium, userResourceForUser());
        UserResource applicationAssessorResource = userResourceForUser().apply(applicationAssessor);

        combineLists(applicationConsortiumRoles, assessorProcessRole).forEach(role -> {
            when(processRoleRepositoryMock.findOne(role.getId())).thenReturn(role);
        });

        // assert that consortium members can't see the assessor using this rule
        applicationConsortiumResources.forEach(consortiumUser -> {
            assertFalse(rules.consortiumMembersCanViewOtherConsortiumMembers(applicationAssessorResource, consortiumUser));
        });
    }

    @Test
    public void testAssessorsCanViewConsortiumMembersForApplicationsTheyAreAssessing() {

        Role leadRole = newRole().withType(LEADAPPLICANT).build();
        Role collaboratorRole = newRole().withType(COLLABORATOR).build();
        Role assessorRole = newRole().withType(ASSESSOR).build();

        Application application1 = newApplication().build();
        Application application2 = newApplication().build();
        Application application3 = newApplication().build();

        when(applicationRepositoryMock.findOne(application1.getId())).thenReturn(application1);
        when(applicationRepositoryMock.findOne(application2.getId())).thenReturn(application2);
        when(applicationRepositoryMock.findOne(application3.getId())).thenReturn(application3);

        User application1Lead = newUser().build();
        User application2Collaborator = newUser().build();
        User application3Lead = newUser().build();
        User assessorForApplications1And2 = newUser().build();

        ProcessRole application1LeadProcessRole = newProcessRole().
                withApplication(application1).
                withRole(leadRole).
                withUser(application1Lead).
                build();
        ProcessRole application2CollaboratorProcessRole = newProcessRole().
                withApplication(application2).
                withRole(collaboratorRole).
                withUser(application2Collaborator).
                build();
        ProcessRole application3LeadProcessRole = newProcessRole().
                withApplication(application3).
                withRole(leadRole).
                withUser(application3Lead).
                build();

        List<ProcessRole> assessorProcessRoles = newProcessRole().
                withApplication(application1, application2).
                withRole(assessorRole, assessorRole).
                withUser(assessorForApplications1And2, assessorForApplications1And2).
                build(2);

        when(processRoleRepositoryMock.findByUserId(application1Lead.getId())).
                thenReturn(singletonList(application1LeadProcessRole));
        when(processRoleRepositoryMock.findByUserId(application2Collaborator.getId())).
                thenReturn(singletonList(application2CollaboratorProcessRole));
        when(processRoleRepositoryMock.findByUserId(application3Lead.getId())).
                thenReturn(singletonList(application3LeadProcessRole));
        when(processRoleRepositoryMock.findByUserId(assessorForApplications1And2.getId())).
                thenReturn(assessorProcessRoles);

        UserResource application1LeadResource = userResourceForUser().apply(application1Lead);
        UserResource application2CollaboratorResource = userResourceForUser().apply(application2Collaborator);
        UserResource application3LeadResource = userResourceForUser().apply(application3Lead);
        UserResource assessorForApplications1And2Resource = userResourceForUser().apply(assessorForApplications1And2);

        // assert that the assessor can see users from application1 and application2
        assertTrue(rules.assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(application1LeadResource, assessorForApplications1And2Resource));
        assertTrue(rules.assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(application2CollaboratorResource, assessorForApplications1And2Resource));

        // assert that they can't see users from application 3 because they are not assessing it
        assertFalse(rules.assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(application3LeadResource, assessorForApplications1And2Resource));
    }

    @Test
    public void testConsortiumMembersCanViewTheProcessRolesOtherConsortiumMembers() {

        Role leadRole = newRole().withType(LEADAPPLICANT).build();
        Role collaboratorRole = newRole().withType(COLLABORATOR).build();

        Application application1 = newApplication().build();
        when(applicationRepositoryMock.findOne(application1.getId())).thenReturn(application1);

        User application1Lead1 = newUser().build();
        User application1Lead2 = newUser().build();
        User application1Lead3AndApplication2Collaborator2 = newUser().build();
        User application1Collaborator1 = newUser().build();
        User application1Collaborator2 = newUser().build();

        List<ProcessRole> application1ConsortiumRoles = newProcessRole().withApplication(application1).
                withRole(leadRole, leadRole, leadRole, collaboratorRole, collaboratorRole).
                withUser(application1Lead1, application1Lead2, application1Lead3AndApplication2Collaborator2,
                        application1Collaborator1, application1Collaborator2).build(2);

        List<User> application1Consortium = simpleMap(application1ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application1ConsortiumResources = simpleMap(application1Consortium, userResourceForUser());

        when(processRoleRepositoryMock.findByUserId(application1Lead1.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(0)));
        when(processRoleRepositoryMock.findByUserId(application1Lead2.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(1)));

        ProcessRoleResource validResource = newProcessRoleResource().withApplication(application1.getId()).build();
        ProcessRoleResource invalidResource = newProcessRoleResource().withApplication(10L).build();

        assertTrue(rules.consortiumMembersCanViewTheProcessRolesOfOtherConsortiumMembers(validResource, application1ConsortiumResources.get(0)));
        assertFalse(rules.consortiumMembersCanViewTheProcessRolesOfOtherConsortiumMembers(invalidResource, application1ConsortiumResources.get(0)));
    }

    @Test
    public void assessorsCanViewTheProcessRolesOfConsortiumUsersOnApplicationsTheyAreAssessing() {

        Role assessorRole = newRole().withType(ASSESSOR).build();

        Application application1 = newApplication().build();
        when(applicationRepositoryMock.findOne(application1.getId())).thenReturn(application1);

        User application1Assessor1 = newUser().build();
        User application1Assessor2 = newUser().build();

        List<ProcessRole> application1ConsortiumRoles = newProcessRole().withApplication(application1).
                withRole(assessorRole, assessorRole, assessorRole).
                withUser(application1Assessor1, application1Assessor2).build(2);

        List<User> application1Consortium = simpleMap(application1ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application1ConsortiumResources = simpleMap(application1Consortium, userResourceForUser());

        when(processRoleRepositoryMock.findByUserId(application1Assessor1.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(0)));
        when(processRoleRepositoryMock.findByUserId(application1Assessor2.getId())).
                thenReturn(singletonList(application1ConsortiumRoles.get(1)));

        ProcessRoleResource validResource = newProcessRoleResource().withApplication(application1.getId()).build();
        ProcessRoleResource invalidResource = newProcessRoleResource().withApplication(10L).build();

        assertTrue(rules.assessorsCanViewTheProcessRolesOfConsortiumUsersOnApplicationsTheyAreAssessing(validResource, application1ConsortiumResources.get(0)));
        assertFalse(rules.assessorsCanViewTheProcessRolesOfConsortiumUsersOnApplicationsTheyAreAssessing(invalidResource, application1ConsortiumResources.get(0)));
    }

    @Test
    public void testUsersCanUpdateTheirOwnProfiles() {
        UserResource user = newUserResource().build();
        assertTrue(rules.usersCanUpdateTheirOwnProfiles(user, user));
    }

    @Test
    public void testUsersCanUpdateTheirOwnProfilesButAttemptingToUpdateAnotherUsersProfile() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.usersCanUpdateTheirOwnProfiles(user, anotherUser));
    }

    @Test
    public void testUsersCanChangeTheirOwnPasswords() {
        UserResource user = newUserResource().build();
        assertTrue(rules.usersCanChangeTheirOwnPassword(user, user));
    }

    @Test
    public void testUsersCanViewTheirOwnProfileSkills() {
        UserResource user = newUserResource().build();
        ProfileSkillsResource profileSkillsResource = newProfileSkillsResource()
                .withUser(user.getId())
                .build();
        assertTrue(rules.usersCanViewTheirOwnProfileSkills(profileSkillsResource, user));
    }

    @Test
    public void testUsersCanViewTheirOwnProfileSkillsButAttemptingToViewAnotherUsersProfileSkills() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        ProfileSkillsResource profileSkillsResource = newProfileSkillsResource()
                .withUser(user.getId())
                .build();
        assertFalse(rules.usersCanViewTheirOwnProfileSkills(profileSkillsResource, anotherUser));
    }

    @Test
    public void testUsersCanViewTheirOwnProfileAgreement() {
        UserResource user = newUserResource().build();
        ProfileAgreementResource profileAgreementResource = newProfileAgreementResource()
                .withUser(user.getId())
                .build();
        assertTrue(rules.usersCanViewTheirOwnProfileAgreement(profileAgreementResource, user));
    }

    @Test
    public void testUsersCanViewTheirOwnProfileAgreementButAttemptingToViewAnotherUsersProfileAgreement() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        ProfileAgreementResource profileAgreementResource = newProfileAgreementResource()
                .withUser(user.getId())
                .build();
        assertFalse(rules.usersCanViewTheirOwnProfileAgreement(profileAgreementResource, anotherUser));
    }

    @Test
    public void testUsersCanViewTheirOwnAffiliations() {
        UserResource user = newUserResource().build();
        AffiliationResource affiliation = newAffiliationResource()
                .withUser(user.getId())
                .build();
        assertTrue(rules.usersCanViewTheirOwnAffiliations(affiliation, user));
    }

    @Test
    public void testUsersCanViewTheirOwnAffiliationsButAttemptingToViewAnotherUsersAffiliation() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        AffiliationResource affiliation = newAffiliationResource()
                .withUser(user.getId())
                .build();
        assertFalse(rules.usersCanViewTheirOwnAffiliations(affiliation, anotherUser));
    }

    @Test
    public void testUsersCanViewTheirOwnDetails() {
        UserResource user = newUserResource().build();
        UserProfileResource userDetails = newUserProfileResource().withUser(user.getId()).build();
        assertTrue(rules.usersCanViewTheirOwnProfile(userDetails, user));
    }

    @Test
    public void testUsersCanViewTheirOwnDetailsButNotAnotherUsersDetails() {
        UserResource anotherUser = newUserResource().withId(1L).build();
        UserProfileResource userDetails = newUserProfileResource().withUser(2L).build();
        assertFalse(rules.usersCanViewTheirOwnProfile(userDetails, anotherUser));
    }

    @Test
    public void testUsersCanChangeTheirOwnPasswordsButAttemptingToUpdateAnotherUsersPassword() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.usersCanChangeTheirOwnPassword(user, anotherUser));
    }

    @Test
    public void testUsersCanViewTheirOwnProfileStatus() {
        UserResource user = newUserResource().build();
        UserProfileStatusResource userProfileStatus = newUserProfileStatusResource().withUser(user.getId()).build();
        assertTrue(rules.usersAndCompAdminCanViewProfileStatus(userProfileStatus, user));
    }

    @Test
    public void testUsersCanViewTheirOwnProfileStatusButNotAnotherUsersProfileStatus() {
        UserResource user = newUserResource().withId(1L).build();
        UserProfileStatusResource anotherUsersProfileStatus = newUserProfileStatusResource().withUser(2L).build();
        assertFalse(rules.usersAndCompAdminCanViewProfileStatus(anotherUsersProfileStatus, user));
    }

    @Test
    public void testCompAdminCanViewUserProfileStatus() {
        UserResource user = newUserResource().build();
        UserProfileStatusResource userProfileStatus = newUserProfileStatusResource().withUser(user.getId()).build();
        assertTrue(rules.usersAndCompAdminCanViewProfileStatus(userProfileStatus, compAdminUser()));
    }

    @Test
    public void testUsersCanViewTheirOwnProcessRole() {
        UserResource user = newUserResource().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(user).build();
        assertTrue(rules.usersCanViewTheirOwnProcessRole(processRoleResource, user));
    }

    @Test
    public void testUsersCanViewTheirOwnProcessRoleButNotAnotherUsersProcessRole() {
        UserResource user1 = newUserResource().withId(1L).build();
        UserResource user2 = newUserResource().withId(2L).build();

        ProcessRoleResource anotherUsersprocessRoleResource = newProcessRoleResource().withUser(user2).build();
        assertFalse(rules.usersAndInternalUsersCanViewProcessRole(anotherUsersprocessRoleResource, user1));
    }

    @Test
    public void testCompAdminCanViewUserProcessRole() {
        UserResource user = newUserResource().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(user).build();
        assertTrue(rules.usersAndInternalUsersCanViewProcessRole(processRoleResource, compAdminUser()));
    }

    @Test
    public void testProjectFinanceCanViewUserProcessRole() {
        UserResource user = newUserResource().build();
        ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(user).build();
        assertTrue(rules.usersAndInternalUsersCanViewProcessRole(processRoleResource, projectFinanceUser()));
    }

    @Test
    public void testAllUsersWithProjectRolesCanAccessProcessRolesWithinConsortium(){
        final Long userId = 11L;
        final Long applicationId = 1L;


        Arrays.stream(ProjectParticipantRole.values())
                .forEach(roleType -> {

                    UserResource userResource = newUserResource().withId(userId).build();

                    ProjectUser projectUser = newProjectUser().withUser(newUser().withId(userId).build()).withRole(roleType).withProject(newProject().withApplication(newApplication().withId(applicationId).build()).build()).build();

                    when(projectUserRepositoryMock.findByUserId(userId)).thenReturn(Collections.singletonList(projectUser));

                    ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(userResource).withApplication(applicationId).build();

                    assertTrue(rules.projectPartnersCanViewTheProcessRolesWithinSameApplication(processRoleResource, userResource));

                });
    }

    @Test
    public void testAllUsersWithProjectRolesCanNotAccessProcessRolesWhenNotInConsortium(){
        final Long userId = 11L;
        final Long applicationId = 1L;

        Arrays.stream(ProjectParticipantRole.values())
                .forEach(roleType -> {

                    UserResource userResource = newUserResource().withId(userId).build();

                    ProjectUser projectUser = newProjectUser().withUser(newUser().withId(userId).build()).withRole(roleType).withProject(newProject().withApplication(newApplication().withId(applicationId).build()).build()).build();

                    when(projectUserRepositoryMock.findByUserId(userId)).thenReturn(Collections.singletonList(projectUser));

                    ProcessRoleResource processRoleResource = newProcessRoleResource().withUser(userResource).withApplication(123L).build();

                    assertFalse(rules.projectPartnersCanViewTheProcessRolesWithinSameApplication(processRoleResource, userResource));

                });
    }

    @Test
    public void testUserCanCheckTheyHaveApplicationForCompetition() {
        UserResource user = newUserResource().build();
        assertTrue(rules.userCanCheckTheyHaveApplicationForCompetition(user, user));
    }

    @Test
    public void testUserCanCheckTheyHaveApplicationForCompetitionButAttemptingToCheckAnotherUser() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.userCanCheckTheyHaveApplicationForCompetition(user, anotherUser));
    }

    @Test
    public void testIfsAdminCanViewAnyUsersProfile(){
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(ifsAdminUser())) {
                assertTrue(rules.ifsAdminCanViewAnyUsersProfile(newUserProfileResource().build(), user));
            } else {
                assertFalse(rules.ifsAdminCanViewAnyUsersProfile(newUserProfileResource().build(), user));
            }
        });
    }

    @Override
    protected UserPermissionRules supplyPermissionRulesUnderTest() {
        return new UserPermissionRules();
    }

    private Function<User, UserResource> userResourceForUser() {
        return user -> newUserResource().withId(user.getId()).build();
    }
}
