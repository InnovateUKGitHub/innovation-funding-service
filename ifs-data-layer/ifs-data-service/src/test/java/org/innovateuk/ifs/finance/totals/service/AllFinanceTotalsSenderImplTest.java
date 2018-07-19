package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.stream.Stream;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.resource.ApplicationState.submittedAndFinishedStates;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AllFinanceTotalsSenderImplTest {

    @Mock
    private ApplicationFinanceTotalsSender applicationFinanceTotalsSender;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private AllFinanceTotalsSenderImpl allFinanceTotalsSender;

    @Before
    public void setUp() throws Exception {
        allFinanceTotalsSender = new AllFinanceTotalsSenderImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendAllFinanceTotals() {
        Stream<Application> applicationsStream = newApplication()
                .withCompetition(newCompetition().withId(1L).build())
                .build(2)
                .stream();

        when(applicationRepository.findByApplicationProcessActivityStateIn(submittedAndFinishedStates))
                .thenReturn(applicationsStream);
        ServiceResult<Void> serviceResult = allFinanceTotalsSender.sendAllFinanceTotals();

        assertTrue(serviceResult.isSuccess());
        verify(applicationRepository, only()).findByApplicationProcessActivityStateIn(any());
        verify(applicationFinanceTotalsSender, times(2)).sendFinanceTotalsForApplication(any());
        verifyNoMoreInteractions(applicationRepository, applicationFinanceTotalsSender);
    }
}