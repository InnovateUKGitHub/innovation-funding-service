package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.transactional.CompetitionSummaryService;
import org.innovateuk.ifs.application.transactional.CompetitionSummaryServiceImpl;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.security.CompetitionLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CompetitionSummaryServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSummaryService> {

    private CompetitionSummaryPermissionRules rules;

    private CompetitionLookupStrategy competitionLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(CompetitionSummaryPermissionRules.class);
        competitionLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);
        initMocks(this);
    }

    @Override
    protected Class<? extends CompetitionSummaryService> getClassUnderTest() {
        return CompetitionSummaryServiceImpl.class;
    }

    @Test
    public void getCompetitionSummaryByCompetitionId() {
        UserResource user = new UserResource();
        setLoggedInUser(user);
        CompetitionResource competitionResource = newCompetitionResource().withId(1L).build();
        when(competitionLookupStrategy.getCompetitionResource(1L)).thenReturn(competitionResource);
        assertAccessDenied(() -> classUnderTest.getCompetitionSummaryByCompetitionId(1L), () -> {
            verify(rules).allInternalUsersCanViewCompetitionSummaryOtherThanInnovationLeadsAndStakeholders(any(CompetitionResource.class), eq(user));
            verify(rules).innovationLeadsCanViewCompetitionSummaryOnAssignedComps(any(CompetitionResource.class), eq(user));
            verify(rules).stakeholdersCanViewCompetitionSummaryOnAssignedComps(any(CompetitionResource.class), eq(user));
            verify(rules).competitionFinanceUsersCanViewCompetitionSummaryOnAssignedComps(competitionResource, user);
            verifyNoMoreInteractions(rules);
        });
    }
}
