package org.innovateuk.ifs.competitionsetup.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionSetupPermissionRulesTest extends BasePermissionRulesTest<CompetitionSetupPermissionRules> {

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Test
    public void manageInnovationLeads() {

        long competitionId = 14L;

        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource().withSetupComplete(Boolean.TRUE).build();

        UserResource loggedInUser = new UserResource();

        when(competitionServiceMock.getById(competitionId)).thenReturn(competitionResource);
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)).thenReturn(Boolean.TRUE);
        assertTrue(rules.manageInnovationLead(competitionId, loggedInUser));
    }

    @Override
    protected CompetitionSetupPermissionRules supplyPermissionRulesUnderTest() {
        return new CompetitionSetupPermissionRules();
    }
}
