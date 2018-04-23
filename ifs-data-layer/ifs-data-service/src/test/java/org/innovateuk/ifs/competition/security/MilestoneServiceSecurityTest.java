package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.competition.transactional.MilestoneServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
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
        when(competitionLookupStrategies.getCompetitionCompositeId(1L)).thenReturn(CompetitionCompositeId.id(1L));
        assertAccessDenied(() -> classUnderTest.getAllMilestonesByCompetitionId(1L), () -> {
            verify(rules).allInternalUsersCanViewCompetitionMilestonesOtherThanInnovationLeads(any(CompetitionCompositeId.class), isNull(UserResource.class));
            verify(rules).innovationLeadsCanViewMilestonesOnAssignedComps(any(CompetitionCompositeId.class), isNull(UserResource.class));
            verifyNoMoreInteractions(rules);
        });
    }
}
