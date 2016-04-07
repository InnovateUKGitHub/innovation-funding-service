package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ApplicationSummaryMapperTest {

	@InjectMocks
	private ApplicationSummaryMapperImpl mapper;
	
	@Mock
	private ApplicationService applicationService;
	
	@Test
	public void testMap() {
		
		Application source = new Application();
		source.setId(Long.valueOf(123L));
		source.setName("appname");
		source.setApplicationStatus(new ApplicationStatus(6L, "statusname"));
		
		ProcessRole leadProcessRole = leadProcessRole("jim", "kirk");
		source.addUserApplicationRole(leadProcessRole);
		
		CompletedPercentageResource resource = new CompletedPercentageResource();
		resource.setCompletedPercentage(new BigDecimal("66.6"));
		when(applicationService.getProgressPercentageByApplicationId(Long.valueOf(123L))).thenReturn(serviceSuccess(resource));
		
		ApplicationSummaryResource result = mapper.mapToResource(source);
		
		assertEquals(Long.valueOf(123L), result.getId());
		assertEquals("appname", result.getName());
		assertEquals(Long.valueOf(6L), result.getApplicationStatus());
		assertEquals("statusname", result.getApplicationStatusName());
		assertEquals(Integer.valueOf(66), result.getCompletedPercentage());
		assertEquals("jim kirk", result.getLead());
	}

	private ProcessRole leadProcessRole(String firstName, String surname) {
		ProcessRole leadProcessRole = new ProcessRole();
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(surname);
		leadProcessRole.setUser(user);
		Role role = new Role();
		role.setName(UserRoleType.LEADAPPLICANT.getName());
				
		leadProcessRole.setRole(role);
		return leadProcessRole;
	}
	
}
