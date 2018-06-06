package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ReviewPermissionRulesTest extends BasePermissionRulesTest<ReviewPermissionRules> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void reviewPanel() {
        UserResource loggedInUser = compAdminUser();

        for (CompetitionStatus competitionStatus : CompetitionStatus.values()) {
            final CompetitionResource competitionWithInterviewStage = newCompetitionResource()
                    .withHasAssessmentPanel(true)
                    .withCompetitionStatus(competitionStatus)
                    .build();

            final CompetitionResource competitionWithoutInterviewStage = newCompetitionResource()
                    .withHasAssessmentPanel(false)
                    .withCompetitionStatus(competitionStatus)
                    .build();

            when(competitionRestService.getCompetitionById(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionWithInterviewStage));
            when(competitionRestService.getCompetitionById(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionWithoutInterviewStage));

            switch (competitionStatus) {
                case ASSESSOR_FEEDBACK: case PROJECT_SETUP:
                    assertFalse("With interview stage and status " + competitionStatus.toString(),
                            rules.reviewPanel(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
                    break;
                default:
                    assertTrue("With interview stage and status " + competitionStatus.toString(),
                            rules.reviewPanel(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
            }
            assertFalse("Without interview stage and status " + competitionStatus.toString(),
                    rules.reviewPanel(CompetitionCompositeId.id(competitionWithoutInterviewStage.getId()), loggedInUser));
        }
    }

    @Test
    public void reviewPanelApplications() {
        UserResource loggedInUser = compAdminUser();

        for (CompetitionStatus competitionStatus : CompetitionStatus.values()) {
            final CompetitionResource competitionWithInterviewStage = newCompetitionResource()
                    .withHasAssessmentPanel(true)
                    .withCompetitionStatus(competitionStatus)
                    .build();

            final CompetitionResource competitionWithoutInterviewStage = newCompetitionResource()
                    .withHasAssessmentPanel(false)
                    .withCompetitionStatus(competitionStatus)
                    .build();

            when(competitionRestService.getCompetitionById(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionWithInterviewStage));
            when(competitionRestService.getCompetitionById(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionWithoutInterviewStage));

            if (competitionStatus == CompetitionStatus.FUNDERS_PANEL) {
                assertTrue(rules.reviewPanelApplications(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
            }
            else {
                assertFalse(rules.reviewPanelApplications(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
            }
            assertFalse(rules.reviewPanelApplications(CompetitionCompositeId.id(competitionWithoutInterviewStage.getId()), loggedInUser));
        }
    }

    @Override
    protected ReviewPermissionRules supplyPermissionRulesUnderTest() {
        return new ReviewPermissionRules();
    }
}
