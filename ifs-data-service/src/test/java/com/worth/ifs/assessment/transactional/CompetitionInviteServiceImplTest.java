package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.domain.ParticipantStatus;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import static com.worth.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class CompetitionInviteServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CompetitionInviteService competitionInviteService = new CompetitionInviteServiceImpl();

    private CompetitionParticipant competitionParticipant;

    @Before
    public void setUp() {
        Competition competition = newCompetition().withName("my competition").build();
        CompetitionInvite competitionInvite = newCompetitionInvite().withCompetition(competition).build();
        competitionParticipant = new CompetitionParticipant(competition, competitionInvite);
        CompetitionInviteResource expected = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRepositoryMock.getByHash("inviteHash")).thenReturn(competitionInvite);

        when(competitionInviteRepositoryMock.save(same(competitionInvite))).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(same(competitionInvite))).thenReturn(expected);

        when(competitionParticipantRepositoryMock.getByInviteHash("inviteHash")).thenReturn(competitionParticipant);
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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHashNotExists");
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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock, calls(1)).getByHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    // accept

    @Test
    public void acceptInvite() {
        competitionInviteService.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        ServiceResult<Void> serviceResult = competitionInviteService.acceptInvite("inviteHash");

        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.ACCEPTED, competitionParticipant.getStatus());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).save(competitionParticipant);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_hashNotExists() {

    }

    @Test
    public void acceptInvite_notOpened() {
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        ServiceResult<Void> serviceResult = competitionInviteService.acceptInvite("inviteHash");

        assertTrue(serviceResult.isFailure());
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");

        inOrder.verifyNoMoreInteractions();
    }


    @Test
    public void acceptInvite_alreadyAccepted() {
    }

    @Test
    public void acceptInvite_alreadyRejected() {
    }

    // reject

    @Test
    public void rejectInvite() {

    }

    @Test
    public void rejectInvite_hashNotExists() {
    }

    @Test
    public void rejectInvite_notOpened() {
    }

    @Test
    public void rejectInvite_alreadyAccepted() {
    }

    @Test
    public void rejectInvite_alreadyRejected() {
    }


}