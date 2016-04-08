package com.worth.ifs.user.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.domain.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
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
        allRoleUsers.forEach(user -> {
            allRoleUsers.forEach(otherUser -> {
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
        allRoleUsers.forEach(user -> {
            allRoleUsers.forEach(otherUser -> {
                if (user.equals(compAdminUser())) {
                    assertTrue(rules.compAdminsCanViewEveryone(otherUser, user));
                } else {
                    assertFalse(rules.compAdminsCanViewEveryone(otherUser, user));
                }
            });
        });
    }

    @Test
    public void testSystemRegistrationUserCanViewEveryone() {
        allRoleUsers.forEach(user -> {
            allRoleUsers.forEach(otherUser -> {
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
        allRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanCreateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanCreateUsers(newUserResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserCanActivateUsers() {
        allRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanActivateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanActivateUsers(newUserResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserChangeUsersPasswords() {
        allRoleUsers.forEach(user -> {
            allRoleUsers.forEach(otherUser -> {
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
        allRoleUsers.forEach(user -> {
            allRoleUsers.forEach(otherUser -> {
                if (user.equals(otherUser)) {
                    assertTrue(rules.anyoneCanChangeTheirOwnPassword(otherUser, user));
                } else {
                    assertFalse(rules.anyoneCanChangeTheirOwnPassword(otherUser, user));
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
        List<UserResource> application1ConsortiumResources = simpleMap(application1Consortium, user -> {
            List<Long> processRoleIds = simpleMap(user.getProcessRoles(), ProcessRole::getId);
            return newUserResource().withId(user.getId()).withProcessRoles(processRoleIds).build();
        });

        List<User> application2Consortium = simpleMap(application2ConsortiumRoles, ProcessRole::getUser);
        List<UserResource> application2ConsortiumResources = simpleMap(application2Consortium, user -> {
            List<Long> processRoleIds = simpleMap(user.getProcessRoles(), ProcessRole::getId);
            return newUserResource().withId(user.getId()).withProcessRoles(processRoleIds).build();
        });

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
    public void testUsersCanUpdateTheirOwnProfiles() {
        UserResource user = newUserResource().build();
        assertTrue(rules.usersCanUpdateTheirOwnProfiles(user, user));
    }

    @Test
    public void testUsersCanUpdateTheirOwnProfilesButAttemptingTOUpdatenotherUsersProfile() {
        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        assertFalse(rules.usersCanUpdateTheirOwnProfiles(user, anotherUser));
    }

    @Override
    protected UserPermissionRules supplyPermissionRulesUnderTest() {
        return new UserPermissionRules();
    }
}