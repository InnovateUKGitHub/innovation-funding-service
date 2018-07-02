package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompetitionFinanceTotalsSenderImplTest {

    @Mock
    private ApplicationFinanceTotalsSender applicationFinanceTotalsSender;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private CompetitionFinanceTotalsSender competitionFinanceTotalsSender;

    @Before
    public void setUp() throws Exception {
        competitionFinanceTotalsSender = new CompetitionFinanceTotalsSenderImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendFinanceTotalsForCompetitionId() {
        Map<FinanceRowType, FinanceRowCostCategory> costs = new HashMap<>();
        FinanceRowCostCategory financeRowCostCategory = newDefaultCostCategory().build();
        costs.put(FinanceRowType.LABOUR, financeRowCostCategory);

        List<Application> applicationsCompsStream = newApplication()
                .withId(1L)
                .withCompetition(newCompetition().withId(1L).build())
                .build(6);

        when(applicationService.getApplicationsByCompetitionIdAndState(any(),any())).thenReturn(ServiceResult.serviceSuccess(applicationsCompsStream));
        ServiceResult<Void> serviceResult = competitionFinanceTotalsSender.sendFinanceTotalsForCompetition(1L);

        assertTrue(serviceResult.isSuccess());
        verify(applicationFinanceTotalsSender, times(6)).sendFinanceTotalsForApplication(1L);
    }
}