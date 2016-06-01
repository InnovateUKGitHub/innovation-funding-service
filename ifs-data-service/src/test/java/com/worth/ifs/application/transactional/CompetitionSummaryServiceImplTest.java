package com.worth.ifs.application.transactional;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.CompletedPercentageResourceBuilder.newCompletedPercentageResource;
import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.CREATED_AND_OPEN_STATUS_IDS;
import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource.Status;

public class CompetitionSummaryServiceImplTest extends BaseUnitTestMocksTest {

	private static final Long COMP_ID = Long.valueOf(123L);

	@InjectMocks
	private CompetitionSummaryService competitionSummaryService = new CompetitionSummaryServiceImpl();
		
	private Competition competition;
	
	@Before
	public void setUp() {
		competition = newCompetition()
				.withId(COMP_ID)
				.withName("compname")
				.withCompetitionStatus(Status.IN_ASSESSMENT)
				.withEndDate(LocalDateTime.of(2016, 5, 23, 8, 30))
				.build();
		when(competitionRepositoryMock.findById(COMP_ID)).thenReturn(competition);
		
		when(applicationRepositoryMock.countByCompetitionId(COMP_ID)).thenReturn(83L);
		
		when(applicationRepositoryMock.findByCompetitionIdAndApplicationStatusIdIn(COMP_ID, CREATED_AND_OPEN_STATUS_IDS)).thenReturn(newApplication().withId(1L, 2L).build(2));
		when(applicationServiceMock.getProgressPercentageByApplicationId(1L)).thenReturn(serviceSuccess(newCompletedPercentageResource().withCompletedPercentage(new BigDecimal("20")).build()));
		when(applicationServiceMock.getProgressPercentageByApplicationId(2L)).thenReturn(serviceSuccess(newCompletedPercentageResource().withCompletedPercentage(new BigDecimal("80")).build()));
		
		when(applicationRepositoryMock.findByCompetitionIdAndApplicationStatusIdNotIn(COMP_ID, SUBMITTED_STATUS_IDS)).thenReturn(newApplication().withId(3L, 4L).build(2));
		when(applicationServiceMock.getProgressPercentageByApplicationId(3L)).thenReturn(serviceSuccess(newCompletedPercentageResource().withCompletedPercentage(new BigDecimal("20")).build()));
		when(applicationServiceMock.getProgressPercentageByApplicationId(4L)).thenReturn(serviceSuccess(newCompletedPercentageResource().withCompletedPercentage(new BigDecimal("80")).build()));
		
		when(applicationRepositoryMock.countByCompetitionIdAndApplicationStatusIdIn(COMP_ID, SUBMITTED_STATUS_IDS)).thenReturn(5L);
		
		when(applicationRepositoryMock.countByCompetitionIdAndApplicationStatusId(COMP_ID, 3L)).thenReturn(8L);
	}
	
	@Test
	public void testSummary() {
		
		ServiceResult<CompetitionSummaryResource> result = competitionSummaryService.getCompetitionSummaryByCompetitionId(COMP_ID);
		
		assertTrue(result.isSuccess());
		assertEquals(COMP_ID, result.getSuccessObject().getCompetitionId());
		assertEquals(competition.getName(), result.getSuccessObject().getCompetitionName());
		assertEquals(competition.getCompetitionStatus(), result.getSuccessObject().getCompetitionStatus());
		assertEquals(Long.valueOf(83L), result.getSuccessObject().getTotalNumberOfApplications());
		assertEquals(Long.valueOf(1L), result.getSuccessObject().getApplicationsStarted());
		assertEquals(Long.valueOf(1L), result.getSuccessObject().getApplicationsInProgress());
		assertEquals(Long.valueOf(5L), result.getSuccessObject().getApplicationsSubmitted());
		assertEquals(Long.valueOf(78L), result.getSuccessObject().getApplicationsNotSubmitted());
		assertEquals(LocalDateTime.of(2016, 5, 23, 8, 30), result.getSuccessObject().getApplicationDeadline());
		assertEquals(Long.valueOf(8L), result.getSuccessObject().getApplicationsFunded());
	}

}
