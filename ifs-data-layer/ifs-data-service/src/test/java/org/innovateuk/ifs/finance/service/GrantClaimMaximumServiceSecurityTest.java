package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.security.CompetitionLookupStrategy;
import org.innovateuk.ifs.finance.security.GrantClaimMaximumPermissionRules;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GrantClaimMaximumServiceSecurityTest extends BaseServiceSecurityTest<GrantClaimMaximumService> {

    private GrantClaimMaximumPermissionRules grantClaimMaximumPermissionRules;
    private CompetitionLookupStrategy competitionLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        grantClaimMaximumPermissionRules = getMockPermissionRulesBean(GrantClaimMaximumPermissionRules.class);
        competitionLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);
    }

    @Test
    public void getGrantClaimMaximumById() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getGrantClaimMaximumById(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void save() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.save(newGrantClaimMaximumResource().build()),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getGrantClaimMaximumsForCompetitionType() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getGrantClaimMaximumsForCompetitionType(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void isMaximumFundingLevelOverridden() {
        CompetitionResource competition = newCompetitionResource().build();
        when(competitionLookupStrategy.getCompetititionResource(competition.getId())).thenReturn(competition);

        assertAccessDenied(
                () -> classUnderTest.isMaximumFundingLevelOverridden(competition.getId()),
                () -> verify(grantClaimMaximumPermissionRules)
                        .internalAdminAndUsersWithApplicationForCompetitionCanCheckMaxFundingLevelOverridden(
                                isA(CompetitionResource.class), isA(UserResource.class))
        );
    }

    @Override
    protected Class<? extends GrantClaimMaximumService> getClassUnderTest() {
        return GrantClaimMaximumServiceImpl.class;
    }
}
