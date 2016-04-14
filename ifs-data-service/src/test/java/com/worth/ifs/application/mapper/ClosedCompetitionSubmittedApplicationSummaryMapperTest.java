package com.worth.ifs.application.mapper;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryResource;
import com.worth.ifs.application.transactional.ApplicationSummarisationService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;

@RunWith(MockitoJUnitRunner.class)
public class ClosedCompetitionSubmittedApplicationSummaryMapperTest {

	@InjectMocks
	private ClosedCompetitionSubmittedApplicationSummaryMapperImpl mapper;
	
	@Mock
	private ApplicationSummarisationService applicationSummarisationService;
	
	@Test
	public void testMap() {
		
		Application source = new Application();
		source.setId(Long.valueOf(123L));
		source.setName("appname");
		source.setDurationInMonths(5L);
		
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
		
		when(applicationSummarisationService.getFundingSought(source)).thenReturn(serviceSuccess(new BigDecimal("1.23")));
		when(applicationSummarisationService.getTotalProjectCost(source)).thenReturn(serviceSuccess(new BigDecimal("9.87")));
		
		ClosedCompetitionSubmittedApplicationSummaryResource result = mapper.mapToResource(source);
		
		assertEquals(Long.valueOf(123L), result.getId());
		assertEquals("appname", result.getName());
		assertEquals("leadorg", result.getLead());
		assertEquals(Integer.valueOf(2), result.getNumberOfPartners());
		assertEquals(new BigDecimal("1.23"), result.getGrantRequested());
		assertEquals(new BigDecimal("9.87"), result.getTotalProjectCost());
		assertEquals(Long.valueOf(5L), result.getDuration());
	}

	private ProcessRole collaboratorProcessRole(Organisation organisation) {
		ProcessRole collaboratorProcessRole = new ProcessRole();
		collaboratorProcessRole.setOrganisation(organisation);
		Role role = new Role();
		role.setName(UserRoleType.COLLABORATOR.getName());
				
		collaboratorProcessRole.setRole(role);
		return collaboratorProcessRole;
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
