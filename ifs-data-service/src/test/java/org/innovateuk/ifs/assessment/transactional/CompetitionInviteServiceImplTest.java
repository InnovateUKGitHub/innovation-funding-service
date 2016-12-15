package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.invite.domain.*;
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
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
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
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static java.util.Arrays.asList;
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
                        .build())
                .withCompliant(TRUE)
                .withEmail("worth.email.test+assessor1@gmail.com")
                .withBusinessType(BUSINESS)
                .withAdded(FALSE)
                .build(1);

        when(userRepositoryMock.findAllAvailableAssessorsByCompetition(competitionId)).thenReturn(asList(newUser()
                .withId(77L)
                .withFirstName("Jeremy")
                .withLastName("Alufson")
                .withEmailAddress("worth.email.test+assessor1@gmail.com")
                .withProfile(newProfile().withBusinessType(BUSINESS).build())
                .build()));

        when(userProfileServiceMock.getUserProfileStatus(77L)).thenReturn(serviceSuccess(newUserProfileStatusResource()
                .withContractComplete(TRUE)
                .withSkillsComplete(TRUE)
                .withAffliliationsComplete(TRUE)
                .build()));

        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId("worth.email.test+assessor1@gmail.com", competitionId)).thenReturn(null);

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

        CompetitionInvite inviteExpectation = createInviteExpectations(newUser.getName(), newUser.getEmail(), CREATED, competition, null);

        when(competitionInviteRepositoryMock.save(inviteExpectation)).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(competitionInvite)).thenReturn(expectedInviteResource);

        CompetitionInviteResource invite = competitionInviteService.inviteUser(existingAssessor).getSuccessObjectOrThrowException();

        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(userRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(userRepositoryMock).findByEmail(newUser.getEmail());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(newUser.getName(), newUser.getEmail(), CREATED, competition, null));
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
                .withEmail(newAssessorEmail)
                .withName(newAssessorName)
                .withCompetitionId(competition.getId())
                .withInnovationCategoryId(innovationArea.getId())
                .build();

        final CompetitionInvite competitionInvite = newCompetitionInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withEmail(newAssessorEmail)
                .withName(newAssessorName)
                .withInnovationArea(innovationArea)
                .build();

        final CompetitionInviteResource expectedInviteResource = newCompetitionInviteResource().build();

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(categoryRepositoryMock.findByIdAndType(innovationArea.getId(), innovationArea.getType())).thenReturn(innovationArea);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(null);

        CompetitionInvite inviteExpectation = createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea);
        when(competitionInviteRepositoryMock.save(inviteExpectation)).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(competitionInvite)).thenReturn(expectedInviteResource);

        CompetitionInviteResource invite = competitionInviteService.inviteUser(newAssessor).getSuccessObjectOrThrowException();

        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(categoryRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(categoryRepositoryMock).findByIdAndType(innovationArea.getId(), innovationArea.getType());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea));
        inOrder.verify(competitionInviteMapperMock).mapToResource(competitionInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUser_new_categoryNotFound() {
        final String newAssessorName = "tom baldwin";
        final String newAssessorEmail = "tom@poly.io";
        final long innovationArea = 100L;

        final Competition competition = newCompetition().build();

        final NewUserStagedInviteResource newAssessor = newNewUserStagedInviteResource()
                .withEmail(newAssessorEmail)
                .withName(newAssessorName)
                .withCompetitionId(competition.getId())
                .withInnovationCategoryId(innovationArea)
                .build();

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(categoryRepositoryMock.findByIdAndType(innovationArea, INNOVATION_AREA)).thenReturn(null);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(null);

        ServiceResult<CompetitionInviteResource> serviceResult = competitionInviteService.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(categoryRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(categoryRepositoryMock).findByIdAndType(innovationArea, INNOVATION_AREA);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUser_new_alreadyExists() {
        final String newAssessorName = "tom baldwin";
        final String newAssessorEmail = "tom@poly.io";

        final Competition competition = newCompetition().build();

        final Category innovationArea = newCategory()
                .withName("machine learning")
                .withType(INNOVATION_AREA)
                .build();

        final NewUserStagedInviteResource newAssessor = newNewUserStagedInviteResource()
                .withEmail(newAssessorEmail)
                .withName(newAssessorName)
                .withCompetitionId(competition.getId())
                .withInnovationCategoryId(innovationArea.getId())
                .build();

        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(new CompetitionInvite());

        ServiceResult<CompetitionInviteResource> serviceResult = competitionInviteService.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(categoryRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUser_new_competitionNotFound() {
        final String newAssessorName = "tom baldwin";
        final String newAssessorEmail = "tom@poly.io";

        final long competitionId = 10L;

        final Category innovationArea = newCategory()
                .withName("machine learning")
                .withType(INNOVATION_AREA)
                .build();

        final NewUserStagedInviteResource newAssessor = newNewUserStagedInviteResource()
                .withEmail(newAssessorEmail)
                .withName(newAssessorName)
                .withCompetitionId(competitionId)
                .withInnovationCategoryId(innovationArea.getId())
                .build();

        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competitionId)).thenReturn(null);
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(null);

        ServiceResult<CompetitionInviteResource> serviceResult = competitionInviteService.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(categoryRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competitionId);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers() throws Exception {
        Competition competition = newCompetition().build();

        Category innovationArea = newCategory()
                .withName("machine learning")
                .withType(INNOVATION_AREA)
                .build();

        String testName1 = "Test Name A";
        String testName2 = "Test Name B";
        String testEmail1 = "test1@test.com";
        String testEmail2 = "test2@test.com";

        List<NewUserStagedInviteResource> newUserInvites = newNewUserStagedInviteResource()
                .withName(testName1, testName2)
                .withEmail(testEmail1, testEmail2)
                .withInnovationCategoryId(innovationArea.getId())
                .withCompetitionId(competition.getId())
                .build(2);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(anyString(), anyLong())).thenReturn(null);
        when(categoryRepositoryMock.findByIdAndType(innovationArea.getId(), INNOVATION_AREA)).thenReturn(innovationArea);
        when(competitionInviteRepositoryMock.save(any(CompetitionInvite.class))).thenReturn(new CompetitionInvite());

        ServiceResult<Void> serviceResult = competitionInviteService.inviteNewUsers(newUserInvites, competition.getId());

        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock, categoryRepositoryMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(categoryRepositoryMock).findByIdAndType(innovationArea.getId(), innovationArea.getType());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(testName1, testEmail1, CREATED, competition, innovationArea));
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verify(categoryRepositoryMock).findByIdAndType(innovationArea.getId(), innovationArea.getType());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(testName2, testEmail2, CREATED, competition, innovationArea));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_alreadyExists() throws Exception {
        Competition competition = newCompetition().build();

        Category innovationArea = newCategory()
                .withName("machine learning")
                .withType(INNOVATION_AREA)
                .build();

        String testName1 = "Test Name A";
        String testName2 = "Test Name B";
        String testEmail1 = "test1@test.com";
        String testEmail2 = "test2@test.com";

        List<NewUserStagedInviteResource> newUserInvites = newNewUserStagedInviteResource()
                .withName(testName1, testName2)
                .withEmail(testEmail1, testEmail2)
                .withInnovationCategoryId(innovationArea.getId())
                .withCompetitionId(competition.getId())
                .build(2);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(testEmail1, competition.getId()))
                .thenReturn(new CompetitionInvite());
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(testEmail2, competition.getId())).thenReturn(null);

        when(categoryRepositoryMock.findByIdAndType(innovationArea.getId(), INNOVATION_AREA)).thenReturn(innovationArea);
        when(competitionInviteRepositoryMock.save(any(CompetitionInvite.class))).thenReturn(new CompetitionInvite());

        ServiceResult<Void> serviceResult = competitionInviteService.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals("test1@test.com", serviceResult.getErrors().get(0).getFieldRejectedValue());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock, categoryRepositoryMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verify(categoryRepositoryMock).findByIdAndType(innovationArea.getId(), innovationArea.getType());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(testName2, testEmail2, CREATED, competition, innovationArea));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_competitionNotFound() throws Exception {
        long competitionId = 5L;

        Category innovationArea =  newCategory()
                .withName("machine learning")
                .withType(INNOVATION_AREA)
                .build();

        String testName1 = "Test Name A";
        String testName2 = "Test Name B";
        String testEmail1 = "test1@test.com";
        String testEmail2 = "test2@test.com";

        List<NewUserStagedInviteResource> newUserInvites = newNewUserStagedInviteResource()
                .withName(testName1, testName2)
                .withEmail(testEmail1, testEmail2)
                .withInnovationCategoryId(innovationArea.getId())
                .withCompetitionId(competitionId)
                .build(2);

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(null);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(anyString(), anyLong())).thenReturn(null);

        ServiceResult<Void> serviceResult = competitionInviteService.inviteNewUsers(newUserInvites, competitionId);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock, categoryRepositoryMock);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_categoryNotFound() throws Exception {
        Competition competition = newCompetition().build();
        long categoryId = 2L;

        String testName1 = "Test Name A";
        String testName2 = "Test Name B";
        String testEmail1 = "test1@test.com";
        String testEmail2 = "test2@test.com";

        List<NewUserStagedInviteResource> newUserInvites = newNewUserStagedInviteResource()
                .withName(testName1, testName2)
                .withEmail(testEmail1, testEmail2)
                .withInnovationCategoryId(categoryId)
                .withCompetitionId(competition.getId())
                .build(2);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(anyString(), anyLong())).thenReturn(null);
        when(categoryRepositoryMock.findByIdAndType(categoryId, INNOVATION_AREA)).thenReturn(null);
        when(competitionInviteRepositoryMock.save(any(CompetitionInvite.class))).thenReturn(new CompetitionInvite());

        ServiceResult<Void> serviceResult = competitionInviteService.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock, categoryRepositoryMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(categoryRepositoryMock).findByIdAndType(categoryId, INNOVATION_AREA);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verify(categoryRepositoryMock).findByIdAndType(categoryId, INNOVATION_AREA);

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

    private CompetitionInvite createInviteExpectations(String name, String email, InviteStatus status, Competition competition, Category innovationArea) {
        return createLambdaMatcher( invite -> {
                    assertEquals(name, invite.getName());
                    assertEquals(email, invite.getEmail());
                    assertEquals(status, invite.getStatus());
                    assertEquals(competition, invite.getTarget());
                    assertFalse(invite.getHash().isEmpty());
                    assertEquals(innovationArea, invite.getInnovationArea());
                }
        );
    }
}
