package org.innovateuk.ifs.management.competition.setup.core.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionSetupPermissionRulesTest extends BasePermissionRulesTest<CompetitionSetupPermissionRules> {

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void manageInnovationLeads() {

        CompetitionCompositeId competitionId = CompetitionCompositeId.id(14L);

        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource().withSetupComplete(Boolean.TRUE).build();

        UserResource loggedInUser = new UserResource();

        when(competitionRestService.getCompetitionById(competitionId.id())).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId.id())).thenReturn(true);
        assertTrue(rules.manageInnovationLead(competitionId, loggedInUser));
    }

    @Test
    public void manageStakeholders() {

        CompetitionCompositeId competitionId = CompetitionCompositeId.id(14L);
        UserResource loggedInUser = new UserResource();

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId.id())).thenReturn(Boolean.TRUE);
        assertTrue(rules.manageStakeholders(competitionId, loggedInUser));
    }

    @Test
    public void manageCompetitionFinance() {

        CompetitionCompositeId competitionId = CompetitionCompositeId.id(14L);
        UserResource loggedInUser = new UserResource();

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId.id())).thenReturn(Boolean.TRUE);
        assertTrue(rules.manageCompetitionFinance(competitionId, loggedInUser));
    }

    @Test
    public void choosePostAwardService() {

        CompetitionCompositeId competitionId = CompetitionCompositeId.id(14L);
        UserResource loggedInUser = new UserResource();

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId.id())).thenReturn(Boolean.TRUE);
        assertTrue(rules.choosePostAwardService(competitionId, loggedInUser));
    }

    @Override
    protected CompetitionSetupPermissionRules supplyPermissionRulesUnderTest() {
        return new CompetitionSetupPermissionRules();
    }
}
