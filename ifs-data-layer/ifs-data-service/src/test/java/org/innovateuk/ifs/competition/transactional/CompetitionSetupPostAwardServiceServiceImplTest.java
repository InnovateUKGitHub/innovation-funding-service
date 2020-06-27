package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.grant.builder.GrantProcessConfigurationBuilder;
import org.innovateuk.ifs.grant.domain.GrantProcessConfiguration;
import org.innovateuk.ifs.grant.repository.GrantProcessConfigurationRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.grant.builder.GrantProcessConfigurationBuilder.newGrantProcessConfiguration;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompetitionSetupPostAwardServiceServiceImplTest extends BaseServiceUnitTest<CompetitionSetupPostAwardServiceServiceImpl> {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private GrantProcessConfigurationRepository grantProcessConfigurationRepository;

    @Override
    protected CompetitionSetupPostAwardServiceServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupPostAwardServiceServiceImpl();
    }

    @Test
    public void configurePostAwardServiceWasSetToSendByDefault() {
        // given
        Long competitionId = 1L;
        Competition competition = newCompetition().withId(competitionId).build();
        GrantProcessConfiguration grantProcessConfiguration = newGrantProcessConfiguration().withCompetition(competition).withSendByDefault(true).build();

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(grantProcessConfigurationRepository.findByCompetitionId(competitionId)).thenReturn(Optional.of(grantProcessConfiguration));

        // when
        ServiceResult<Void> result = service.configurePostAwardService(competitionId, PostAwardService.CONNECT);

        // then
        assertTrue(result.isSuccess());
        assertFalse(grantProcessConfiguration.isSendByDefault());
        verify(grantProcessConfigurationRepository).save(grantProcessConfiguration);
    }

    @Test
    public void configurePostAwardServiceWasSetToNotSendByDefault() {
        // given
        Long competitionId = 1L;
        Competition competition = newCompetition().withId(competitionId).build();
        GrantProcessConfiguration grantProcessConfiguration = newGrantProcessConfiguration().withCompetition(competition).withSendByDefault(false).build();

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(grantProcessConfigurationRepository.findByCompetitionId(competitionId)).thenReturn(Optional.of(grantProcessConfiguration));

        // when
        ServiceResult<Void> result = service.configurePostAwardService(competitionId, PostAwardService.IFS_POST_AWARD);

        // then
        assertTrue(result.isSuccess());
        assertTrue(grantProcessConfiguration.isSendByDefault());
        verify(grantProcessConfigurationRepository).save(grantProcessConfiguration);
    }

    @Test
    public void configurePostAwardServiceWasNoConfigPresent() {
        // given
        Long competitionId = 1L;
        Competition competition = newCompetition().withId(competitionId).build();

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(grantProcessConfigurationRepository.findByCompetitionId(competitionId)).thenReturn(Optional.empty());

        // when
        ServiceResult<Void> result = service.configurePostAwardService(competitionId, PostAwardService.IFS_POST_AWARD);

        // then
        assertTrue(result.isSuccess());
        verify(grantProcessConfigurationRepository).save(argThat(config -> config.isSendByDefault() == true));
    }

    @Test
    public void getPostAwardServiceWasSetToSendByDefault() {
        // given
        Long competitionId = 1L;
        Competition competition = newCompetition().withId(competitionId).build();
        GrantProcessConfiguration grantProcessConfiguration = newGrantProcessConfiguration().withCompetition(competition).withSendByDefault(true).build();

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(grantProcessConfigurationRepository.findByCompetitionId(competitionId)).thenReturn(Optional.of(grantProcessConfiguration));

        // when
        ServiceResult<CompetitionPostAwardServiceResource> result = service.getPostAwardService(competitionId);

        // then
        assertTrue(result.isSuccess());
        assertEquals(competitionId, result.getSuccess().getCompetitionId());
        assertEquals(PostAwardService.IFS_POST_AWARD, result.getSuccess().getPostAwardService());
    }

    @Test
    public void getPostAwardServiceWasSetToNotSendByDefault() {
        // given
        Long competitionId = 1L;
        Competition competition = newCompetition().withId(competitionId).build();
        GrantProcessConfiguration grantProcessConfiguration = newGrantProcessConfiguration().withCompetition(competition).withSendByDefault(false).build();

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(grantProcessConfigurationRepository.findByCompetitionId(competitionId)).thenReturn(Optional.of(grantProcessConfiguration));

        // when
        ServiceResult<CompetitionPostAwardServiceResource> result = service.getPostAwardService(competitionId);

        // then
        assertTrue(result.isSuccess());
        assertEquals(competitionId, result.getSuccess().getCompetitionId());
        assertEquals(PostAwardService.CONNECT, result.getSuccess().getPostAwardService());
    }

    @Test
    public void getPostAwardServiceWasNoConfigPresent() {
        // given
        Long competitionId = 1L;
        Competition competition = newCompetition().withId(competitionId).build();

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(grantProcessConfigurationRepository.findByCompetitionId(competitionId)).thenReturn(Optional.empty());

        // when
        ServiceResult<CompetitionPostAwardServiceResource> result = service.getPostAwardService(competitionId);

        // then
        assertTrue(result.isSuccess());
        assertEquals(competitionId, result.getSuccess().getCompetitionId());
        assertEquals(PostAwardService.CONNECT, result.getSuccess().getPostAwardService());
    }
}
