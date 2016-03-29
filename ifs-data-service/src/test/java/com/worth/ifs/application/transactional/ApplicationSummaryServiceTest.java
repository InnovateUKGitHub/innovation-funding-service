package com.worth.ifs.application.transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.ApplicationSummaryMapper;
import com.worth.ifs.application.mapper.ApplicationSummaryPageMapper;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;

public class ApplicationSummaryServiceTest extends BaseUnitTestMocksTest {

	@InjectMocks
	private ApplicationSummaryService applicationSummaryService = new ApplicationSummaryServiceImpl();

	@Mock
	private ApplicationSummaryMapper applicationSummaryMapper;

	@Mock
	private ApplicationSummaryPageMapper applicationSummaryPageMapper;

	@Test
	public void getById() throws Exception {

		Application application = mock(Application.class);
		when(applicationRepositoryMock.findOne(5L)).thenReturn(application);

		ApplicationSummaryResource resource = mock(ApplicationSummaryResource.class);
		when(applicationSummaryMapper.mapToResource(application)).thenReturn(resource);
		
		ServiceResult<ApplicationSummaryResource> result = applicationSummaryService.getApplicationSummaryById(Long.valueOf(5L));
		
		assertTrue(result.isSuccess());
		assertEquals(resource, result.getSuccessObject());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void findByCompetition() throws Exception {

		Page<Application> page = mock(Page.class);
		when(applicationRepositoryMock.findByCompetitionId(eq(Long.valueOf(123L)), argThat(new PageableMatcher(6, 20)))).thenReturn(page);

		ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
		when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(Long.valueOf(123L), 6);
		
		assertTrue(result.isSuccess());
		assertEquals(resource, result.getSuccessObject());
	}
	
	private static class PageableMatcher extends ArgumentMatcher<Pageable> {

		private int expectedPage;
		private int expectedPageSize;
		
		public PageableMatcher(int expectedPage, int expectedPageSize) {
			this.expectedPage = expectedPage;
			this.expectedPageSize = expectedPageSize;
		}
		
		@Override
		public boolean matches(Object argument) {
			Pageable arg = (Pageable) argument;
			
			if(!(expectedPage == arg.getPageNumber())){
				return false;
			}
			
			if(!(expectedPageSize == arg.getPageSize())){
				return false;
			}
			
			return true;
		}
		
	}
}
