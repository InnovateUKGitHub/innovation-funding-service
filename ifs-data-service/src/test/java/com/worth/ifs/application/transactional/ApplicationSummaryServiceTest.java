package com.worth.ifs.application.transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.google.common.collect.Lists;
import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.ApplicationSummaryMapper;
import com.worth.ifs.application.mapper.ApplicationSummaryPageMapper;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;

public class ApplicationSummaryServiceTest extends BaseUnitTestMocksTest {

	private static final Long COMP_ID = Long.valueOf(123L);

	@InjectMocks
	private ApplicationSummaryService applicationSummaryService = new ApplicationSummaryServiceImpl();

	@Mock
	private ApplicationSummaryMapper applicationSummaryMapper;

	@Mock
	private ApplicationSummaryPageMapper applicationSummaryPageMapper;
	
	@Test
	public void findByCompetitionNoSortWillSortById() throws Exception {

		Page<Application> page = mock(Page.class);
		when(applicationRepositoryMock.findByCompetitionId(eq(COMP_ID), argThat(new PageableMatcher(6, 20, srt("id", ASC))))).thenReturn(page);

		ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
		when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 6, null);
		
		assertTrue(result.isSuccess());
		assertEquals(resource, result.getSuccessObject());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void findByCompetitionSortById() throws Exception {

		Page<Application> page = mock(Page.class);
		when(applicationRepositoryMock.findByCompetitionId(eq(COMP_ID), argThat(new PageableMatcher(6, 20, srt("id", ASC))))).thenReturn(page);

		ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
		when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 6, "id");
		
		assertTrue(result.isSuccess());
		assertEquals(resource, result.getSuccessObject());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void findByCompetitionSortByName() throws Exception {

		Page<Application> page = mock(Page.class);
		when(applicationRepositoryMock.findByCompetitionId(eq(COMP_ID), argThat(new PageableMatcher(6, 20, srt("name", ASC), srt("id", ASC))))).thenReturn(page);

		ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
		when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 6, "name");
		
