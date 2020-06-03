package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompetitionSetupPostAwardServiceServiceImplTest extends BaseServiceUnitTest<CompetitionSetupPostAwardServiceServiceImpl> {

    @Mock
    private CompetitionRepository competitionRepository;

    @Override
    protected CompetitionSetupPostAwardServiceServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupPostAwardServiceServiceImpl();
    }

    @Test
    public void configurePostAwardService() {
        // given
        long competitionId = 1L;
        Competition competition = newCompetition().withId(competitionId).withPostAwardService(PostAwardService.IFS_POST_AWARD).build();

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));

        // when
        ServiceResult<Void> result = service.configurePostAwardService(competitionId, PostAwardService.CONNECT);

        // then
        assertTrue(result.isSuccess());
        assertEquals(PostAwardService.CONNECT, competition.getPostAwardService());
        verify(competitionRepository).save(competition);
    }
}
