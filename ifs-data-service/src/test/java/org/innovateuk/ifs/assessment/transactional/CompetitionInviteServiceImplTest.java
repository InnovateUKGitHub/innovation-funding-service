package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.CategoryBuilder.newCategory;
import static org.innovateuk.ifs.category.builder.CategoryResourceBuilder.newCategoryResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class CompetitionInviteServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CompetitionInviteService competitionInviteService = new CompetitionInviteServiceImpl();

    private CompetitionParticipant competitionParticipant;
    private UserResource userResource;
    private User user;


    @Before
    public void setUp() {
        List<Milestone> milestones = newMilestone()
                .withDate(LocalDateTime.now().minusDays(1))
                .withType(OPEN_DATE, SUBMISSION_DATE, ASSESSORS_NOTIFIED).build(3);
        milestones.addAll(newMilestone()
                .withDate(LocalDateTime.now().plusDays(1))
                .withType(NOTIFICATIONS, ASSESSOR_DEADLINE)
                .build(2));
        Competition competition = newCompetition().withName("my competition")
                .withMilestones(milestones)
                .withSetupComplete(true)
                .build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withStatus(SENT)
                .withCompetition(competition)
                .build();

        competitionParticipant = new CompetitionParticipant(competitionInvite);
        CompetitionInviteResource expected = newCompetitionInviteResource().withCompetitionName("my competition").build();
        RejectionReason rejectionReason = newRejectionReason().withId(1L).withReason("not available").build();
        userResource = newUserResource().withId(7L).build();
        user = newUser().withId(7L).build();


        when(competitionInviteRepositoryMock.getByHash("inviteHash")).thenReturn(competitionInvite);

        when(competitionInviteRepositoryMock.save(same(competitionInvite))).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(same(competitionInvite))).thenReturn(expected);

        when(competitionParticipantRepositoryMock.getByInviteHash("inviteHash")).thenReturn(competitionParticipant);

        when(rejectionReasonRepositoryMock.findOne(1L)).thenReturn(rejectionReason);

        when(userRepositoryMock.findOne(7L)).thenReturn(user);
    }

    @Test
    public void getInvite() throws Exception {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = competitionInviteService.getInvite("inviteHash");

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccessObjectOrThrowException();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionInviteMapperMock, calls(1)).mapToResource(any(CompetitionInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_hashNotExists() throws Exception {
        when(competitionInviteRepositoryMock.getByHash(anyString())).thenReturn(null);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = competitionInviteService.getInvite("inviteHashNotExists");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(notFoundError(CompetitionInvite.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_afterAccepted() throws Exception {
        competitionInviteService.openInvite("inviteHash");
        ServiceResult<Void> acceptResult = competitionInviteService.acceptInvite("inviteHash", userResource);
        assertTrue(acceptResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = competitionInviteService.getInvite("inviteHash");
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_afterRejected() throws Exception {
        RejectionReasonResource rejectionReason = RejectionReasonResourceBuilder.newRejectionReasonResource()
                .withId(1L)
                .build();

        competitionInviteService.openInvite("inviteHash");
        ServiceResult<Void> rejectResult = competitionInviteService.rejectInvite("inviteHash", rejectionReason, Optional.of("no time"));
        assertTrue(rejectResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = competitionInviteService.getInvite("inviteHash");
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite() throws Exception {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = competitionInviteService.openInvite("inviteHash");

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccessObjectOrThrowException();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).save(any(CompetitionInvite.class));
        inOrder.verify(competitionInviteMapperMock, calls(1)).mapToResource(any(CompetitionInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(competitionInviteRepositoryMock.getByHash(anyString())).thenReturn(null);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = competitionInviteService.openInvite("inviteHashNotExists");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(notFoundError(CompetitionInvite.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_inviteExpired() throws Exception {
        Competition competition = newCompetition().withName("my competition").withAssessorAcceptsDate(LocalDateTime.now().minusDays(1)).build();
        CompetitionInvite competitionInvite = newCompetitionInvite().withCompetition(competition).build();
        when(competitionInviteRepositoryMock.getByHash(anyString())).thenReturn(competitionInvite);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = competitionInviteService.openInvite("inviteHashExpired");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(new Error(COMPETITION_INVITE_EXPIRED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHashExpired");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_afterAccepted() throws Exception {
        competitionInviteService.openInvite("inviteHash");
        ServiceResult<Void> acceptResult = competitionInviteService.acceptInvite("inviteHash", userResource);
        assertTrue(acceptResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = competitionInviteService.openInvite("inviteHash");
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_afterRejected() throws Exception {
        RejectionReasonResource rejectionReason = RejectionReasonResourceBuilder.newRejectionReasonResource()
                .withId(1L)
                .build();

        competitionInviteService.openInvite("inviteHash");
        ServiceResult<Void> rejectResult = competitionInviteService.rejectInvite("inviteHash", rejectionReason, Optional.of("no time"));
        assertTrue(rejectResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = competitionInviteService.openInvite("inviteHash");
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    // accept

    @Test
    public void acceptInvite() {
        competitionInviteService.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());
        assertNull(competitionParticipant.getUser());

        ServiceResult<Void> serviceResult = competitionInviteService.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock, calls(1)).findOne(7L);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).save(competitionParticipant);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_hashNotExists() {
        ServiceResult<Void> serviceResult = competitionInviteService.acceptInvite("inviteHashNotExists", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionParticipant.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_notOpened() {
        assertEquals(SENT, competitionParticipant.getInvite().getStatus());
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        ServiceResult<Void> serviceResult = competitionInviteService.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_alreadyAccepted() {
        competitionInviteService.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // accept the invite
        ServiceResult<Void> serviceResult = competitionInviteService.acceptInvite("inviteHash", userResource);
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.ACCEPTED, competitionParticipant.getStatus());

        // accept a second time
        serviceResult = competitionInviteService.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).save(competitionParticipant);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_alreadyRejected() {
        competitionInviteService.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // reject the invite
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = competitionInviteService.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());

        // accept the invite
        serviceResult = competitionInviteService.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).save(competitionParticipant);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    // reject

    @Test
    public void rejectInvite() {
        competitionInviteService.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = competitionInviteService.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals("too busy", competitionParticipant.getRejectionReasonComment());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock, calls(1)).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).save(competitionParticipant);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_hashNotExists() {
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = competitionInviteService.rejectInvite("inviteHashNotExists", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionParticipant.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_notOpened() {
        assertEquals(SENT, competitionParticipant.getInvite().getStatus());
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = competitionInviteService.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, "my competition")));

        InOrder inOrder = inOrder(rejectionReasonRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock, calls(1)).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_alreadyAccepted() {
        competitionInviteService.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // accept the invite
        ServiceResult<Void> serviceResult = competitionInviteService.acceptInvite("inviteHash", userResource);
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.ACCEPTED, competitionParticipant.getStatus());

        // reject
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        serviceResult = competitionInviteService.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).save(competitionParticipant);
        inOrder.verify(rejectionReasonRepositoryMock, calls(1)).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_alreadyRejected() {
        competitionInviteService.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // reject the invite
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();
        ServiceResult<Void> serviceResult = competitionInviteService.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());

        // reject again

        serviceResult = competitionInviteService.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("still too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);

        inOrder.verify(rejectionReasonRepositoryMock, calls(1)).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).save(competitionParticipant);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_unknownRejectionReason() {
        competitionInviteService.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());


        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(2L)
                .build();

        ServiceResult<Void> serviceResult = competitionInviteService.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(RejectionReason.class, 2L)));

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock, calls(1)).findOne(2L);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_emptyComment() {
        competitionInviteService.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());


        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = competitionInviteService.rejectInvite("inviteHash", rejectionReasonResource, Optional.of(""));


        assertTrue(serviceResult.isSuccess());

        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals("", competitionParticipant.getRejectionReasonComment());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock, calls(1)).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).save(competitionParticipant);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendInvite() throws Exception {
        long inviteId = 1L;
        CompetitionInvite invite = spy(
                newCompetitionInvite()
                .withCompetition(newCompetition().withName("my competition").build())
                .withStatus(CREATED)
                .build()
        );

        when(competitionInviteRepositoryMock.findOne(inviteId)).thenReturn(invite);

        ServiceResult<Void> serviceResult = competitionInviteService.sendInvite(inviteId);

        assertTrue(serviceResult.isSuccess());
        assertEquals(SENT, invite.getStatus());

        verify(invite).send();
        verify(competitionInviteRepositoryMock).findOne(inviteId);
        verifyNoMoreInteractions(competitionInviteRepositoryMock);
    }

    @Test
    public void sendInvite_alreadySent() throws Exception {
        long inviteId = 1L;
        CompetitionInvite invite = newCompetitionInvite()
                .withCompetition(newCompetition().withName("my competition").build())
                .withStatus(SENT)
                .build();

        when(competitionInviteRepositoryMock.findOne(inviteId)).thenReturn(invite);

        try {
            competitionInviteService.sendInvite(inviteId);
            fail();
        } catch (RuntimeException e) {
            assertSame(IllegalStateException.class, e.getCause().getClass());

            verify(competitionInviteRepositoryMock).findOne(inviteId);
            verifyNoMoreInteractions(competitionInviteRepositoryMock);
        }
    }

    @Test
    public void checkExistingUser_hashNotExists() throws Exception {
        when(competitionInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(null);

        ServiceResult<Boolean> result = competitionInviteService.checkExistingUser("hash");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(CompetitionInvite.class, "hash")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userServiceMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userServiceMock, never()).findByEmail(isA(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userExistsOnInvite() throws Exception {
        User user = newUser().build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withUser(user)
                .withEmail("test@test.com")
                .build();

        when(competitionInviteRepositoryMock.getByHash("hash")).thenReturn(competitionInvite);

        assertTrue(competitionInviteService.checkExistingUser("hash").getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userServiceMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userServiceMock, never()).findByEmail(isA(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userExistsForEmail() throws Exception {
        UserResource user = newUserResource().build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withEmail("test@test.com")
                .build();

        when(competitionInviteRepositoryMock.getByHash("hash")).thenReturn(competitionInvite);
        when(userServiceMock.findByEmail("test@test.com")).thenReturn(serviceSuccess(user));

        assertTrue(competitionInviteService.checkExistingUser("hash").getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userServiceMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userServiceMock).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userDoesNotExist() throws Exception {
        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withEmail("test@test.com")
                .build();

        when(competitionInviteRepositoryMock.getByHash("hash")).thenReturn(competitionInvite);
        when(userServiceMock.findByEmail("test@test.com")).thenReturn(serviceFailure(notFoundError(UserResource.class, "hash")));

        assertFalse(competitionInviteService.checkExistingUser("hash").getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userServiceMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userServiceMock).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;

        List<AvailableAssessorResource> expected = newAvailableAssessorResource()
                .withFirstName("Jeremy")
                .withLastName("Alufson")
                .withInnovationArea(newCategoryResource()
                        .with(id(null))
                        .withName("Earth Observation")
                        .build())
                .withCompliant(TRUE)
                .withEmail("worth.email.test+assessor1@gmail.com")
                .withBusinessType(BUSINESS)
                .withAdded(FALSE)
                .build(1);

        List<AvailableAssessorResource> actual = competitionInviteService.getAvailableAssessors(competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;

        List<AssessorCreatedInviteResource> expected = newAssessorCreatedInviteResource()
                .withFirstName("Jeremy")
                .withLastName("Alufson")
                .withInnovationArea(newCategoryResource()
                        .with(id(null))
                        .withName("Earth Observation")
                        .build())
                .withCompliant(TRUE)
                .withEmail("worth.email.test+assessor1@gmail.com")
                .build(1);

        List<AssessorCreatedInviteResource> actual = competitionInviteService.getCreatedInvites(competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;

        // TODO INFUND-6450

        List<AssessorInviteOverviewResource> actual = competitionInviteService.getInvitationOverview(competitionId).getSuccessObjectOrThrowException();
        assertTrue(actual.isEmpty());
    }

    @Test
    public void inviteUser_existing() {
        User newUser = newUser()
                .withEmailAddress("tom@poly.io")
                .withFirstName("tom")
                .withLastName("baldwin")
                .build();

        Competition competition = newCompetition()
                .withName("competition name")
                .build();

        ExistingUserStagedInviteResource existingAssessor = newExistingUserStagedInviteResource()
                .withCompetitionId(competition.getId())
                .withEmail(newUser.getEmail())
                .build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withEmail(newUser.getEmail())
                .withName(newUser.getName())
                .build();

        CompetitionInviteResource expectedInviteResource = newCompetitionInviteResource().build();

        when(userRepositoryMock.findByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        CompetitionInvite inviteExpectation = argThat(lambdaMatches(invite -> {
            assertEquals(newUser.getEmail(), invite.getEmail());
            assertEquals(newUser.getName(), invite.getName());
            assertEquals(CREATED, invite.getStatus());
            assertEquals(competition, invite.getTarget());
            assertFalse(invite.getHash().isEmpty());
            assertNull(invite.getInnovationArea());
            return true;
        }));

        when(competitionInviteRepositoryMock.save(inviteExpectation)).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(competitionInvite)).thenReturn(expectedInviteResource);

        CompetitionInviteResource invite = competitionInviteService.inviteUser(existingAssessor).getSuccessObjectOrThrowException();

        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(userRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(userRepositoryMock).findByEmail(newUser.getEmail());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(any(CompetitionInvite.class));
        inOrder.verify(competitionInviteMapperMock).mapToResource(competitionInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUser_new() {
        final String newAssessorName = "tom baldwin";
        final String newAssessorEmail = "tom@poly.io";

        final Competition competition = newCompetition().build();

        final Category innovationArea = newCategory()
                .withName("machine learning")
                .withType(INNOVATION_AREA)
                .build();

        final NewUserStagedInviteResource newAssessor = newNewUserStagedInviteResource()
                .withEmail(newAssessorName)
                .withName(newAssessorEmail)
                .withCompetitionId(competition.getId())
                .withInnovationCategoryId(innovationArea.getId())
                .build();

        final CompetitionInvite competitionInvite = newCompetitionInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withEmail(newAssessorName)
                .withName(newAssessorEmail)
                .withInnovationArea(innovationArea)
                .withUser()
                .build();

        final CompetitionInviteResource expectedInviteResource = newCompetitionInviteResource().build();

        when(categoryRepositoryMock.findByIdAndType(innovationArea.getId(), innovationArea.getType())).thenReturn(innovationArea);
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        CompetitionInvite inviteExpectation = argThat(lambdaMatches(invite -> {
            assertEquals(newAssessorName, invite.getEmail());
            assertEquals(newAssessorEmail, invite.getName());
            assertEquals(CREATED, invite.getStatus());
            assertEquals(competition, invite.getTarget());
            assertFalse(invite.getHash().isEmpty());
            assertEquals(innovationArea, invite.getInnovationArea());
            return true;
        }));

        when(competitionInviteRepositoryMock.save(inviteExpectation)).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(competitionInvite)).thenReturn(expectedInviteResource);

        CompetitionInviteResource invite = competitionInviteService.inviteUser(newAssessor).getSuccessObjectOrThrowException();

        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(categoryRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(categoryRepositoryMock).findByIdAndType(innovationArea.getId(), innovationArea.getType());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(any(CompetitionInvite.class));
        inOrder.verify(competitionInviteMapperMock).mapToResource(competitionInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite() {
        final String email = "tom@poly.io";
        final long competitonId = 11L;
        final CompetitionInvite competitionInvite = newCompetitionInvite()
                .withStatus(CREATED)
                .build();

        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(email, competitonId)).thenReturn(competitionInvite);

        competitionInviteService.deleteInvite(email, competitonId).getSuccessObjectOrThrowException();

        InOrder inOrder = inOrder(competitionInviteRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(email, competitonId);
        inOrder.verify(competitionInviteRepositoryMock).delete(competitionInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite_sent() {
        final String email = "tom@poly.io";
        final long competitonId = 11L;
        final CompetitionInvite competitionInvite = newCompetitionInvite()
                .withStatus(SENT)
                .build();

        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(email, competitonId)).thenReturn(competitionInvite);

        ServiceResult<Void> serviceResult = competitionInviteService.deleteInvite(email, competitonId);

        assertTrue(serviceResult.isFailure());

        verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(email, competitonId);
        verifyNoMoreInteractions(competitionInviteRepositoryMock);
    }
}
