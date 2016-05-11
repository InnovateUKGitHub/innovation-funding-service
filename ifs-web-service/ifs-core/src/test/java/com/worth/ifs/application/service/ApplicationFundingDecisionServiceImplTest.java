package com.worth.ifs.application.service;

import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.error.exception.GeneralUnexpectedErrorException;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFundingDecisionServiceImplTest {
	@InjectMocks
	private ApplicationFundingDecisionServiceImpl service;
	
	@Mock
	private ApplicationFundingDecisionRestService applicationFundingDecisionRestService;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMakeFundingDecision() {
		Long competitionId = 123L;
		Map<Long, FundingDecision> applicationIdToFundingDecision = mock(Map.class);
		
		when(applicationFundingDecisionRestService.makeApplicationFundingDecision(competitionId, applicationIdToFundingDecision)).thenReturn(restSuccess());

		service.makeApplicationFundingDecision(competitionId, applicationIdToFundingDecision);
		
		verify(applicationFundingDecisionRestService).makeApplicationFundingDecision(competitionId,  applicationIdToFundingDecision);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = GeneralUnexpectedErrorException.class)
	public void testErrorMakingFundingDecision() {
		Long competitionId = 123L;
		Map<Long, FundingDecision> applicationIdToFundingDecision = mock(Map.class);
		
		when(applicationFundingDecisionRestService.makeApplicationFundingDecision(competitionId, applicationIdToFundingDecision)).thenReturn(restFailure(CommonErrors.internalServerErrorError()));
		service.makeApplicationFundingDecision(competitionId, applicationIdToFundingDecision);
	}
}
