package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationExpressionOfInterestConfig;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.transactional.ApplicationProgressService;
import org.innovateuk.ifs.application.transactional.ApplicationProgressServiceImpl;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
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
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationProgressServiceImplTest {


    @InjectMocks
    private ApplicationProgressService agreementService = new ApplicationProgressServiceImpl();

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionStatusRepository questionStatusRepository;

    Competition competition = newCompetition().withName("Technology Inspired").build();
    Application application = newApplication()
            .withCompetition(competition)
            .build();
    ApplicationExpressionOfInterestConfig applicationExpressionOfInterestConfig =
            ApplicationExpressionOfInterestConfig.builder().
                    application(application).enabledForExpressionOfInterest(true).build();

    @Before
    public void setup() {
        application.setApplicationExpressionOfInterestConfig(applicationExpressionOfInterestConfig);
        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(organisationRepository.countDistinctByProcessRolesApplicationId(application.getId())).thenReturn(1L);
        when(questionRepository.countPreRegQuestionsWithMultipleStatuses(competition.getId())).thenReturn(10L);
        when(questionRepository.countPreRegQuestionsWithSingleStatus(competition.getId())).thenReturn(5L);
        when(questionStatusRepository.countByApplicationIdAndMarkedAsCompleteTrue(application.getId())).thenReturn(5L);


    }

    @Test
    public void getApplicationProgress() {
        ServiceResult<BigDecimal> result = agreementService.getApplicationProgress(application.getId());
        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), new BigDecimal("33.33"));
    }

    @Test
    public void updateApplicationProgress() {
        long appId = 2L;
        ServiceResult<BigDecimal> result = agreementService.updateApplicationProgress(application.getId());
        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), new BigDecimal("33.33"));
    }


}
