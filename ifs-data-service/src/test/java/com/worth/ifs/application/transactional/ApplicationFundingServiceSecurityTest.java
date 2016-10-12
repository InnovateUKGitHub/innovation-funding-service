package com.worth.ifs.application.transactional;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.resource.RoleResource;

public class ApplicationFundingServiceSecurityTest extends BaseServiceSecurityTest<ApplicationFundingService> {

	@Test
	public void testFundingDecisionAllowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		classUnderTest.makeFundingDecision(123L, new HashMap<Long, FundingDecision>());
	}

	@Test
	public void testFundingDecisionDeniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.makeFundingDecision(123L, new HashMap<Long, FundingDecision>());
			fail("Should not have been able to make funding decision without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testFundingDecisionDeniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.makeFundingDecision(123L, new HashMap<Long, FundingDecision>());
			fail("Should not have been able to make funding decision without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testFundingDecisionDeniedIfNotCorrectGlobalRoles() {

		List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
				.collect(toList());

		nonCompAdminRoles.forEach(role -> {

			setLoggedInUser(
					newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
			try {
				classUnderTest.makeFundingDecision(123L, new HashMap<Long, FundingDecision>());
				fail("Should not have been able to make funding decision without the global Comp Admin role");
			} catch (AccessDeniedException e) {
				// expected behaviour
			}
		});
	}
	
	@Test
	public void testSaveFundingDecisionDataAllowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		classUnderTest.saveFundingDecisionData(123L, new HashMap<Long, FundingDecision>());
	}

	@Test
	public void testSaveFundingDecisionDataDeniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.saveFundingDecisionData(123L, new HashMap<Long, FundingDecision>());
			fail("Should not have been able to save funding decision data without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testSaveFundingDecisionDataDeniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.saveFundingDecisionData(123L, new HashMap<Long, FundingDecision>());
			fail("Should not have been able to save funding decision data without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testSaveFundingDecisionDataDeniedIfNotCorrectGlobalRoles() {

		List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
				.collect(toList());

		nonCompAdminRoles.forEach(role -> {

			setLoggedInUser(
					newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
			try {
				classUnderTest.saveFundingDecisionData(123L, new HashMap<Long, FundingDecision>());
				fail("Should not have been able to save funding decision data without the global Comp Admin role");
			} catch (AccessDeniedException e) {
				// expected behaviour
			}
		});
	}
	
	@Override
	protected Class<? extends ApplicationFundingService> getClassUnderTest() {
		return TestApplicationFundingService.class;
	}

	public static class TestApplicationFundingService implements ApplicationFundingService {

		@Override
		public ServiceResult<Void> makeFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {
			return null;
		}

		@Override
		public ServiceResult<Void> notifyLeadApplicantsOfFundingDecisions(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions) {
			return null;
		}

		@Override
		public ServiceResult<Void> saveFundingDecisionData(Long competitionId, Map<Long, FundingDecision> decision) {
			return null;
		}

	}
}
