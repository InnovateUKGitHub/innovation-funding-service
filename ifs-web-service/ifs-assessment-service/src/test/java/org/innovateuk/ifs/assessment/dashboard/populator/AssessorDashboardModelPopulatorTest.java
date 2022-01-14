package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorDashboardViewModel;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileStatusViewModel;
import org.innovateuk.ifs.assessment.service.CompetitionParticipantRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.service.RoleProfileStatusRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.review.builder.ReviewParticipantResourceBuilder.newReviewParticipantResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder.newAssessmentPeriodResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.interview.builder.InterviewParticipantResourceBuilder.newInterviewParticipantResource;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.review.builder.ReviewInviteResourceBuilder.newReviewInviteResource;
import static org.innovateuk.ifs.interview.builder.InterviewInviteResourceBuilder.newInterviewInviteResource;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessorDashboardModelPopulatorTest {

    @InjectMocks
    private AssessorDashboardModelPopulator assessorDashboardModelPopulator;

    @Mock
    private CompetitionParticipantRestService competitionParticipantRestService;

    @Mock
    private InterviewInviteRestService interviewInviteRestService;

    @Mock
    private ProfileRestService profileRestService;

    @Mock
    private ReviewInviteRestService reviewInviteRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private RoleProfileStatusRestService roleProfileStatusRestService;

    @Mock
    private AssessmentPeriodRestService assessmentPeriodRestService;

    private final Long USER_ID = 1L;
    private final Long COMPETITION_ID = 1L;
    private final Long COMPETITION_ID_WITH_ASSESSMENT_PERIOD = 2L;

    @Test
    public void populateModel() {
        UserResource userResource = newUserResource().withId(USER_ID).withRoleGlobal(Role.ASSESSOR).build();

        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource()
                .withUserId(USER_ID)
                .withRoleProfileState(RoleProfileState.ACTIVE)
                .build();
        when(roleProfileStatusRestService.findByUserIdAndProfileRole(USER_ID, ASSESSOR)).thenReturn(restSuccess(roleProfileStatusResource));

        UserProfileStatusResource profileStatusResource = newUserProfileStatusResource()
                .withUser(USER_ID)
                .build();
        when(profileRestService.getUserProfileStatus(USER_ID)).thenReturn(restSuccess(profileStatusResource));

        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource()
                .withId(COMPETITION_ID)
                .withOpen(true)
                .withInAssessment(true)
                .withAssessmentClosed(false)
                .build();
        when(assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(COMPETITION_ID)).thenReturn(restSuccess(emptyList()));
        when(assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(COMPETITION_ID_WITH_ASSESSMENT_PERIOD)).thenReturn(restSuccess(singletonList(assessmentPeriodResource)));

        CompetitionParticipantResource competitionParticipantResource1 = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
                .withStatus(ACCEPTED)
                .withUser(USER_ID)
                .withCompetition(COMPETITION_ID)
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().plusDays(0))
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();
        CompetitionParticipantResource competitionParticipantResource2 = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
                .withStatus(ACCEPTED)
                .withUser(USER_ID)
                .withCompetition(COMPETITION_ID_WITH_ASSESSMENT_PERIOD)
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().plusDays(0))
                .withCompetitionStatus(IN_ASSESSMENT)
                .withAssessmentPeriod(assessmentPeriodResource)
                .build();
        when(competitionParticipantRestService.getAssessorParticipants(USER_ID)).thenReturn(restSuccess(asList(competitionParticipantResource1, competitionParticipantResource2)));
        when(competitionParticipantRestService.getAssessorParticipantsWithAssessmentPeriod(USER_ID)).thenReturn(restSuccess(singletonList(competitionParticipantResource2)));

        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource()
                .withUser(USER_ID)
                .withCompetition(COMPETITION_ID_WITH_ASSESSMENT_PERIOD)
                .withStatus(ACCEPTED)
                .withInvite(newReviewInviteResource()
                        .withPanelDate(ZonedDateTime.now())
                )
                .build();
        when(reviewInviteRestService.getAllInvitesByUser(USER_ID)).thenReturn(restSuccess(singletonList(reviewParticipantResource)));

        InterviewParticipantResource interviewParticipantResource = newInterviewParticipantResource()
                .withUser(USER_ID)
                .withCompetition(COMPETITION_ID_WITH_ASSESSMENT_PERIOD)
                .withStatus(PENDING)
                .withInvite(newInterviewInviteResource()
                        .withInviteHash("")
                )
                .build();
        when(interviewInviteRestService.getAllInvitesByUser(USER_ID)).thenReturn(restSuccess(singletonList(interviewParticipantResource)));

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withAlwaysOpen(false)
                .withFundersPanelDate(ZonedDateTime.now().plusDays(2))
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        CompetitionResource competitionResourceWithAssessmentPeriod = newCompetitionResource()
                .withId(COMPETITION_ID_WITH_ASSESSMENT_PERIOD)
                .withAlwaysOpen(true)
                .withFundersPanelDate(ZonedDateTime.now().plusDays(2))
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID_WITH_ASSESSMENT_PERIOD)).thenReturn(restSuccess(competitionResourceWithAssessmentPeriod));

        AssessorDashboardViewModel viewModel = assessorDashboardModelPopulator.populateModel(userResource);
        assertEquals(viewModel.getProfileStatus(), new AssessorProfileStatusViewModel(profileStatusResource, roleProfileStatusResource.getRoleProfileState()));
        assertEquals(viewModel.getActiveCompetitions().size(), 1);
        assertEquals(viewModel.getUpcomingCompetitions().size(), 1);
        assertEquals(viewModel.getPendingInvites().size(), 0);
        assertEquals(viewModel.getAssessmentPanelInvites().size(), 0);
        assertEquals(viewModel.getAssessmentPanelAccepted().size(), 1);
        assertEquals(viewModel.getInterviewPanelInvites().size(), 1);
        assertEquals(viewModel.getInterviewPanelAccepted().size(), 0);

        verify(roleProfileStatusRestService, times(1)).findByUserIdAndProfileRole(USER_ID, ASSESSOR);
        verify(profileRestService, times(1)).getUserProfileStatus(USER_ID);
        verify(competitionParticipantRestService, times(1)).getAssessorParticipants(USER_ID);
        verify(reviewInviteRestService, times(1)).getAllInvitesByUser(USER_ID);
        verify(interviewInviteRestService, times(1)).getAllInvitesByUser(USER_ID);
        verify(assessmentPeriodRestService, times(1)).getAssessmentPeriodByCompetitionId(COMPETITION_ID);
        verify(assessmentPeriodRestService, times(1)).getAssessmentPeriodByCompetitionId(COMPETITION_ID_WITH_ASSESSMENT_PERIOD);
        verify(competitionRestService, times(0)).getCompetitionById(COMPETITION_ID);
        verify(competitionRestService, times(2)).getCompetitionById(COMPETITION_ID_WITH_ASSESSMENT_PERIOD);
    }

}
