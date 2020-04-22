package org.innovateuk.ifs.management.interview.security;

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

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class InterviewPermissionRulesTest extends BasePermissionRulesTest<InterviewPermissionRules> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Override
    protected InterviewPermissionRules supplyPermissionRulesUnderTest() {
        return new InterviewPermissionRules();
    }

    @Test
    public void interviewPanel() {
        UserResource loggedInUser = compAdminUser();

        for (CompetitionStatus competitionStatus : CompetitionStatus.values()) {
            final CompetitionResource competitionWithInterviewStage = newCompetitionResource()
                    .withHasInterviewStage(true)
                    .withCompetitionStatus(competitionStatus)
                    .build();

            final CompetitionResource competitionWithoutInterviewStage = newCompetitionResource()
                    .withHasInterviewStage(false)
                    .withCompetitionStatus(competitionStatus)
                    .build();

            final CompetitionAssessmentConfigResource configResourceWithInterview = newCompetitionAssessmentConfigResource()
                    .withHasInterviewStage(true)
                    .build();
            final CompetitionAssessmentConfigResource configResourceWithoutInterview = newCompetitionAssessmentConfigResource()
                    .withHasInterviewStage(false)
                    .build();


            when(competitionRestService.getCompetitionById(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionWithInterviewStage));
            when(competitionRestService.getCompetitionById(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionWithoutInterviewStage));
            when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionWithInterviewStage.getId())).thenReturn(restSuccess(configResourceWithInterview));
            when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(configResourceWithoutInterview));

            switch (competitionStatus) {
                case ASSESSOR_FEEDBACK: case PROJECT_SETUP: case PREVIOUS:
                    assertFalse("With interview stage and status " + competitionStatus.toString(),
                            rules.interviewPanel(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
                    break;
                default:
                    assertTrue("With interview stage and status " + competitionStatus.toString(),
                            rules.interviewPanel(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
            }
            assertFalse("Without interview stage and status " + competitionStatus.toString(),
                    rules.interviewPanel(CompetitionCompositeId.id(competitionWithoutInterviewStage.getId()), loggedInUser));
        }
    }

    @Test
    public void interviewPanelApplications() {
        UserResource loggedInUser = compAdminUser();

        for (CompetitionStatus competitionStatus : CompetitionStatus.values()) {
            final CompetitionResource competitionWithInterviewStage = newCompetitionResource()
                    .withHasInterviewStage(true)
                    .withCompetitionStatus(competitionStatus)
                    .build();

            final CompetitionResource competitionWithoutInterviewStage = newCompetitionResource()
                    .withHasInterviewStage(false)
                    .withCompetitionStatus(competitionStatus)
                    .build();
            final CompetitionAssessmentConfigResource configResourceWithInterview = newCompetitionAssessmentConfigResource().withHasInterviewStage(true).build();
            final CompetitionAssessmentConfigResource configResourceWithoutInterview = newCompetitionAssessmentConfigResource().withHasInterviewStage(false).build();


            when(competitionRestService.getCompetitionById(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionWithInterviewStage));
            when(competitionRestService.getCompetitionById(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionWithoutInterviewStage));
            when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionWithInterviewStage.getId())).thenReturn(restSuccess(configResourceWithInterview));
            when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(configResourceWithoutInterview));

            if (competitionStatus == CompetitionStatus.FUNDERS_PANEL) {
                assertTrue(rules.interviewPanelApplications(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
            }
            else {
                assertFalse(rules.interviewPanelApplications(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
            }
            assertFalse(rules.interviewPanelApplications(CompetitionCompositeId.id(competitionWithoutInterviewStage.getId()), loggedInUser));
        }
    }
}