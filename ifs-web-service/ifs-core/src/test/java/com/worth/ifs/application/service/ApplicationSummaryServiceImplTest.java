package com.worth.ifs.application.service;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSummaryServiceImplTest {

	@InjectMocks
	private ApplicationSummaryServiceImpl service;
	
	@Mock
	private ApplicationSummaryRestService applicationSummaryRestService;
	
	@Test
	public void testSummaryByCompetitionId() {
		CompetitionSummaryResource resource = new CompetitionSummaryResource();
		when(applicationSummaryRestService.getCompetitionSummaryByCompetitionId(Long.valueOf(123L))).thenReturn(restSuccess(resource));
		
		CompetitionSummaryResource result = service.getCompetitionSummaryByCompetitionId(Long.valueOf(123L));
		
		assertEquals(resource, result);
	}
	
	@Test
	public void testFindByCompetitionId() {
		ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
		when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(123L), "sort", 0, 20)).thenReturn(restSuccess(resource));
		
		ApplicationSummaryPageResource result = service.findByCompetitionId(Long.valueOf(123L), "sort", 0, 20);
		
		assertEquals(resource, result);
	}
	
	@Test
	public void testFindSubmittedByCompetitionId() {
		ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
		when(applicationSummaryRestService.getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(123L), "sort", 0, 20)).thenReturn(restSuccess(resource));
		
		ApplicationSummaryPageResource result = service.getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(123L), "sort", 0, 20);
		
		assertEquals(resource, result);
	}
	
	@Test
	public void testFindNotSubmittedByCompetitionId() {
		ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
		when(applicationSummaryRestService.getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(123L), "sort", 0, 20)).thenReturn(restSuccess(resource));
		
		ApplicationSummaryPageResource result = service.getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(123L), "sort", 0, 20);
		
		assertEquals(resource, result);
	}
	
	@Test
	public void testFindFundedByCompetitionId() {
		ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
		when(applicationSummaryRestService.getFundedApplicationSummariesByCompetitionId(Long.valueOf(123L), "sort", 0, 20)).thenReturn(restSuccess(resource));
		
		ApplicationSummaryPageResource result = service.getFundedApplicationSummariesByCompetitionId(Long.valueOf(123L), "sort", 0, 20);
		
		assertEquals(resource, result);
	}
	
	@Test
	public void testFindFundedCountByCompetitionId() {
		ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
		resource.setTotalElements(987L);
		when(applicationSummaryRestService.getFundedApplicationSummariesByCompetitionId(Long.valueOf(123L), null, 0, 1)).thenReturn(restSuccess(resource));
		
		Long result = service.getFundedApplicationCountByCompetitionId(Long.valueOf(123L));
		
		assertEquals(Long.valueOf(987L), result);
	}
}
