package org.innovateuk.ifs.finance.sync.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.sync.FinanceType;
import org.innovateuk.ifs.finance.sync.filter.SpendProfileCostFilter;
import org.innovateuk.ifs.finance.sync.mapper.FinanceCostTotalResourceMapper;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ApplicationFinanceTotalsSenderImplTest {

    @Mock
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Mock
    private FinanceCostTotalResourceMapper financeCostTotalResourceMapper;

    @Mock
    private MessageQueueServiceStub messageQueueServiceStub;

    @Mock
    private SpendProfileCostFilter spendProfileCostFilter;

    @InjectMocks
    private ApplicationFinanceTotalsSender applicationFinanceTotalsSender;

    @Before
    public void setUp() throws Exception {
        applicationFinanceTotalsSender = new ApplicationFinanceTotalsSenderImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendFinanceTotalsForApplication() {
        Long applicationId = 1L;

        FinanceRowType financeRowType = FinanceRowType.LABOUR;
        FinanceRowCostCategory financeRowCostCategory = newDefaultCostCategory().build();

        Map<FinanceRowType, FinanceRowCostCategory> costs = MapFunctions.asMap(financeRowType, financeRowCostCategory);

        List<ApplicationFinanceResource> applicationFinanceResource = newApplicationFinanceResource()
                .withId(1L)
                .withFinanceOrganisationDetails(costs)
                .build(1);

        List<FinanceCostTotalResource> expectedFinanceCostTotalResource = newFinanceCostTotalResource()
                .withName(financeRowType.getType())
                .withFinanceId(1L)
                .withTotal(new BigDecimal(10000))
                .withType(FinanceType.APPLICATION).build(1);

        List<FinanceCostTotalResource> expectedFilteredFinanceCostTotalResource = newFinanceCostTotalResource()
                .withName(FinanceRowType.LABOUR.getType())
                .withFinanceId(1L)
                .withTotal(new BigDecimal(10000))
                .withType(FinanceType.APPLICATION).build(1);

        when(messageQueueServiceStub.sendFinanceTotals(any())).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFinanceHandler.getApplicationFinances(applicationId)).thenReturn(applicationFinanceResource);
        when(financeCostTotalResourceMapper.mapFromApplicationFinanceResourceListToList(any())).thenReturn(expectedFinanceCostTotalResource);
        when(spendProfileCostFilter.filterBySpendProfile(expectedFinanceCostTotalResource)).thenReturn(expectedFilteredFinanceCostTotalResource);
        ServiceResult<Void> serviceResult = applicationFinanceTotalsSender.sendFinanceTotalsForApplication(applicationId);


        assertTrue(serviceResult.isSuccess());
        verify(messageQueueServiceStub, times(1)).sendFinanceTotals(expectedFilteredFinanceCostTotalResource);
    }
}