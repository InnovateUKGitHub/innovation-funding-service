package com.worth.ifs.organisation.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
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

        User user = newUser().build();
        OrganisationResource organisation = newOrganisationResource().build();

        assertTrue(rules.anyoneCanSeeOrganisationsNotYetConnectedToApplications(organisation, user));
    }

    @Test
    public void testAnyoneCanViewAnOrganisationThatIsNotYetLinkedToAnApplicationIncludingAnonymousUsers() {

        User user = newUser().build();
        OrganisationResource organisation = newOrganisationResource().build();

        assertTrue(rules.anyoneCanSeeOrganisationsNotYetConnectedToApplications(organisation, user));
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
    public void testUsersCanViewOrganisationsOnTheirOwnApplications() {

        User user = newUser().build();
        Application application = newApplication().build();
        ProcessRole processRole = newProcessRole().withUser(user).withApplication(application).build();

        OrganisationResource organisation = newOrganisationResource().withProcessRoles(singletonList(processRole.getId())).build();

        assertTrue(rules.usersCanViewOrganisationsOnTheirOwnApplications(organisation, user));
    }

}
