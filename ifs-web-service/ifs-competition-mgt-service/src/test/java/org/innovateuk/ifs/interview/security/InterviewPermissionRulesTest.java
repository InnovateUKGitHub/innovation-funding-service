package org.innovateuk.ifs.interview.security;

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

public class InterviewPermissionRulesTest extends BasePermissionRulesTest<InterviewPermissionRules> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void interviewPanel() {

        CompetitionCompositeId successId = CompetitionCompositeId.id(1L);
        CompetitionCompositeId failureId = CompetitionCompositeId.id(2L);

        CompetitionResource successCompetition = CompetitionResourceBuilder.newCompetitionResource()
                .withHasInterviewStage(true).build();
        CompetitionResource failureCompetition = CompetitionResourceBuilder.newCompetitionResource()
                .withHasInterviewStage(false).build();

        UserResource loggedInUser = compAdminUser();

        when(competitionRestService.getCompetitionById(successId.id())).thenReturn(restSuccess(successCompetition));
        when(competitionRestService.getCompetitionById(failureId.id())).thenReturn(restSuccess(failureCompetition));

        assertTrue(rules.interviewPanel(successId, loggedInUser));
        assertFalse(rules.interviewPanel(failureId, loggedInUser));
    }

    @Test
    public void interviewPanelApplications() {

        CompetitionCompositeId successId = CompetitionCompositeId.id(1L);
        CompetitionCompositeId failureId = CompetitionCompositeId.id(2L);

        CompetitionResource successCompetition = CompetitionResourceBuilder.newCompetitionResource()
                .withHasInterviewStage(true)
                .withCompetitionStatus(CompetitionStatus.FUNDERS_PANEL)
                .build();
        CompetitionResource failureCompetition = CompetitionResourceBuilder.newCompetitionResource()
                .withHasInterviewStage(true)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        UserResource loggedInUser = compAdminUser();

        when(competitionRestService.getCompetitionById(successId.id())).thenReturn(restSuccess(successCompetition));
        when(competitionRestService.getCompetitionById(failureId.id())).thenReturn(restSuccess(failureCompetition));

        assertTrue(rules.interviewPanelApplications(successId, loggedInUser));
        assertFalse(rules.interviewPanelApplications(failureId, loggedInUser));
    }

    @Override
    protected InterviewPermissionRules supplyPermissionRulesUnderTest() {
        return new InterviewPermissionRules();
    }
}
