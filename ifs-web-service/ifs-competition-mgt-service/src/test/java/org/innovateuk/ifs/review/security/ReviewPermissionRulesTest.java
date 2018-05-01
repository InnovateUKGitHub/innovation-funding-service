package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ReviewPermissionRulesTest extends BasePermissionRulesTest<ReviewPermissionRules> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void reviewPanel() {

        CompetitionCompositeId successId = CompetitionCompositeId.id(1L);
        CompetitionCompositeId failureId = CompetitionCompositeId.id(2L);

        CompetitionResource successCompetition = CompetitionResourceBuilder.newCompetitionResource()
                .withHasAssessmentPanel(true).build();
        CompetitionResource failureCompetition = CompetitionResourceBuilder.newCompetitionResource()
                .withHasAssessmentPanel(false).build();

        UserResource loggedInUser = compAdminUser();

        when(competitionRestService.getCompetitionById(successId.id())).thenReturn(restSuccess(successCompetition));
        when(competitionRestService.getCompetitionById(failureId.id())).thenReturn(restSuccess(failureCompetition));

        assertTrue(rules.reviewPanel(successId, loggedInUser));
        assertFalse(rules.reviewPanel(failureId, loggedInUser));
    }

    @Test
    public void reviewPanelApplications() {

        CompetitionCompositeId successId = CompetitionCompositeId.id(1L);
        CompetitionCompositeId failureId = CompetitionCompositeId.id(2L);

        CompetitionResource successCompetition = CompetitionResourceBuilder.newCompetitionResource()
                .withHasAssessmentPanel(true)
                .withCompetitionStatus(CompetitionStatus.FUNDERS_PANEL)
                .build();
        CompetitionResource failureCompetition = CompetitionResourceBuilder.newCompetitionResource()
                .withHasAssessmentPanel(true)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        UserResource loggedInUser = compAdminUser();

        when(competitionRestService.getCompetitionById(successId.id())).thenReturn(restSuccess(successCompetition));
        when(competitionRestService.getCompetitionById(failureId.id())).thenReturn(restSuccess(failureCompetition));

        assertTrue(rules.reviewPanelApplications(successId, loggedInUser));
        assertFalse(rules.reviewPanelApplications(failureId, loggedInUser));
    }

    @Override
    protected ReviewPermissionRules supplyPermissionRulesUnderTest() {
        return new ReviewPermissionRules();
    }
}
