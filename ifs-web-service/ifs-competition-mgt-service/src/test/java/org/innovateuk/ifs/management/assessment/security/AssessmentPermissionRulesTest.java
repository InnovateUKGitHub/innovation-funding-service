package org.innovateuk.ifs.management.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentPermissionRulesTest extends BasePermissionRulesTest<AssessmentPermissionRules> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Override
    protected AssessmentPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentPermissionRules();
    }

    @Test
    public void reviewPanel() {
        UserResource loggedInUser = compAdminUser();

        for (CompetitionStatus competitionStatus : CompetitionStatus.values()) {
            final CompetitionResource competition = newCompetitionResource()
                    .withCompetitionStatus(competitionStatus)
                    .build();

            when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

            switch (competitionStatus) {
                case COMPETITION_SETUP: case ASSESSOR_FEEDBACK: case PROJECT_SETUP: case PREVIOUS:
                    assertFalse("With status " + competitionStatus.toString(),
                            rules.assessment(CompetitionCompositeId.id(competition.getId()), loggedInUser));
                    break;
                default:
                    assertTrue("With status " + competitionStatus.toString(),
                            rules.assessment(CompetitionCompositeId.id(competition.getId()), loggedInUser));
            }
        }
    }

    @Test
    public void assessmentApplications() {
        UserResource loggedInUser = compAdminUser();

        for (CompetitionStatus competitionStatus : CompetitionStatus.values()) {
            final CompetitionResource competition = newCompetitionResource()
                    .withCompetitionStatus(competitionStatus)
                    .build();

            CompetitionAssessmentConfigResource competitionAssessmentConfigResource = newCompetitionAssessmentConfigResource()
                    .withIncludeAverageAssessorScoreInNotifications(false)
                    .withAssessorCount(5)
                    .withAssessorPay(BigDecimal.valueOf(100))
                    .withHasAssessmentPanel(true)
                    .withAssessorFinanceView(AssessorFinanceView.OVERVIEW)
                    .build();

            when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

            switch (competitionStatus) {
                case ASSESSOR_FEEDBACK: case PROJECT_SETUP: case PREVIOUS:
                    assertFalse("With status " + competitionStatus.toString(),
                            rules.assessmentApplications(CompetitionCompositeId.id(competition.getId()), loggedInUser));
                    break;
                default:
                    assertTrue("With status " + competitionStatus.toString(),
                            rules.assessmentApplications(CompetitionCompositeId.id(competition.getId()), loggedInUser));
            }
        }
    }
}