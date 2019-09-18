package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowService;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
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
}
