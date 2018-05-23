package org.innovateuk.ifs.interview.security;

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

public class InterviewPermissionRulesTest extends BasePermissionRulesTest<InterviewPermissionRules> {

    @Mock
    private CompetitionRestService competitionRestService;

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

            when(competitionRestService.getCompetitionById(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionWithInterviewStage));
            when(competitionRestService.getCompetitionById(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionWithoutInterviewStage));

            if (competitionStatus == CompetitionStatus.ASSESSOR_FEEDBACK) {
                assertFalse(rules.interviewPanel(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
            }
            else {
                assertTrue(rules.interviewPanel(CompetitionCompositeId.id(competitionWithInterviewStage.getId()), loggedInUser));
            }
            assertFalse(rules.interviewPanel(CompetitionCompositeId.id(competitionWithoutInterviewStage.getId()), loggedInUser));
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

            when(competitionRestService.getCompetitionById(competitionWithInterviewStage.getId())).thenReturn(restSuccess(competitionWithInterviewStage));
            when(competitionRestService.getCompetitionById(competitionWithoutInterviewStage.getId())).thenReturn(restSuccess(competitionWithoutInterviewStage));

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
