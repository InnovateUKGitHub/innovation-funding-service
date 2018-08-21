package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeResourceBuilder.newCompetitionTypeResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class GrantClaimMaximumPermissionRulesTest extends BasePermissionRulesTest<GrantClaimMaximumPermissionRules> {

    private UserResource leadApplicant;
    private UserResource collaborator;
    private UserResource compAdmin;
    private UserResource applicant;
    private UserResource assessor;

    @Override
    protected GrantClaimMaximumPermissionRules supplyPermissionRulesUnderTest() {
        return new GrantClaimMaximumPermissionRules();
    }

    @Before
    public void setup() {
        leadApplicant = newUserResource().withRoleGlobal(LEADAPPLICANT).build();
        applicant = newUserResource().withRoleGlobal(APPLICANT).build();
        collaborator = newUserResource().withRoleGlobal(COLLABORATOR).build();
        compAdmin = newUserResource().withRoleGlobal(COMP_ADMIN).build();
        assessor = newUserResource().withRoleGlobal(ASSESSOR).build();
    }

    @Test
    public void testUserCanSeeGrantClaimMaximumsForCompetitionType() {
        CompetitionTypeResource competitionType = newCompetitionTypeResource().build();

        assertTrue(rules.userCanSeeGrantClaimMaximumsForCompetitionType(competitionType, leadApplicant));
        assertTrue(rules.userCanSeeGrantClaimMaximumsForCompetitionType(competitionType, applicant));
        assertTrue(rules.userCanSeeGrantClaimMaximumsForCompetitionType(competitionType, collaborator));
        assertTrue(rules.userCanSeeGrantClaimMaximumsForCompetitionType(competitionType, compAdmin));

        assertFalse(rules.userCanSeeGrantClaimMaximumsForCompetitionType(competitionType, assessor));
    }

    @Test
    public void testUserCanSeeGrantClaimMaximumsForCompetition() {
        CompetitionResource competition = newCompetitionResource().build();

        assertTrue(rules.userCanSeeGrantClaimMaximumsForCompetition(competition, leadApplicant));
        assertTrue(rules.userCanSeeGrantClaimMaximumsForCompetition(competition, applicant));
        assertTrue(rules.userCanSeeGrantClaimMaximumsForCompetition(competition, collaborator));

        assertFalse(rules.userCanSeeGrantClaimMaximumsForCompetition(competition, compAdmin));
        assertFalse(rules.userCanSeeGrantClaimMaximumsForCompetition(competition, assessor));
    }
}
