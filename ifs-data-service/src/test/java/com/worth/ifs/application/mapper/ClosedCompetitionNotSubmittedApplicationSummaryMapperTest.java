package com.worth.ifs.application.mapper;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;

@RunWith(MockitoJUnitRunner.class)
public class ClosedCompetitionNotSubmittedApplicationSummaryMapperTest {
	
	private static final Long APPLICATION_ID = Long.valueOf(123L);
	
	@InjectMocks
	private ClosedCompetitionNotSubmittedApplicationSummaryMapperImpl mapper;
	
	@Mock
	private ApplicationService applicationService;
	
	@Test
	public void testMap() {
		
		Application source = new Application();
		source.setId(APPLICATION_ID);
		source.setName("appname");
		source.setDurationInMonths(5L);
		
		Organisation org1 = new Organisation(1L, "leadorg");
		
		ProcessRole leadProcessRole = leadProcessRole(org1);
		source.addUserApplicationRole(leadProcessRole);
		
		CompletedPercentageResource resource = new CompletedPercentageResource();
		resource.setCompletedPercentage(new BigDecimal("66.6"));
		when(applicationService.getProgressPercentageByApplicationId(APPLICATION_ID)).thenReturn(serviceSuccess(resource));
		
		ClosedCompetitionNotSubmittedApplicationSummaryResource result = mapper.mapToResource(source);
		
		assertEquals(APPLICATION_ID, result.getId());
		assertEquals("appname", result.getName());
		assertEquals("leadorg", result.getLead());
		assertEquals(Integer.valueOf(66), result.getCompletedPercentage());
	}

	private ProcessRole leadProcessRole(Organisation organisation) {
		ProcessRole leadProcessRole = new ProcessRole();
		
		leadProcessRole.setOrganisation(organisation);
		Role role = new Role();
		role.setName(UserRoleType.LEADAPPLICANT.getName());
		leadProcessRole.setRole(role);
		return leadProcessRole;
	}
}
