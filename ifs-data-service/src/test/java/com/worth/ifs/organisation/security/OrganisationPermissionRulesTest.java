package com.worth.ifs.organisation.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests the logic within the individual OrganisationRules methods that secures basic Organisation details
 */
public class OrganisationPermissionRulesTest extends BasePermissionRulesTest<OrganisationPermissionRules> {

    @Test
    public void testSystemRegistrationUserCanViewAnOrganisationThatIsNotYetLinkedToAnApplication() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanSeeOrganisationsNotYetConnectedToApplications(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanSeeOrganisationsNotYetConnectedToApplications(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void testCompAdminsCanViewAnyOrganisation() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(compAdminUser())) {
                assertTrue(rules.compAdminsCanSeeAllOrganisations(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.compAdminsCanSeeAllOrganisations(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void projectFinanceUserCanSeeAllOrganisations(){
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(projectFinanceUser())) {
                assertTrue(rules.projectFinanceUserCanSeeAllOrganisations(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.projectFinanceUserCanSeeAllOrganisations(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUsersCanViewAnyOrganisation() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanSeeAllOrganisations(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanSeeAllOrganisations(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void testMemberOfOrganisationCanViewOwnOrganisation() {

        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user.getId(), anotherUser.getId())).build();

        assertTrue(rules.memberOfOrganisationCanViewOwnOrganisation(organisation, user));
    }

    @Test
    public void testMemberOfOrganisationCanViewOwnOrganisationButUserIsNotAMemberOfTheOrganisation() {

        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        UserResource unrelatedUser = newUserResource().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user.getId(), anotherUser.getId())).build();

        assertFalse(rules.memberOfOrganisationCanViewOwnOrganisation(organisation, unrelatedUser));
    }

    @Test
    public void testMemberOfOrganisationCanUpdateOwnOrganisation() {

        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user.getId(), anotherUser.getId())).build();

        assertTrue(rules.memberOfOrganisationCanUpdateOwnOrganisation(organisation, user));
    }

    @Test
    public void testMemberOfOrganisationCanUpdateOwnOrganisationButUserIsNotAMemberOfTheOrganisation() {

        UserResource user = newUserResource().build();
        UserResource anotherUser = newUserResource().build();
        UserResource unrelatedUser = newUserResource().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user.getId(), anotherUser.getId())).build();

        assertFalse(rules.memberOfOrganisationCanUpdateOwnOrganisation(organisation, unrelatedUser));
    }

    @Test
    public void testUsersCanViewOrganisationsOnTheirOwnApplications() {

        Organisation organisation = newOrganisation().withId(123L).build();
        Application application = newApplication().build();
        ProcessRole processRole = newProcessRole().withApplication(application).withOrganisation(organisation).build();
        UserResource user = newUserResource().withProcessRoles(singletonList(processRole.getId())).build();

        when(processRoleRepositoryMock.findOne(processRole.getId())).thenReturn(processRole);

        OrganisationResource organisationResource =
                newOrganisationResource().withId(organisation.getId()).withProcessRoles(singletonList(processRole.getId())).build();

        assertTrue(rules.usersCanViewOrganisationsOnTheirOwnApplications(organisationResource, user));
    }

    @Test
    public void testUsersCanViewOrganisationsOnTheirOwnApplicationsButUserIsNotOnAnyApplicationsWithThisOrganisation() {

        UserResource user = newUserResource().build();

        Organisation anotherOrganisation = newOrganisation().withId(456L).build();
        User anotherUser = newUser().build();
        Application anotherApplication = newApplication().build();
        ProcessRole anotherProcessRole = newProcessRole().withUser(anotherUser).withApplication(anotherApplication).withOrganisation(anotherOrganisation).build();

        OrganisationResource anotherOrganisationResource =
                newOrganisationResource().withId(anotherOrganisation.getId()).withProcessRoles(singletonList(anotherProcessRole.getId())).build();

        assertFalse(rules.usersCanViewOrganisationsOnTheirOwnApplications(anotherOrganisationResource, user));
    }

    @Test
    public void testSystemRegistrationUserCanCreateOrganisations() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanCreateOrganisations(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanCreateOrganisations(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(newOrganisationResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(newOrganisationResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsersButOrganisationAttachedToApplication() {
        OrganisationResource organisation = newOrganisationResource().withProcessRoles(singletonList(123L)).build();
        allGlobalRoleUsers.forEach(user -> {
            assertFalse(rules.systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(organisation, user));
        });
    }

    @Test
    public void testSystemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsersButOrganisationAttachedToUsers() {
        OrganisationResource organisation = newOrganisationResource().withUsers(singletonList(123L)).build();
        allGlobalRoleUsers.forEach(user -> {
            assertFalse(rules.systemRegistrationUserCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(organisation, user));
        });
    }

    @Test
    public void testSystemRegistrationUserCanSeeOrganisationSearchResults() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanSeeOrganisationSearchResults(new OrganisationSearchResult(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanSeeOrganisationSearchResults(new OrganisationSearchResult(), user));
            }
        });
    }

    @Override
    protected OrganisationPermissionRules supplyPermissionRulesUnderTest() {
        return new OrganisationPermissionRules();
    }
}
