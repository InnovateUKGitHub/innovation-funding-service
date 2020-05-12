package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.security.CompetitionLookupStrategy;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.status.transactional.InternalUserProjectStatusService;
import org.innovateuk.ifs.project.status.transactional.InternalUserProjectStatusServiceImpl;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectService interact with Spring Security
 */
public class InternalUserProjectStatusServiceSecurityTest extends BaseServiceSecurityTest<InternalUserProjectStatusService> {

    private StatusPermissionRules statusPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;
    private CompetitionLookupStrategy competitionLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        statusPermissionRules = getMockPermissionRulesBean(StatusPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        competitionLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);
    }

    @Test
    public void getCompetitionStatus(){

        Long competitionId = 1L;
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();

        when(competitionLookupStrategy.getCompetitionResource(competitionId)).thenReturn(competition);

        assertAccessDenied(() -> classUnderTest.getCompetitionStatus(competitionId, "12", 0, 1), () -> {
            verify(statusPermissionRules).internalAdminTeamCanViewCompetitionStatus(competition, getLoggedInUser());
            verify(statusPermissionRules).supportCanViewCompetitionStatus(competition, getLoggedInUser());
            verify(statusPermissionRules).assignedInnovationLeadCanViewCompetitionStatus(competition, getLoggedInUser());
            verify(statusPermissionRules).assignedStakeholderCanViewCompetitionStatus(competition, getLoggedInUser());
            verify(statusPermissionRules).assignedCompetitionFinanceCanViewCompetitionStatus(competition, getLoggedInUser());
            verifyNoMoreInteractions(statusPermissionRules);
        });
    }

    @Override
    protected Class<? extends InternalUserProjectStatusService> getClassUnderTest() {
        return InternalUserProjectStatusServiceImpl.class;
    }
}