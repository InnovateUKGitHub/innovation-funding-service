package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.transactional.CompetitionSummaryService;
import org.innovateuk.ifs.commons.service.ServiceResult;
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
        return TestCompetitionSummaryService.class;
    }

    @Test
    public void getCompetitionSummaryByCompetitionId() {
        setLoggedInUser(null);
        CompetitionResource competitionResource = newCompetitionResource().withId(1L).build();
        when(competitionLookupStrategy.getCompetititionResource(1L)).thenReturn(competitionResource);
        assertAccessDenied(() -> classUnderTest.getCompetitionSummaryByCompetitionId(1L), () -> {
            verify(rules).allInternalUsersCanViewCompetitionSummaryOtherThanInnovationLeads(any(CompetitionResource.class), isNull(UserResource.class));
            verify(rules).innovationLeadsCanViewCompetitionSummaryOnAssginedComps(any(CompetitionResource.class), isNull(UserResource.class));
            verifyNoMoreInteractions(rules);
        });
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestCompetitionSummaryService implements CompetitionSummaryService {

        @Override
        public ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId) {
            return null;
        }
    }
}
