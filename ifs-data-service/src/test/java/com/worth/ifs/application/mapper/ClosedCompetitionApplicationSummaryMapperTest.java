package com.worth.ifs.application.mapper;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
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
import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClosedCompetitionApplicationSummaryMapperTest {

	@InjectMocks
	private ClosedCompetitionApplicationSummaryMapperImpl mapper;
	
	@Mock
	private ApplicationFinanceRestService applicationFinanceRestService;
	
	@Test
	public void testMap() {
		
		Application source = new Application();
		source.setId(Long.valueOf(123L));
		source.setName("appname");
		source.setDurationInMonths(5L);
		
		ProcessRole leadProcessRole = leadProcessRole("jim", "kirk");
		source.addUserApplicationRole(leadProcessRole);
		ProcessRole collaboratorProcessRole1 = collaboratorProcessRole();
		source.addUserApplicationRole(collaboratorProcessRole1);
		ProcessRole collaboratorProcessRole2 = collaboratorProcessRole();
		source.addUserApplicationRole(collaboratorProcessRole2);
		
		ApplicationFinanceResource resource = mock(ApplicationFinanceResource.class);
		GrantClaim grantClaim = mock(GrantClaim.class);
		when(grantClaim.getTotal()).thenReturn(new BigDecimal("12.30"));
		when(resource.getGrantClaim()).thenReturn(grantClaim);
		when(resource.getTotal()).thenReturn(new BigDecimal("66.60"));
		List<ApplicationFinanceResource> resources = Arrays.asList(resource);
		when(applicationFinanceRestService.getApplicationFinances(Long.valueOf(123L))).thenReturn(restSuccess(resources));
		
		ClosedCompetitionApplicationSummaryResource result = mapper.mapToResource(source);
		
		assertEquals(Long.valueOf(123L), result.getId());
		assertEquals("appname", result.getName());
		assertEquals("jim kirk", result.getLead());
		assertEquals(Integer.valueOf(2), result.getNumberOfPartners());
		assertEquals(new BigDecimal("12.30"), result.getGrantRequested());
		assertEquals(new BigDecimal("66.60"), result.getTotalProjectCost());
		assertEquals(Long.valueOf(5L), result.getDuration());
	}

	private ProcessRole collaboratorProcessRole() {
		ProcessRole collaboratorProcessRole = new ProcessRole();
		User user = new User();
		collaboratorProcessRole.setUser(user);
		Role role = new Role();
		role.setName(UserRoleType.COLLABORATOR.getName());
				
		collaboratorProcessRole.setRole(role);
		return collaboratorProcessRole;
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
