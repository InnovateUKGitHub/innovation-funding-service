package com.worth.ifs.application.mapper;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.application.transactional.ApplicationSummarisationService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.UserRoleType;


@RunWith(MockitoJUnitRunner.class)
public class ApplicationSummaryMapperTest {

	private static final Long APPLICATION_ID = Long.valueOf(123L);

	@InjectMocks
	private ApplicationSummaryMapperImpl mapper;
	
	@Mock
	private ApplicationService applicationService;
	
	@Mock
	private ApplicationSummarisationService applicationSummarisationService;
	
	@Test
	public void testMap() {
		
		Application source = new Application();
		source.setId(APPLICATION_ID);
		source.setName("appname");
		source.setApplicationStatus(new ApplicationStatus(ApplicationStatusConstants.OPEN.getId(), ApplicationStatusConstants.OPEN.getName()));
		source.setDurationInMonths(7L);
		
		Organisation org1 = new Organisation(1L, "leadorg");
		Organisation org2 = new Organisation(2L, "otherorg1");
		
		ProcessRole leadProcessRole = leadProcessRole(org1);
		source.addUserApplicationRole(leadProcessRole);
		
		ProcessRole collaboratorProcessRole1 = collaboratorProcessRole(org2);
		source.addUserApplicationRole(collaboratorProcessRole1);
		ProcessRole collaboratorProcessRole2 = collaboratorProcessRole(org2);
		source.addUserApplicationRole(collaboratorProcessRole2);
		ProcessRole collaboratorProcessRole3 = collaboratorProcessRole(org1);
		source.addUserApplicationRole(collaboratorProcessRole3);
		
		CompletedPercentageResource resource = new CompletedPercentageResource();
		resource.setCompletedPercentage(new BigDecimal("66.6"));
		when(applicationService.getProgressPercentageByApplicationId(APPLICATION_ID)).thenReturn(serviceSuccess(resource));
		
		when(applicationSummarisationService.getFundingSought(source)).thenReturn(serviceSuccess(new BigDecimal("1.23")));
		when(applicationSummarisationService.getTotalProjectCost(source)).thenReturn(serviceSuccess(new BigDecimal("9.87")));
		
		ApplicationSummaryResource result = mapper.mapToResource(source);
		
		assertEquals(APPLICATION_ID, result.getId());
		assertEquals("appname", result.getName());
		assertEquals("In Progress", result.getStatus());
		assertEquals(Integer.valueOf(66), result.getCompletedPercentage());
		assertEquals("leadorg", result.getLead());
		assertEquals(Integer.valueOf(2), result.getNumberOfPartners());
		assertEquals(new BigDecimal("1.23"), result.getGrantRequested());
		assertEquals(new BigDecimal("9.87"), result.getTotalProjectCost());
		assertEquals(Long.valueOf(7L), result.getDuration());
		assertFalse(result.isFunded());
	}

	private ProcessRole leadProcessRole(Organisation organisation) {
		ProcessRole leadProcessRole = new ProcessRole();
		
		leadProcessRole.setOrganisation(organisation);
		Role role = new Role();
		role.setName(UserRoleType.LEADAPPLICANT.getName());
		leadProcessRole.setRole(role);
		return leadProcessRole;
	}
	
	private ProcessRole collaboratorProcessRole(Organisation organisation) {
		ProcessRole collaboratorProcessRole = new ProcessRole();
		collaboratorProcessRole.setOrganisation(organisation);
		Role role = new Role();
		role.setName(UserRoleType.COLLABORATOR.getName());
				
		collaboratorProcessRole.setRole(role);
		return collaboratorProcessRole;
	}
}
