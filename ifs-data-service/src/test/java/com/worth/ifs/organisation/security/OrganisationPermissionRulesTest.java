package com.worth.ifs.organisation.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the logic within the individual OrganisationRules methods that secures basic Organisation details
 */
public class OrganisationPermissionRulesTest {

    @InjectMocks
    private OrganisationPermissionRules rules = new OrganisationPermissionRules();

    @Test
    public void testAnyoneCanViewAnOrganisationThatIsNotYetLinkedToAnApplication() {
        assertTrue(rules.anyoneCanSeeOrganisationsNotYetConnectedToApplications(newOrganisationResource().build(), null));
    }

    @Test
    public void testAnyoneCanViewAnOrganisationThatIsNotYetLinkedToAnApplicationIncludingAnonymousUsers() {
        assertTrue(rules.anyoneCanSeeOrganisationsNotYetConnectedToApplications(newOrganisationResource().build(), null));
    }

    @Test
    public void testMemberOfOrganisationCanViewOwnOrganisation() {

        User user = newUser().build();
        User anotherUser = newUser().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user, anotherUser)).build();

        assertTrue(rules.memberOfOrganisationCanViewOwnOrganisation(organisation, user));
    }

    @Test
    public void testMemberOfOrganisationCanViewOwnOrganisationButUserIsNotAMemberOfTheOrganisation() {

        User user = newUser().build();
        User anotherUser = newUser().build();
        User unrelatedUser = newUser().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user, anotherUser)).build();

        assertFalse(rules.memberOfOrganisationCanViewOwnOrganisation(organisation, unrelatedUser));
    }

    @Test
    public void testMemberOfOrganisationCanUpdateOwnOrganisation() {

        User user = newUser().build();
        User anotherUser = newUser().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user, anotherUser)).build();

        assertTrue(rules.memberOfOrganisationCanUpdateOwnOrganisation(organisation, user));
    }

    @Test
    public void testMemberOfOrganisationCanUpdateOwnOrganisationButUserIsNotAMemberOfTheOrganisation() {

        User user = newUser().build();
        User anotherUser = newUser().build();
        User unrelatedUser = newUser().build();

        OrganisationResource organisation = newOrganisationResource().withUsers(asList(user, anotherUser)).build();

        assertFalse(rules.memberOfOrganisationCanUpdateOwnOrganisation(organisation, unrelatedUser));
    }

    @Test
    public void testUsersCanViewOrganisationsOnTheirOwnApplications() {

        Organisation organisation = newOrganisation().withId(123L).build();
        User user = newUser().build();
        Application application = newApplication().build();
        ProcessRole processRole = newProcessRole().withUser(user).withApplication(application).withOrganisation(organisation).build();

        OrganisationResource organisationResource =
                newOrganisationResource().withId(organisation.getId()).withProcessRoles(singletonList(processRole.getId())).build();

        assertTrue(rules.usersCanViewOrganisationsOnTheirOwnApplications(organisationResource, user));
    }

    @Test
    public void testUsersCanViewOrganisationsOnTheirOwnApplicationsButUserIsNotOnAnyApplicationsWithThisOrganisation() {

        Organisation organisation = newOrganisation().withId(123L).build();
        User user = newUser().build();
        Application application = newApplication().build();
        newProcessRole().withUser(user).withApplication(application).withOrganisation(organisation).build();

        Organisation anotherOrganisation = newOrganisation().withId(456L).build();
        User anotherUser = newUser().build();
        Application anotherApplication = newApplication().build();
        ProcessRole anotherProcessRole = newProcessRole().withUser(anotherUser).withApplication(anotherApplication).withOrganisation(anotherOrganisation).build();

        OrganisationResource anotherOrganisationResource =
                newOrganisationResource().withId(anotherOrganisation.getId()).withProcessRoles(singletonList(anotherProcessRole.getId())).build();

        assertFalse(rules.usersCanViewOrganisationsOnTheirOwnApplications(anotherOrganisationResource, user));
    }

    @Test
    public void testAnyoneCanCreateOrganisations() {
        assertTrue(rules.anyoneCanCreateOrganisations(newOrganisationResource().build(), null));
    }

    @Test
    public void testAnyoneCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers() {
        assertTrue(rules.anyoneCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(newOrganisationResource().build(), null));
    }

    @Test
    public void testAnyoneCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsersButOrganisationAttachedToApplication() {
        OrganisationResource organisation = newOrganisationResource().withProcessRoles(singletonList(123L)).build();
        assertFalse(rules.anyoneCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(organisation, null));
    }

    @Test
    public void testAnyoneCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsersButOrganisationAttachedToUsers() {
        OrganisationResource organisation = newOrganisationResource().withUsers(singletonList(newUser().build())).build();
        assertFalse(rules.anyoneCanUpdateOrganisationsNotYetConnectedToApplicationsOrUsers(organisation, null));
    }

    @Test
    public void testAnyoneCanSeeOrganisationSearchResults() {
        assertTrue(rules.anyoneCanSeeOrganisationSearchResults(new OrganisationSearchResult(), null));
    }
}
