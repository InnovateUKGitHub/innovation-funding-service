package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.competition.transactional.MilestoneServiceImpl;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MilestoneServiceSecurityTest extends BaseServiceSecurityTest<MilestoneService> {

    private MilestonePermissionRules rules;
    private CompetitionLookupStrategy competitionLookupStrategies;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(MilestonePermissionRules.class);
        competitionLookupStrategies = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);
        initMocks(this);

    }

    @Override
    protected Class<? extends MilestoneService> getClassUnderTest() {
        return MilestoneServiceImpl.class;
    }

    @Test
    public void getCompetitionById() {

        setLoggedInUser(null);

        CompetitionCompositeId compositeId = CompetitionCompositeId.id(1L);

        when(competitionLookupStrategies.getCompetitionCompositeId(1L)).thenReturn(compositeId);

        assertAccessDenied(() -> classUnderTest.getAllMilestonesByCompetitionId(1L), () -> {
            verify(rules).allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(compositeId, null);
            verify(rules).innovationLeadsCanViewMilestonesOnAssignedComps(compositeId, null);
            verify(rules).stakeholdersCanViewMilestonesOnAssignedComps(compositeId, null);
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void updateCompletionStage() {

        setLoggedInUser(null);

        CompetitionCompositeId compositeId = CompetitionCompositeId.id(1L);

        when(competitionLookupStrategies.getCompetitionCompositeId(1L)).thenReturn(compositeId);

        assertAccessDenied(() -> classUnderTest.updateCompletionStage(1L, CompetitionCompletionStage.PROJECT_SETUP), () -> {
            verify(rules).compAdminsAndProjectFinanceUserCanUpdateCompletionStageDuringCompetitionSetup(compositeId, null);
            verifyNoMoreInteractions(rules);
        });
    }
}
