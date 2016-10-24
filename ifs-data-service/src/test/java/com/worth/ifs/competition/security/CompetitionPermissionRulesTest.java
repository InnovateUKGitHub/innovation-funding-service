package com.worth.ifs.competition.security;

import com.google.common.collect.Lists;
import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.competition.resource.CompetitionStatus;
import com.worth.ifs.user.builder.RoleResourceBuilder;
import com.worth.ifs.user.builder.UserResourceBuilder;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the logic within the individual CompetitionPermissionRules methods that secures basic Competition details
 */
public class CompetitionPermissionRulesTest extends BasePermissionRulesTest<CompetitionPermissionRules> {

	@Override
	protected CompetitionPermissionRules supplyPermissionRulesUnderTest() {
		return new CompetitionPermissionRules();
	}
	
    @Test
    public void testExternalUsersCannotViewACompetitionInSetup() {
        //null user cannot see competition in setup.
        assertFalse(rules.externalUsersCannotViewCompetitionsInSetup(newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build(), null));
        //null user can see open competitions
        assertTrue(rules.externalUsersCannotViewCompetitionsInSetup(newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build(), null));
    }

    @Test
    public void testCompAdminCanViewCompetitionInSetup(){
        //Comp admin can see competition in setup
        assertTrue(rules.compAdminUserCanViewAllCompetitions(newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build(),
                UserResourceBuilder.newUserResource().withRolesGlobal(
                        Lists.newArrayList(RoleResourceBuilder.newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build()));
    }

    @Test
    public void testProjectFinanceUserCanViewCompetitionInSetup(){
        //Comp admin can see competition in setup
        assertTrue(rules.projectFinanceUserCanViewAllCompetitions(newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build(),
                UserResourceBuilder.newUserResource().withRolesGlobal(
                        Lists.newArrayList(RoleResourceBuilder.newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build())).build()));
    }

    @Test
    public void testProjectFinanceUserCanViewAllCompetitions() {

        allGlobalRoleUsers.forEach(user -> {
            if (user == projectFinanceUser()) {
                assertTrue(rules.projectFinanceUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void testCompAdminCanViewAllCompetitions() {

        allGlobalRoleUsers.forEach(user -> {
            if (user == compAdminUser()) {
                assertTrue(rules.compAdminUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            } else {
                assertFalse(rules.compAdminUserCanViewAllCompetitions(newCompetitionResource().build(), user));
            }
        });
    }

    @Test
    public void testProjectFinanceUserCanViewAllCompetitionSearchResults() {

        allGlobalRoleUsers.forEach(user -> {
            if (user == projectFinanceUser()) {
                assertTrue(rules.projectFinanceUserCanViewAllCompetitionSearchResults(newCompetitionSearchResultItem().build(), user));
            } else {
                assertFalse(rules.projectFinanceUserCanViewAllCompetitionSearchResults(newCompetitionSearchResultItem().build(), user));
            }
        });
    }

    @Test
    public void testCompAdminCanViewAllCompetitionSearchResults() {

        allGlobalRoleUsers.forEach(user -> {
            if (user == compAdminUser()) {
                assertTrue(rules.compAdminUserCanViewAllCompetitionSearchResults(newCompetitionSearchResultItem().build(), user));
            } else {
                assertFalse(rules.compAdminUserCanViewAllCompetitionSearchResults(newCompetitionSearchResultItem().build(), user));
            }
        });
    }
}
