package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowService;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherCostBuilder.newOtherCost;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.finance.resource.category.BaseOtherFundingCostCategory.OTHER_FUNDING;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationSummarisationServiceTest {

	private static final Long APPLICATION_ID = 123L;

	@InjectMocks
	private ApplicationSummarisationServiceImpl service;

	@Mock
	private ApplicationFinanceRowService financeRowCostsService;

	@Mock
	private ApplicationFinanceService financeService;

	private Application application;

	@Before
	public void setUp() {
		application = new Application();
		application.setId(APPLICATION_ID);
		application.getApplicationFinances().add(new ApplicationFinance());
	}

	@Test
	public void testFundingSought() {

		ApplicationFinanceResource afr1 = mock(ApplicationFinanceResource.class);
		when(afr1.getTotalFundingSought()).thenReturn(new BigDecimal("4.00"));
		ApplicationFinanceResource afr2 = mock(ApplicationFinanceResource.class);
		when(afr2.getTotalFundingSought()).thenReturn(new BigDecimal("2.00"));

		ServiceResult<List<ApplicationFinanceResource>> afrs = serviceSuccess(Arrays.asList(afr1, afr2));

		when(financeService.financeTotals(APPLICATION_ID)).thenReturn(afrs);

		ServiceResult<BigDecimal> result = service.getFundingSought(application);

		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("6.00"), result.getSuccess());
	}

	@Test
	public void testFundingSoughtForApplicationWithNoFinances() {
		application.getApplicationFinances().clear();

		ServiceResult<BigDecimal> result = service.getFundingSought(application);

		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("0.00"), result.getSuccess());
		verifyNoMoreInteractions(financeRowCostsService);
	}

	@Test
	public void testTotalProjectCost() {

		ApplicationFinanceResource afr1 = mock(ApplicationFinanceResource.class);
		when(afr1.getTotal()).thenReturn(new BigDecimal("8.00"));
		ApplicationFinanceResource afr2 = mock(ApplicationFinanceResource.class);
		when(afr2.getTotal()).thenReturn(new BigDecimal("2.00"));

		ServiceResult<List<ApplicationFinanceResource>> afrs = serviceSuccess(Arrays.asList(afr1, afr2));

		when(financeService.financeTotals(APPLICATION_ID)).thenReturn(afrs);

		ServiceResult<BigDecimal> result = service.getTotalProjectCost(application);

		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("10.00"), result.getSuccess());
	}

	@Test
	public void testTotalProjectCostForApplicationWithNoFinances() {
		application.getApplicationFinances().clear();

		ServiceResult<BigDecimal> result = service.getFundingSought(application);

		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("0.00"), result.getSuccess());
		verifyNoMoreInteractions(financeRowCostsService);
	}
    @Test
    public void testProjectTotalCost() {

        OtherCost otherCost = newOtherCost()
                .withCost(BigDecimal.valueOf(1000)).build();
        DefaultCostCategory otherCostCategory = newDefaultCostCategory().withCosts(Arrays.asList(otherCost)).build();
        otherCostCategory.calculateTotal();

        Materials materialCost = newMaterials()
                .withCost(BigDecimal.valueOf(500))
                .withQuantity(10).build();
        DefaultCostCategory materialCostCategory = newDefaultCostCategory().withCosts(Arrays.asList(materialCost)).build();
        materialCostCategory.calculateTotal();

        TravelCost travelCost = new TravelCost(1L, "transport", new BigDecimal("25.00"), 5, 1L);
        DefaultCostCategory travelCostCategory = newDefaultCostCategory().withCosts(Arrays.asList(travelCost)).build();
        travelCostCategory.calculateTotal();


        SubContractingCost subContractingCost = new SubContractingCost(188l);
        subContractingCost.setName("Tom Bloggs");
        subContractingCost.setCountry("UK");
        subContractingCost.setRole("Business Analyst");
        subContractingCost.setCost(new BigDecimal("10000"));
        DefaultCostCategory subContractingCostCategory = newDefaultCostCategory().withCosts(Arrays.asList(subContractingCost)).build();
        subContractingCostCategory.calculateTotal();

        List<LabourCost> labourCost = newLabourCost().
                withGrossEmployeeCost(new BigDecimal("10000.23"), new BigDecimal("5100.11"), BigDecimal.ZERO).
                withDescription("Developers", "Testers", WORKING_DAYS_PER_YEAR).
                withLabourDays(100, 120, 250).
                build(3);
        DefaultCostCategory labourCostCostCategory = newDefaultCostCategory().withCosts(labourCost).build();
        labourCostCostCategory.calculateTotal();


        Map<FinanceRowType, FinanceRowCostCategory> costs = MapFunctions.asMap(
                FinanceRowType.LABOUR, labourCostCostCategory,
                FinanceRowType.OTHER_COSTS, otherCostCategory,
                FinanceRowType.MATERIALS, materialCostCategory,
                FinanceRowType.TRAVEL, travelCostCategory,
                FinanceRowType.MATERIALS, travelCostCategory,
                FinanceRowType.SUBCONTRACTING_COSTS, subContractingCostCategory
        );


        ApplicationFinanceResource afr1 = new ApplicationFinanceResource();
        afr1.setFinanceOrganisationDetails(costs);

        when(financeService.financeTotals(APPLICATION_ID)).thenReturn(serviceSuccess(Arrays.asList(afr1)));

        ServiceResult<BigDecimal> result = service.getProjectTotalFunding(APPLICATION_ID);
        assertTrue(result.isSuccess());
        assertEquals(new BigDecimal("11250"), result.getSuccess());
    }

    @Test
    public void testProjectOtherCost() {


        OtherFundingCostCategory otherFundingCostCategory = newOtherFundingCostCategory().withCosts(
                        newOtherFunding().
                                withOtherPublicFunding("Yes", "").
                                withFundingSource(OTHER_FUNDING, "other funding").
                                withFundingAmount(null, BigDecimal.valueOf(225)).
                                build(2)).
                build();
        otherFundingCostCategory.calculateTotal();

        Map<FinanceRowType, FinanceRowCostCategory> costs = MapFunctions.asMap(
                FinanceRowType.OTHER_FUNDING, otherFundingCostCategory
        );


        ApplicationFinanceResource afr1 = new ApplicationFinanceResource();
        afr1.setFinanceOrganisationDetails(costs);

        when(financeService.financeTotals(APPLICATION_ID)).thenReturn(serviceSuccess(Arrays.asList(afr1)));

        ServiceResult<BigDecimal> result = service.getProjectOtherFunding(APPLICATION_ID);
        assertTrue(result.isSuccess());
        assertEquals(new BigDecimal("225"), result.getSuccess());
    }
}
