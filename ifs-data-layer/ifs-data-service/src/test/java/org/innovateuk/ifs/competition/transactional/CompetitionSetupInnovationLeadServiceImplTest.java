package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompetitionSetupInnovationLeadServiceImplTest extends BaseServiceUnitTest<CompetitionSetupInnovationLeadServiceImpl> {

    @Mock
    private InnovationLeadRepository innovationLeadRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private UserRepository userRepository;

    private long competitionId = 1L;

    @Override
    protected CompetitionSetupInnovationLeadServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupInnovationLeadServiceImpl();
    }

    @Test
    public void findInnovationLeads() {

        User innovationLead1 = newUser().withRoles(singleton(INNOVATION_LEAD)).build();
        User innovationLead2 = newUser().withRoles(singleton(INNOVATION_LEAD)).build();
        List<User> innovationLeads = asList(innovationLead1, innovationLead2);

        UserResource userResource1 = UserResourceBuilder.newUserResource().build();
        UserResource userResource2 = UserResourceBuilder.newUserResource().build();

        when(innovationLeadRepository.findAvailableInnovationLeadsNotAssignedToCompetition(competitionId)).thenReturn(innovationLeads);
        when(userMapper.mapToResource(innovationLead1)).thenReturn(userResource1);
        when(userMapper.mapToResource(innovationLead2)).thenReturn(userResource2);
        List<UserResource> result = service.findInnovationLeads(competitionId).getSuccess();

        assertEquals(2, result.size());
        assertEquals(userResource1, result.get(0));
    }

    @Test
    public void addInnovationLeadWhenCompetitionNotFound() {
        Long innovationLeadUserId = 2L;

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());
        ServiceResult<Void> result = service.addInnovationLead(competitionId, innovationLeadUserId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Competition.class, competitionId)));
    }

    @Test
    public void addInnovationLead() {
        long innovationLeadUserId = 2L;

        Competition competition = newCompetition().build();
        User innovationLead = newUser().build();
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(userRepository.findById(innovationLeadUserId)).thenReturn(Optional.of(innovationLead));
        ServiceResult<Void> result = service.addInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isSuccess());

        InnovationLead savedCompetitionParticipant = new InnovationLead(competition, innovationLead);

        verify(innovationLeadRepository).save(savedCompetitionParticipant);
    }

    @Test
    public void removeInnovationLeadWhenCompetitionParticipantNotFound() {
        long innovationLeadUserId = 2L;

        when(innovationLeadRepository.findInnovationLead(competitionId, innovationLeadUserId)).thenReturn(null);
        ServiceResult<Void> result = service.removeInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(InnovationLead.class, competitionId,
                innovationLeadUserId)));
    }

    @Test
    public void removeInnovationLead() {
        long innovationLeadUserId = 2L;

        InnovationLead innovationLead = newInnovationLead().build();
        when(innovationLeadRepository.findInnovationLead(competitionId, innovationLeadUserId)).thenReturn
                (innovationLead);

        ServiceResult<Void> result = service.removeInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isSuccess());

        verify(innovationLeadRepository).delete(innovationLead);
    }
}