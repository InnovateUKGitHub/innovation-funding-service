package org.innovateuk.ifs.management.review.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ReviewPermissionRulesTest extends BasePermissionRulesTest<ReviewPermissionRules> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Test
    public void reviewPanel() {
        UserResource loggedInUser = compAdminUser();

        for (CompetitionStatus competitionStatus : CompetitionStatus.values()) {
            final CompetitionResource competitionWithInterviewStage = newCompetitionResource()
                    .withCompetitionStatus(competitionStatus)
                    .build();

            CompetitionAssessmentConfigResource competitionAssessmentConfigResourceWithInterviewStage = newCompetitionAssessmentConfigResource()
                    .withIncludeAverageAssessorScoreInNotifications(false)
                    .withAssessorCount(5)
                    .withAssessorPay(BigDecimal.valueOf(100))
                    .withHasAssessmentPanel(true)
                    .withHasInterviewStage(true)
                    .withAssessorFinanceView(DETAILED)
                    .build();

            final CompetitionResource competitionWithoutInterviewStage = newCompetitionResource()
                    .withCompetitionStatus(competitionStatus)
                    .build();

            CompetitionAssessmentConfigResource competitionAssessmentConfigResourceWithoutInterviewStage = newCompetitionAssessmentConfigResource()
                    .withIncludeAverageAssessorScoreInNotifications(false)
                    .withAssessorCount(5)
                    .withAssessorPay(BigDecimal.valueOf(100))
                    .withHasAssessmentPanel(false)
                    .withHasInterviewStage(false)
                    .withAssessorFinanceView(DETAILED)
                    .build();

            when(competitionRestService.getCompetitionById(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionWithInterviewStage));
            when(competitionRestService.getCompetitionById(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionWithoutInterviewStage));
            when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionAssessmentConfigResourceWithInterviewStage));
            when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionAssessmentConfigResourceWithoutInterviewStage));


            switch (competitionStatus) {
                case ASSESSOR_FEEDBACK: case PROJECT_SETUP: case PREVIOUS:
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
                    .withCompetitionStatus(competitionStatus)
                    .build();

            CompetitionAssessmentConfigResource competitionAssessmentConfigResourceWithInterviewStage = newCompetitionAssessmentConfigResource()
                    .withIncludeAverageAssessorScoreInNotifications(false)
                    .withAssessorCount(5)
                    .withAssessorPay(BigDecimal.valueOf(100))
                    .withHasAssessmentPanel(true)
                    .withHasInterviewStage(true)
                    .withAssessorFinanceView(DETAILED)
                    .build();

            final CompetitionResource competitionWithoutInterviewStage = newCompetitionResource()
                    .withCompetitionStatus(competitionStatus)
                    .build();

            CompetitionAssessmentConfigResource competitionAssessmentConfigResourceWithoutInterviewStage = newCompetitionAssessmentConfigResource()
                    .withIncludeAverageAssessorScoreInNotifications(false)
                    .withAssessorCount(5)
                    .withAssessorPay(BigDecimal.valueOf(100))
                    .withHasAssessmentPanel(false)
                    .withHasInterviewStage(false)
                    .withAssessorFinanceView(DETAILED)
                    .build();

            when(competitionRestService.getCompetitionById(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionWithInterviewStage));
            when(competitionRestService.getCompetitionById(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionWithoutInterviewStage));
            when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionAssessmentConfigResourceWithInterviewStage));
            when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionAssessmentConfigResourceWithoutInterviewStage));

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
