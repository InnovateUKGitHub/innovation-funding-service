package org.innovateuk.ifs.competitionsetup.core.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
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
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId.id())).thenReturn(Boolean.TRUE);
        assertTrue(rules.manageInnovationLead(competitionId, loggedInUser));
    }

    @Override
    protected CompetitionSetupPermissionRules supplyPermissionRulesUnderTest() {
        return new CompetitionSetupPermissionRules();
    }
}
