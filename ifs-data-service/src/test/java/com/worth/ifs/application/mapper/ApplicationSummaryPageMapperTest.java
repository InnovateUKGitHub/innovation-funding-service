package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSummaryPageMapperTest {

	@InjectMocks
	private ApplicationSummaryPageMapperImpl mapper;
	
	@Mock
	private ApplicationSummaryMapper applicationSummaryMapper;
	
	@Test
	public void testMap() {
		
		Application app1 = mock(Application.class);
		Application app2 = mock(Application.class);
		List<Application> content = Arrays.asList(app1, app2);

		Pageable pageable = new PageRequest(3, 20);
		Page<Application> source = new PageImpl<Application>(content, pageable, 62L);
		
		ApplicationSummaryResource conv1 = mock(ApplicationSummaryResource.class);
		ApplicationSummaryResource conv2 = mock(ApplicationSummaryResource.class);
		when(applicationSummaryMapper.mapToResource(app1)).thenReturn(conv1);
		when(applicationSummaryMapper.mapToResource(app2)).thenReturn(conv2);
		
		
		ApplicationSummaryPageResource result = mapper.mapToResource(source);
		
		
		assertEquals(3, result.getNumber());
		assertEquals(62L, result.getTotalElements());
		assertEquals(2, result.getContent().size());
		assertEquals(conv1, result.getContent().get(0));
		assertEquals(conv2, result.getContent().get(1));
		assertEquals(4, result.getTotalPages());
		assertEquals(20, result.getSize());
	}
}