		assertTrue(result.isSuccess());
		assertEquals(resource, result.getSuccessObject());
	}
	
	@Test
	public void findByCompetitionSortByLead() throws Exception {

		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		List<Application> applications = Arrays.asList(app1, app2);
		
		ApplicationSummaryResource sum1 = sumLead("b");
		ApplicationSummaryResource sum2 = sumLead("a");
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 0, "lead");
		
		assertTrue(result.isSuccess());
		assertEquals(0, result.getSuccessObject().getNumber());
		assertEquals(2, result.getSuccessObject().getContent().size());
		assertEquals(sum2, result.getSuccessObject().getContent().get(0));
		assertEquals(sum1, result.getSuccessObject().getContent().get(1));
		assertEquals(20, result.getSuccessObject().getSize());
		assertEquals(2, result.getSuccessObject().getTotalElements());
		assertEquals(1, result.getSuccessObject().getTotalPages());
	}
	
	@Test
	public void findByCompetitionSortByLeadSameLeadWillSortById() throws Exception {

		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		List<Application> applications = Arrays.asList(app1, app2);
		
		ApplicationSummaryResource sum1 = sumLead("a", 2L);
		ApplicationSummaryResource sum2 = sumLead("a", 1L);
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 0, "lead");
		
		assertTrue(result.isSuccess());
		assertEquals(0, result.getSuccessObject().getNumber());
		assertEquals(2, result.getSuccessObject().getContent().size());
		assertEquals(sum2, result.getSuccessObject().getContent().get(0));
		assertEquals(sum1, result.getSuccessObject().getContent().get(1));
		assertEquals(20, result.getSuccessObject().getSize());
		assertEquals(2, result.getSuccessObject().getTotalElements());
		assertEquals(1, result.getSuccessObject().getTotalPages());
	}
	
	@Test
	public void findByCompetitionSortByLeadNotFirstPage() throws Exception {

		List<Application> applications = new ArrayList<>();
		for(int i = 0; i < 22; i++) {
			Application app = mock(Application.class);
			applications.add(app);
			ApplicationSummaryResource sum = sumLead("a" + String.format("%02d", i));
			when(applicationSummaryMapper.mapToResource(app)).thenReturn(sum);
		}
		
		Collections.reverse(applications);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 1, "lead");
		
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccessObject().getNumber());
		assertEquals(2, result.getSuccessObject().getContent().size());
		assertEquals("a20", result.getSuccessObject().getContent().get(0).getLead());
		assertEquals("a21", result.getSuccessObject().getContent().get(1).getLead());
		assertEquals(20, result.getSuccessObject().getSize());
		assertEquals(22, result.getSuccessObject().getTotalElements());
		assertEquals(2, result.getSuccessObject().getTotalPages());
	}
	
	@Test
	public void findByCompetitionSortByLeadHandlesNullLeads() throws Exception {

		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		List<Application> applications = Arrays.asList(app1, app2);
		
		ApplicationSummaryResource sum1 = sumLead(null);
		ApplicationSummaryResource sum2 = sumLead(null);
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 0, "lead");
		
		assertEquals(2, result.getSuccessObject().getContent().size());
		assertEquals(sum1, result.getSuccessObject().getContent().get(0));
		assertEquals(sum2, result.getSuccessObject().getContent().get(1));
	}
	
	@Test
	public void findByCompetitionSortByLeadHandlesNullAndNotNullLead() throws Exception {

		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		Application app3 = mock(Application.class);
		List<Application> applications = Arrays.asList(app1, app2, app3);
		
		ApplicationSummaryResource sum1 = sumLead(null);
		ApplicationSummaryResource sum2 = sumLead("a");
		ApplicationSummaryResource sum3 = sumLead(null);
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
		when(applicationSummaryMapper.mapToResource(app3)).thenReturn(sum3);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 0, "lead");
		
		assertEquals(3, result.getSuccessObject().getContent().size());
		assertEquals(sum1, result.getSuccessObject().getContent().get(0));
		assertEquals(sum3, result.getSuccessObject().getContent().get(1));
		assertEquals(sum2, result.getSuccessObject().getContent().get(2));
	}
	
	@Test
	public void findByCompetitionSortByCompletedPercentage() throws Exception {

		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		List<Application> applications = Arrays.asList(app1, app2);
		
		ApplicationSummaryResource sum1 = sumPercentage(25);
		ApplicationSummaryResource sum2 = sumPercentage(50);
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 0, "percentageComplete");
		
		assertTrue(result.isSuccess());
		assertEquals(0, result.getSuccessObject().getNumber());
		assertEquals(2, result.getSuccessObject().getContent().size());
		assertEquals(sum2, result.getSuccessObject().getContent().get(0));
		assertEquals(sum1, result.getSuccessObject().getContent().get(1));
		assertEquals(20, result.getSuccessObject().getSize());
		assertEquals(2, result.getSuccessObject().getTotalElements());
		assertEquals(1, result.getSuccessObject().getTotalPages());
	}
	
	@Test
	public void findByCompetitionSortByCompletedPercentageSamePercentageWillSortById() throws Exception {

		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		List<Application> applications = Arrays.asList(app1, app2);
		
		ApplicationSummaryResource sum1 = sumPercentage(50, 2L);
		ApplicationSummaryResource sum2 = sumPercentage(50, 1L);
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 0, "percentageComplete");
		
		assertTrue(result.isSuccess());
		assertEquals(0, result.getSuccessObject().getNumber());
		assertEquals(2, result.getSuccessObject().getContent().size());
		assertEquals(sum2, result.getSuccessObject().getContent().get(0));
		assertEquals(sum1, result.getSuccessObject().getContent().get(1));
		assertEquals(20, result.getSuccessObject().getSize());
		assertEquals(2, result.getSuccessObject().getTotalElements());
		assertEquals(1, result.getSuccessObject().getTotalPages());
	}
	
	@Test
	public void findByCompetitionSortByPercentageCompleteNotFirstPage() throws Exception {

		List<Application> applications = new ArrayList<>();
		for(int i = 0; i < 22; i++) {
			Application app = mock(Application.class);
			applications.add(app);
			ApplicationSummaryResource sum = sumPercentage(21 - i);
			when(applicationSummaryMapper.mapToResource(app)).thenReturn(sum);
		}
		
		Collections.reverse(applications);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 1, "percentageComplete");
		
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccessObject().getNumber());
		assertEquals(2, result.getSuccessObject().getContent().size());
		assertEquals(Integer.valueOf(1), result.getSuccessObject().getContent().get(0).getCompletedPercentage());
		assertEquals(Integer.valueOf(0), result.getSuccessObject().getContent().get(1).getCompletedPercentage());
		assertEquals(20, result.getSuccessObject().getSize());
		assertEquals(22, result.getSuccessObject().getTotalElements());
		assertEquals(2, result.getSuccessObject().getTotalPages());
	}
	
	@Test
	public void findByCompetitionSortByCompletedPercentageHandlesNullPercentages() throws Exception {

		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		List<Application> applications = Arrays.asList(app1, app2);
		
		ApplicationSummaryResource sum1 = sumPercentage(null);
		ApplicationSummaryResource sum2 = sumPercentage(null);
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 0, "percentageComplete");
		
		assertEquals(2, result.getSuccessObject().getContent().size());
		assertEquals(sum1, result.getSuccessObject().getContent().get(0));
		assertEquals(sum2, result.getSuccessObject().getContent().get(1));
	}
	
	@Test
	public void findByCompetitionSortByCompletedPercentageHandlesNullAndNotNullPercentages() throws Exception {

		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		Application app3 = mock(Application.class);
		List<Application> applications = Arrays.asList(app1, app2, app3);
		
		ApplicationSummaryResource sum1 = sumPercentage(null);
		ApplicationSummaryResource sum2 = sumPercentage(50);
		ApplicationSummaryResource sum3 = sumPercentage(null);
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
		when(applicationSummaryMapper.mapToResource(app3)).thenReturn(sum3);
		
		when(applicationRepositoryMock.findByCompetitionId(COMP_ID)).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getApplicationSummariesByCompetitionId(COMP_ID, 0, "percentageComplete");
		
		assertEquals(3, result.getSuccessObject().getContent().size());
		assertEquals(sum2, result.getSuccessObject().getContent().get(0));
		assertEquals(sum1, result.getSuccessObject().getContent().get(1));
		assertEquals(sum3, result.getSuccessObject().getContent().get(2));
	}
	
	@Test
	public void findByCompetitionNotSubmittedApplications() throws Exception {

		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		List<Application> applications = Arrays.asList(app1, app2);
		
		ApplicationSummaryResource sum1 = sumPercentage(25, 1L);
		ApplicationSummaryResource sum2 = sumPercentage(50, 2L);
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(sum1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(sum2);
		
		when(applicationRepositoryMock.findByCompetitionIdAndApplicationStatusIdNotIn(COMP_ID, Arrays.asList(3L,4L,2L))).thenReturn(applications);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(COMP_ID, 0, "percentageComplete");
		
		assertTrue(result.isSuccess());
		assertEquals(0, result.getSuccessObject().getNumber());
		assertEquals(2, result.getSuccessObject().getContent().size());
		assertEquals(sum2, result.getSuccessObject().getContent().get(0));
		assertEquals(sum1, result.getSuccessObject().getContent().get(1));
		assertEquals(20, result.getSuccessObject().getSize());
		assertEquals(2, result.getSuccessObject().getTotalElements());
		assertEquals(1, result.getSuccessObject().getTotalPages());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findByCompetitionSubmittedApplications() throws Exception {

		Page<Application> page = mock(Page.class);

		ApplicationSummaryPageResource resource = mock(ApplicationSummaryPageResource.class);
		when(applicationSummaryPageMapper.mapToResource(page)).thenReturn(resource);
		
		when(applicationRepositoryMock.findByCompetitionIdAndApplicationStatusIdIn(eq(COMP_ID), eq(Arrays.asList(3L,4L,2L)), argThat(new PageableMatcher(0, 20, srt("id", ASC))))).thenReturn(page);
		
		ServiceResult<ApplicationSummaryPageResource> result = applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMP_ID, 0, "id");
		
		assertTrue(result.isSuccess());
		assertEquals(0, result.getSuccessObject().getNumber());
		assertEquals(resource, result.getSuccessObject());
	}
	
	private ApplicationSummaryResource sumLead(String lead) {
		ApplicationSummaryResource res = new ApplicationSummaryResource();
		res.setLead(lead);
		return res;
	}
	
	private ApplicationSummaryResource sumLead(String lead, Long id) {
		ApplicationSummaryResource res = sumLead(lead);
		res.setId(id);
		return res;
	}
	
	private ApplicationSummaryResource sumPercentage(Integer percentage) {
		ApplicationSummaryResource res = new ApplicationSummaryResource();
		res.setCompletedPercentage(percentage);
		return res;
	}
	
	private ApplicationSummaryResource sumPercentage(Integer percentage, Long id) {
		ApplicationSummaryResource res = sumPercentage(percentage);
		res.setId(id);
		return res;
	}
	
	private Sort srt(String field, Direction dir){
		Sort sort = new Sort();
		sort.setField(field);
		sort.setDirection(dir);
		return sort;
	}
	
	private static class PageableMatcher extends ArgumentMatcher<Pageable> {

		private int expectedPage;
		private int expectedPageSize;
		private Sort[] sortFields;
		
		public PageableMatcher(int expectedPage, int expectedPageSize, Sort... sortFields) {
			this.expectedPage = expectedPage;
			this.expectedPageSize = expectedPageSize;
			this.sortFields = sortFields;
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
			
			List<Order> sortList = Lists.newArrayList(arg.getSort().iterator());
			
			if(sortList.size() != sortFields.length) {
				return false;
			}
			
			for(int i = 0; i < sortFields.length; i++) {
				Sort sortField = sortFields[i];
				Order order = sortList.get(i);
				if(!sortField.getDirection().equals(order.getDirection())) {
					return false;
				}
				if(!sortField.getField().equals(order.getProperty())){
					return false;
				}
			}
			
			return true;
		}
	}
	
	private static class Sort {
		private String field;
		private Direction direction;
		
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public Direction getDirection() {
			return direction;
		}
		public void setDirection(Direction direction) {
			this.direction = direction;
		}
	}
}
