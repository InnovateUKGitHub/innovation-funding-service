package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.domain.CompetitionParticipantRole;
import com.worth.ifs.invite.domain.ParticipantStatus;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class CompetitionParticipantServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CompetitionParticipantService competitionParticipantService = new CompetitionParticipantServiceImpl();

    @Test
    public void getCompetitionParticipants() throws Exception {
        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);
        CompetitionParticipantResource expected = newCompetitionParticipantResource().build();
        when(competitionParticipantRepositoryMock.getByUserIdAndRoleAndStatus(1L, CompetitionParticipantRole.ASSESSOR, ParticipantStatus.PENDING)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(CompetitionParticipantRole.ASSESSOR);
        when(participantStatusMapperMock.mapToDomain(ParticipantStatusResource.PENDING)).thenReturn(ParticipantStatus.PENDING);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult = competitionParticipantService.getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.PENDING);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();
        assertSame(expected, found.get(0));
        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, competitionParticipantMapperMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRoleAndStatus(1L, CompetitionParticipantRole.ASSESSOR, ParticipantStatus.PENDING);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }
}