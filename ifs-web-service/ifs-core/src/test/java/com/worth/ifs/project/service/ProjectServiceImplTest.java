package com.worth.ifs.project.service;

import com.worth.ifs.project.ProjectServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceImplTest {

	@InjectMocks
	private ProjectServiceImpl service;

	@Mock
	private ProjectRestService projectRestService;
	
	@Test
	public void testUpdateFinanceContact() {
		Long projectId = 1L;
		Long organisationId = 2L;
		Long financeContactId = 3L;
		
		when(projectRestService.updateFinanceContact(projectId, organisationId, financeContactId)).thenReturn(restSuccess());
		
		service.updateFinanceContact(projectId, organisationId, financeContactId);
		
		verify(projectRestService).updateFinanceContact(projectId, organisationId, financeContactId);
	}
	
}
