package com.worth.ifs.user.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
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
    public void testCompAdminsCanViewEveryone() {
        allGlobalRoleUsers.forEach(user -> {
            allGlobalRoleUsers.forEach(otherUser -> {
                if (user.equals(compAdminUser())) {
                    assertTrue(rules.compAdminsCanViewEveryone(otherUser, user));
                } else {
                    assertFalse(rules.compAdminsCanViewEveryone(otherUser, user));
                }
            });
        });
    }

    @Test
    public void testProjectFinanceUserCanViewEveryone() {
        allGlobalRoleUsers.forEach(user -> {
            allGlobalRoleUsers.forEach(otherUser -> {
                if (user.equals(projectFinanceUser())) {
                    assertTrue(rules.projectFinanceUsersCanViewEveryone(otherUser, user));
                } else {
                    assertFalse(rules.projectFinanceUsersCanViewEveryone(otherUser, user));
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

        User application1Lead1 = newUser().build();
        User application1Lead2 = newUser().build();
        User application1Lead3AndApplication2Collaborator2 = newUser().build();
        User application1Collaborator1 = newUser().build();
        User application1Collaborator2 = newUser().build();

        List<ProcessRole> application1ConsortiumRoles = newProcessRole().withApplication(application1).
                withRole(leadRole, leadRole, collaboratorRole, collaboratorRole).
                withUser(application1Lead1, application1Lead2, application1Lead3AndApplication2Collaborator2,
                        application1Collaborator1, application1Collaborator2).
                build(5);

        Application application2 = newApplication().build();

        User application2Lead = newUser().build();
        User application2Collaborator1 = newUser().build();

        List<ProcessRole> application2ConsortiumRoles = newProcessRole().withApplication(application2).
                withRole(leadRole, collaboratorRole, collaboratorRole).
                withUser(application2Lead, application2Collaborator1, application1Lead3AndApplication2Collaborator2).build(3);

        List<User> application1Consortium = simpleMap(application1ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application1ConsortiumResources = simpleMap(application1Consortium, userResourceForUser());

        List<User> application2Consortium = simpleMap(application2ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application2ConsortiumResources = simpleMap(application2Consortium, userResourceForUser());

        combineLists(application1ConsortiumRoles, application2ConsortiumRoles).forEach(role -> {
            when(processRoleRepositoryMock.findOne(role.getId())).thenReturn(role);
        });

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

        User application1Lead = newUser().build();
        User application2Collaborator = newUser().build();
        User application3Lead = newUser().build();
        User assessorForApplications1And2 = newUser().build();

        ProcessRole application1LeadProcessRole = newProcessRole().withApplication(application1).withRole(leadRole).withUser(application1Lead).build();
        ProcessRole application2CollaboratorProcessRole = newProcessRole().withApplication(application2).withRole(collaboratorRole).withUser(application2Collaborator).build();
        ProcessRole application3LeadProcessRole = newProcessRole().withApplication(application3).withRole(leadRole).withUser(application3Lead).build();

        List<ProcessRole> assessorProcessRole = newProcessRole().withApplication(application1, application2).withRole(assessorRole, assessorRole).
                withUser(assessorForApplications1And2, assessorForApplications1And2).build(2);

        combineLists(assessorProcessRole, application1LeadProcessRole, application2CollaboratorProcessRole, application3LeadProcessRole).forEach(role -> {
            when(processRoleRepositoryMock.findOne(role.getId())).thenReturn(role);
        });

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
    public void testUsersCanViewTheirOwnAffiliations() {
        UserResource user = newUserResource().build();
        AffiliationResource affiliation = newAffiliationResource()
                .withUser(user.getId())
                .build();
        assertTrue(rules.usersCanViewTheirOwnAffiliations(affiliation, user));
    }

    @Test
    public void testUsersCanViewTheirOwnAffiliationsButAttemptingToUpdateAnotherUsersAffiliation() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        AffiliationResource affiliation = newAffiliationResource()
                .withUser(user.getId())
                .build();
        assertFalse(rules.usersCanViewTheirOwnAffiliations(affiliation, anotherUser));
    }

    @Test
    public void testUsersCanUpdateTheirAffiliations() {
        UserResource user = newUserResource().build();
        assertTrue(rules.usersCanUpdateTheirOwnAffiliations(user, user));
    }

    @Test
    public void testUsersCantUpdateOtherUsersContracts() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.usersCanUpdateTheirSignedContract(user, anotherUser));
    }

    @Test
    public void testUsersCanUpdateTheirContract() {
        UserResource user = newUserResource().build();
        assertTrue(rules.usersCanUpdateTheirSignedContract(user, user));
    }

    @Test
    public void testUsersCanUpdateTheirAffiliationsButAttemptingToUpdateAnotherUsersProfile() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.usersCanUpdateTheirOwnAffiliations(anotherUser, user));
    }

    @Test
    public void testUsersCanChangeTheirOwnPasswordsButAttemptingToUpdateAnotherUsersPassword() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.usersCanChangeTheirOwnPassword(user, anotherUser));
    }

    @Override
    protected UserPermissionRules supplyPermissionRulesUnderTest() {
        return new UserPermissionRules();
    }

    private Function<User, UserResource> userResourceForUser() {
        return user -> {
            List<Long> processRoleIds = simpleMap(user.getProcessRoles(), ProcessRole::getId);
            return newUserResource().withId(user.getId()).withProcessRoles(processRoleIds).build();
        };
    }
}