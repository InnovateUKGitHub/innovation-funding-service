package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFundingDecisionServiceImplTest extends BaseServiceUnitTest<ApplicationFundingDecisionServiceImpl> {
	@Mock
	private ApplicationFundingDecisionRestService applicationFundingDecisionRestService;

	@Mock
	private ApplicationSummaryService applicationSummaryService;

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	protected ApplicationFundingDecisionServiceImpl supplyServiceUnderTest() {
		return new ApplicationFundingDecisionServiceImpl();
	}

	@Test
	public void saveFundingDecision() throws Exception {
		String fundingDecisionChoice = "ON_HOLD";

		List<Long> applicationIds = new ArrayList<>();
		applicationIds.add(8L);
		applicationIds.add(9L);

		ApplicationSummaryPageResource applicationSummaryPageResource = new ApplicationSummaryPageResource();
		List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(8L,9L).build(4);
		applicationSummaryPageResource.setContent(applicationSummaryResources);

		when(applicationFundingDecisionRestService.saveApplicationFundingDecisionData(any(), any())).thenReturn(restSuccess());
		when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(any(), any(), any(), any(),any())).thenReturn(applicationSummaryPageResource);

		service.saveApplicationFundingDecisionData(1L, fundingDecisionChoice, applicationIds);

		Map<Long, FundingDecision> expectedDecisionMap = new HashMap<>();
		expectedDecisionMap.put(8L, FundingDecision.ON_HOLD);
		expectedDecisionMap.put(9L, FundingDecision.ON_HOLD);

		verify(applicationFundingDecisionRestService).saveApplicationFundingDecisionData(1L, expectedDecisionMap);
		verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(any(),any(),any(),any(),any());
	}

	@Test
	public void saveFundingDecision_undecidedFundingDecisionChoice() throws Exception {
		String fundingDecisionChoice = "UNDECIDED";

		List<Long> applicationIds = new ArrayList<>();
		applicationIds.add(8L);
		applicationIds.add(9L);

		ApplicationSummaryPageResource applicationSummaryPageResource = new ApplicationSummaryPageResource();
		List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(8L,9L).build(2);
		applicationSummaryPageResource.setContent(applicationSummaryResources);

		ServiceResult<Void> result = service.saveApplicationFundingDecisionData(1L, fundingDecisionChoice, applicationIds);

		assertTrue(result.isFailure());

		verifyZeroInteractions(applicationFundingDecisionRestService);
		verifyZeroInteractions(applicationSummaryService);
	}

	@Test
	public void saveFundingDecision_disallowedFundingDecisionChoice() throws Exception {
		String fundingDecisionChoice = "UNLISTED_CHOICE";

		List<Long> applicationIds = new ArrayList<>();
		applicationIds.add(8L);
		applicationIds.add(9L);

		ApplicationSummaryPageResource applicationSummaryPageResource = new ApplicationSummaryPageResource();
		List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(8L,9L).build(4);
		applicationSummaryPageResource.setContent(applicationSummaryResources);

		ServiceResult<Void> result = service.saveApplicationFundingDecisionData(1L, fundingDecisionChoice, applicationIds);

		assertTrue(result.isFailure());

		verifyZeroInteractions(applicationFundingDecisionRestService);
		verifyZeroInteractions(applicationSummaryService);
	}

	@Test
	public void saveFundingDecision_unsubmittedApplicationsShouldNotReceiveFundingDecision() throws Exception {
		String fundingDecisionChoice = "ON_HOLD";

		List<Long> applicationIds = new ArrayList<>();
		applicationIds.add(8L);
		applicationIds.add(9L);

		ApplicationSummaryPageResource applicationSummaryPageResource = new ApplicationSummaryPageResource();
		List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(10L,11L).build(2);
		applicationSummaryPageResource.setContent(applicationSummaryResources);

		when(applicationFundingDecisionRestService.saveApplicationFundingDecisionData(any(), any())).thenReturn(restSuccess());
		when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(any(), any(), any(), any(),any())).thenReturn(applicationSummaryPageResource);

		service.saveApplicationFundingDecisionData(1L, fundingDecisionChoice, applicationIds);

		Map<Long, FundingDecision> expectedDecisionMap = new HashMap<>();

		verify(applicationFundingDecisionRestService).saveApplicationFundingDecisionData(1L, expectedDecisionMap);
		verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(any(),any(),any(),any(),any());
	}

	@Test
	public void saveFundingDecision_onlySubmittedApplicationsShouldNotReceiveFundingDecision() throws Exception {
		String fundingDecisionChoice = "ON_HOLD";

		List<Long> applicationIds = new ArrayList<>();
		applicationIds.add(8L);
		applicationIds.add(9L);

		ApplicationSummaryPageResource applicationSummaryPageResource = new ApplicationSummaryPageResource();
		List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(1L, 2L, 7L, 8L, 9L).build(5);
		applicationSummaryPageResource.setContent(applicationSummaryResources);

		when(applicationFundingDecisionRestService.saveApplicationFundingDecisionData(any(), any())).thenReturn(restSuccess());
		when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(any(), any(), any(), any(),any())).thenReturn(applicationSummaryPageResource);

		service.saveApplicationFundingDecisionData(1L, fundingDecisionChoice, applicationIds);

		Map<Long, FundingDecision> expectedDecisionMap = new HashMap<>();
		expectedDecisionMap.put(8L, FundingDecision.ON_HOLD);
		expectedDecisionMap.put(9L, FundingDecision.ON_HOLD);

		verify(applicationFundingDecisionRestService).saveApplicationFundingDecisionData(1L, expectedDecisionMap);
		verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(any(),any(),any(),any(),any());
	}
}
