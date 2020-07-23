package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionApplicationConfig;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationProgressServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionStatusRepository questionStatusRepository;

    @InjectMocks
    private ApplicationProgressServiceImpl service;

    private static final Competition COMPETITION = newCompetition()
            .withMaxResearchRatio(30)
            .withCompetitionApplicationConfig(new CompetitionApplicationConfig())
            .build();
    private static final Application APPLICATION = newApplication()
            .withCompetition(COMPETITION)
            .build();

    @Before
    public void setup() {
        when(organisationRepository.countDistinctByProcessRolesApplicationId(APPLICATION.getId())).thenReturn(2L);
        when(questionRepository.countQuestionsWithMultipleStatuses(COMPETITION.getId())).thenReturn(1L);
        when(questionRepository.countQuestionsWithSingleStatus(COMPETITION.getId())).thenReturn(1L);
        when(applicationRepository.findById(APPLICATION.getId())).thenReturn(Optional.of(APPLICATION));
    }

    @Test
    public void getApplicationReadyToSubmit() {
        when(questionStatusRepository.countByApplicationIdAndMarkedAsCompleteTrue(APPLICATION.getId())).thenReturn(3L);
        when(applicationFinanceHandler.getResearchParticipationPercentage(APPLICATION.getId())).thenReturn(new BigDecimal("29"));

        boolean result = service.applicationReadyForSubmit(APPLICATION.getId());
        assertTrue(result);
    }

    @Test
    public void applicationNotReadyToSubmitResearchParticipationTooHigh() {
        when(questionStatusRepository.countByApplicationIdAndMarkedAsCompleteTrue(APPLICATION.getId())).thenReturn(3L);
        when(applicationFinanceHandler.getResearchParticipationPercentage(APPLICATION.getId())).thenReturn(new BigDecimal("31"));

        boolean result = service.applicationReadyForSubmit(APPLICATION.getId());
        assertFalse(result);
    }

    @Test
    public void applicationNotReadyToSubmitProgressNotComplete() {
        when(questionStatusRepository.countByApplicationIdAndMarkedAsCompleteTrue(APPLICATION.getId())).thenReturn(2L);
        when(applicationFinanceHandler.getResearchParticipationPercentage(APPLICATION.getId())).thenReturn(new BigDecimal("29"));

        boolean result = service.applicationReadyForSubmit(APPLICATION.getId());
        assertFalse(result);
    }
}