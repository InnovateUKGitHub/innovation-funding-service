package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.security.CompetitionTypeLookupStrategy;
import org.innovateuk.ifs.finance.security.GrantClaimMaximumPermissionRules;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionTypeResourceBuilder.newCompetitionTypeResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GrantClaimMaximumServiceSecurityTest extends BaseServiceSecurityTest<GrantClaimMaximumService> {

    private GrantClaimMaximumPermissionRules grantClaimMaximumPermissionRules;
    private CompetitionTypeLookupStrategy competitionTypeLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        grantClaimMaximumPermissionRules = getMockPermissionRulesBean(GrantClaimMaximumPermissionRules.class);
        competitionTypeLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionTypeLookupStrategy.class);
    }

    @Test
    public void testGetGrantClaimMaximumsForCompetitionType() {
        final Long competitionTypeId = 1L;

        when(competitionTypeLookupStrategy.getCompetititionTypeResource(competitionTypeId))
                .thenReturn(newCompetitionTypeResource().build());

        assertAccessDenied(
                () -> classUnderTest.getGrantClaimMaximumsForCompetitionType(competitionTypeId),
                () -> verify(grantClaimMaximumPermissionRules).userCanSeeGrantClaimMaximumsForCompetitionType(
                        isA(CompetitionTypeResource.class), isA(UserResource.class))
        );
    }

    @Override
    protected Class<? extends GrantClaimMaximumService> getClassUnderTest() {
        return GrantClaimMaximumServiceImpl.class;
    }
}
