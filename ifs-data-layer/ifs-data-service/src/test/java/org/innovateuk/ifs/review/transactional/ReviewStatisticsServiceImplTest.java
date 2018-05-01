package org.innovateuk.ifs.review.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.review.builder.ReviewInviteBuilder.newReviewInvite;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class ReviewStatisticsServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ReviewStatisticsService reviewStatisticsService = new ReviewStatisticsServiceImpl();

    @Test
    public void getReviewPanelKeyStatistics() {
        long competitionId = 1L;
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");
        Profile profile = newProfile().withId(7L).build();
        User user = newUser().withId(11L).withProfileId(profile.getId()).build();

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        List<ReviewInvite> panelInvites = newReviewInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(SENT)
                .withUser(user)
                .build(2);

        List<Long> panelInviteIds = simpleMap(panelInvites, ReviewInvite::getId);

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.SUBMITTED)
                .build(2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateInAndIdLike(
                competitionId, SUBMITTED_STATES, "",  null,true)).thenReturn(applications);
        when(reviewInviteRepositoryMock.getByCompetitionId(competitionId)).thenReturn(panelInvites);
        when(reviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds))
                .thenReturn(1);
        when(reviewInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, singleton(InviteStatus.SENT)))
                .thenReturn(1);
        when(reviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.PENDING, panelInviteIds))
                .thenReturn(1);

        ServiceResult<ReviewKeyStatisticsResource> serviceResult = reviewStatisticsService.getReviewPanelKeyStatistics(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, reviewInviteRepositoryMock, reviewParticipantRepositoryMock);
        inOrder.verify(reviewInviteRepositoryMock).getByCompetitionId(competitionId);
        inOrder.verify(applicationRepositoryMock).findByCompetitionIdAndApplicationProcessActivityStateInAndIdLike(
                competitionId, SUBMITTED_STATES, "",  null,true);

        inOrder.verify(reviewParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds);
        inOrder.verify(reviewInviteRepositoryMock).countByCompetitionIdAndStatusIn(competitionId, singleton(InviteStatus.SENT));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());

        ReviewKeyStatisticsResource result = serviceResult.getSuccess();
        assertEquals(2, result.getApplicationsInPanel());
        assertEquals(1, result.getAssessorsAccepted());
        assertEquals(1, result.getAssessorsPending());
    }

    @Test
    public void getReviewInviteStatistics() {
        long competitionId = 1L;
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");
        Profile profile = newProfile().withId(7L).build();
        User user = newUser().withId(11L).withProfileId(profile.getId()).build();

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        List<ReviewInvite> panelInvites = newReviewInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(SENT)
                .withUser(user)
                .build(2);

        List<Long> panelInviteIds = simpleMap(panelInvites, ReviewInvite::getId);

        when(reviewInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT))).thenReturn(2);
        when(reviewInviteRepositoryMock.getByCompetitionId(competitionId)).thenReturn(panelInvites);

        when(reviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds))
                .thenReturn(1);
        when(reviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.REJECTED, panelInviteIds))
                .thenReturn(1);

        ServiceResult<ReviewInviteStatisticsResource> serviceResult = reviewStatisticsService.getReviewInviteStatistics(competitionId);

        InOrder inOrder = inOrder(reviewInviteRepositoryMock, reviewParticipantRepositoryMock);
        inOrder.verify(reviewInviteRepositoryMock).getByCompetitionId(competitionId);
        inOrder.verify(reviewParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds);
        inOrder.verify(reviewParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.REJECTED, panelInviteIds);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());

        ReviewInviteStatisticsResource result = serviceResult.getSuccess();
        assertEquals(2, result.getInvited());
        assertEquals(1, result.getAccepted());
        assertEquals(1, result.getDeclined());
    }
}