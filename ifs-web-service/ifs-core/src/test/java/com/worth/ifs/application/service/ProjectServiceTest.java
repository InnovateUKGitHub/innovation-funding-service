package com.worth.ifs.application.service;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.project.service.ProjectRestService;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {
	@InjectMocks
	private ProjectService projectService;
	@Mock
	private ProjectRestService projectRestService;
	
	@Test
	public void testSetProjectManager() {
		Long projectId = 2L;
		Long projectManagerId = 4L;
		
		projectService.updateProjectManager(projectId, projectManagerId);
		
		verify(projectRestService).updateProjectManager(projectId, projectManagerId);
	}
}
