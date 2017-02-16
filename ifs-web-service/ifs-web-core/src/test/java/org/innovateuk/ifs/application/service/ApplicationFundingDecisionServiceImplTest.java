package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.error.exception.GeneralUnexpectedErrorException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFundingDecisionServiceImplTest {
	@InjectMocks
	private ApplicationFundingDecisionServiceImpl service;
	
	@Mock
	private ApplicationFundingDecisionRestService applicationFundingDecisionRestService;
	
	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void testSaveFundingDecisionData() {
		Long competitionId = 123L;
		Map<Long, FundingDecision> applicationIdToFundingDecision = mock(Map.class);
		
		when(applicationFundingDecisionRestService.saveApplicationFundingDecisionData(competitionId, applicationIdToFundingDecision)).thenReturn(restSuccess());

		//service.saveApplicationFundingDecisionData(competitionId, applicationIdToFundingDecision);
		
		verify(applicationFundingDecisionRestService).saveApplicationFundingDecisionData(competitionId,  applicationIdToFundingDecision);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = GeneralUnexpectedErrorException.class)
	@Ignore
	public void testErrorSavingFundingDecisionData() {
		Long competitionId = 123L;
		String fundingDecision = FundingDecision.FUNDED.name();
		List<Long> applicationIds = new ArrayList<>();
		applicationIds.add(8L);
		applicationIds.add(9L);
		applicationIds.add(10L);

		Map<Long, FundingDecision> applicationIdToFundingDecision = mock(Map.class);
		
		when(applicationFundingDecisionRestService.saveApplicationFundingDecisionData(competitionId, applicationIdToFundingDecision)).thenReturn(restFailure(CommonErrors.internalServerErrorError()));
		service.saveApplicationFundingDecisionData(competitionId, fundingDecision, applicationIds);
	}
	
	@Test
	@Ignore
	public void testFundingDecisionForStringFunded() {
		FundingDecision result = service.fundingDecisionForString("Y");
		assertEquals(FundingDecision.FUNDED, result);
	}
	
	@Test
	@Ignore
	public void testFundingDecisionForStringUnfunded() {
		FundingDecision result = service.fundingDecisionForString("N");
		assertEquals(FundingDecision.UNFUNDED, result);
	}
	
	@Test
	@Ignore
	public void testFundingDecisionForStringUndecided() {
		FundingDecision result = service.fundingDecisionForString("-");
		assertEquals(FundingDecision.UNDECIDED, result);
	}
	
	private String[] val(String val) {
		return new String[]{val};
	}
}
