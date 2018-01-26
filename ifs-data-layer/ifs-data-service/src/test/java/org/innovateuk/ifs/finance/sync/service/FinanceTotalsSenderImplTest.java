package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.sync.FinanceType;
import org.innovateuk.ifs.finance.sync.mapper.FinanceCostTotalResourceMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FinanceTotalsSenderImplTest {

    @Mock
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Mock
    private FinanceCostTotalResourceMapper financeCostTotalResourceMapper;

    @Mock
    private MessageQueueServiceStub messageQueueServiceStub;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private FinanceTotalsSender financeTotalsSender;

    @Before
    public void setUp() throws Exception {
        financeTotalsSender = new FinanceTotalsSenderImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendFinanceTotalsForApplication() {
        Long applicationId = 1L;

        Map<FinanceRowType, FinanceRowCostCategory> costs = new HashMap<>();
        FinanceRowType financeRowType = FinanceRowType.LABOUR;
        FinanceRowCostCategory financeRowCostCategory = newDefaultCostCategory().build();
        costs.put(financeRowType, financeRowCostCategory);

        List<ApplicationFinanceResource> applicationFinanceResource = newApplicationFinanceResource()
                .withId(1L)
                .withFinanceOrganisationDetails(costs)
                .build(1);


        List<FinanceCostTotalResource> expectedFinanceCostTotalResource = newFinanceCostTotalResource()
                .withName(financeRowType.getType())
                .withFinanceId(1L)
                .withTotal(new BigDecimal(10000))
                .withType(FinanceType.APPLICATION).build(1);

        when(messageQueueServiceStub.sendFinanceTotals(any())).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFinanceHandler.getApplicationFinances(applicationId)).thenReturn(applicationFinanceResource);
        when(financeCostTotalResourceMapper.mapFromApplicationFinanceResourceListToList(any())).thenReturn(expectedFinanceCostTotalResource);
        ServiceResult<Void> serviceResult = financeTotalsSender.sendFinanceTotalsForApplication(applicationId);


        assertTrue(serviceResult.isSuccess());
        verify(messageQueueServiceStub, times(1)).sendFinanceTotals(expectedFinanceCostTotalResource);
    }

    @Test
    public void sendFinanceTotalsForCompetition() {
        Long competitionId = 1L;

        Map<FinanceRowType, FinanceRowCostCategory> costs = new HashMap<>();
        FinanceRowType financeRowType = FinanceRowType.LABOUR;
        FinanceRowCostCategory financeRowCostCategory = newDefaultCostCategory().build();
        costs.put(financeRowType, financeRowCostCategory);

        List<Application> applications = newApplication().build(3);

        List<ApplicationFinanceResource> applicationFinanceResource = newApplicationFinanceResource()
                .withId(1L)
                .withFinanceOrganisationDetails(costs)
                .build(1);

        List<FinanceCostTotalResource> expectedFinanceCostTotalResource = newFinanceCostTotalResource()
                .withName(financeRowType.getType())
                .build(1);

        when(applicationService.getApplicationsByCompetitionIdAndState(any(), any())).thenReturn(
                ServiceResult.serviceSuccess(
                        applications
                ));
        when(messageQueueServiceStub.sendFinanceTotals(any())).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFinanceHandler.getApplicationFinances(any())).thenReturn(applicationFinanceResource);
        when(financeCostTotalResourceMapper.mapFromApplicationFinanceResourceListToList(any())).thenReturn(expectedFinanceCostTotalResource);
        ServiceResult<Void> serviceResult = financeTotalsSender.sendFinanceTotalsForCompetition(competitionId);


        assertTrue(serviceResult.isSuccess());
        verify(messageQueueServiceStub, times(3)).sendFinanceTotals(expectedFinanceCostTotalResource);
    }

    @Test
    public void sendAllFinanceTotals() {

        Map<FinanceRowType, FinanceRowCostCategory> costs = new HashMap<>();
        FinanceRowType financeRowType = FinanceRowType.LABOUR;
        FinanceRowCostCategory financeRowCostCategory = newDefaultCostCategory().build();
        costs.put(financeRowType, financeRowCostCategory);

        List<Application> applicationsComp1 = newApplication().withCompetition(newCompetition().withId(1L).build()).build(3);
        List<Application> applicationsComp2 = newApplication().withCompetition(newCompetition().withId(2L).build()).build(3);
        applicationsComp1.addAll(applicationsComp1);

        List<ApplicationFinanceResource> applicationFinanceResource = newApplicationFinanceResource()
                .withId(1L)
                .withFinanceOrganisationDetails(costs)
                .build(1);

        List<FinanceCostTotalResource> expectedFinanceCostTotalResource = newFinanceCostTotalResource()
                .withName(financeRowType.getType())
                .build(1);

        when(applicationService.getApplicationsByState(any())).thenReturn(ServiceResult.serviceSuccess(applicationsComp1));

        when(applicationService.getApplicationsByCompetitionIdAndState(1L, ApplicationState.wasSubmittedStates())).thenReturn(
                ServiceResult.serviceSuccess(
                        applicationsComp1
                ));

        when(messageQueueServiceStub.sendFinanceTotals(any())).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFinanceHandler.getApplicationFinances(any())).thenReturn(applicationFinanceResource);
        when(financeCostTotalResourceMapper.mapFromApplicationFinanceResourceListToList(any())).thenReturn(expectedFinanceCostTotalResource);
        ServiceResult<Void> serviceResult = financeTotalsSender.sendAllFinanceTotals();


        assertTrue(serviceResult.isSuccess());
        verify(messageQueueServiceStub, times(6)).sendFinanceTotals(expectedFinanceCostTotalResource);
    }
}