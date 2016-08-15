package com.worth.ifs.application.transactional;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.transactional.FinanceRowService;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSummarisationServiceTest {

	private static final Long APPLICATION_ID = 123L;

	@InjectMocks
	private ApplicationSummarisationServiceImpl service;
	
	@Mock
	private FinanceRowService financeRowService;
	
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
		
		when(financeRowService.financeTotals(APPLICATION_ID)).thenReturn(afrs);
		
		ServiceResult<BigDecimal> result = service.getFundingSought(application);
		
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("6.00"), result.getSuccessObject());
	}
	
	@Test
	public void testFundingSoughtForApplicationWithNoFinances() {
		application.getApplicationFinances().clear();
		
		ServiceResult<BigDecimal> result = service.getFundingSought(application);
		
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("0.00"), result.getSuccessObject());
		verifyNoMoreInteractions(financeRowService);
	}
	
	@Test
	public void testTotalProjectCost() {
		
		ApplicationFinanceResource afr1 = mock(ApplicationFinanceResource.class);
		when(afr1.getTotal()).thenReturn(new BigDecimal("8.00"));
		ApplicationFinanceResource afr2 = mock(ApplicationFinanceResource.class);
		when(afr2.getTotal()).thenReturn(new BigDecimal("2.00"));
		
		ServiceResult<List<ApplicationFinanceResource>> afrs = serviceSuccess(Arrays.asList(afr1, afr2));
		
		when(financeRowService.financeTotals(APPLICATION_ID)).thenReturn(afrs);
		
		ServiceResult<BigDecimal> result = service.getTotalProjectCost(application);
		
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("10.00"), result.getSuccessObject());
	}
	
	@Test
	public void testTotalProjectCostForApplicationWithNoFinances() {
		application.getApplicationFinances().clear();
		
		ServiceResult<BigDecimal> result = service.getFundingSought(application);
		
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("0.00"), result.getSuccessObject());
		verifyNoMoreInteractions(financeRowService);
	}
}
