package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.RoleResource;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.domain.UserRoleType.COMP_ADMIN;
import static java.util.Collections.singletonList;
import static org.junit.Assert.fail;

public class ApplicationSummarisationServiceSecurityTest extends BaseServiceSecurityTest<ApplicationSummarisationService> {

	@Test
	public void testTotalProjectCostAllowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		service.getTotalProjectCost(null);
	}

	@Test
	public void testTotalProjectCostDeniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			service.getTotalProjectCost(null);
			fail("Should not have been able to get total project cost without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testTotalCostDeniedIfNoGlobalRolesAtAll() {

		try {
			service.getTotalProjectCost(null);
			fail("Should not have been able to get total project cost without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}
	
	@Test
	public void testFundingSoughtAllowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		service.getFundingSought(null);
	}

	@Test
	public void testFundingSoughtDeniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			service.getFundingSought(null);
			fail("Should not have been able to get funding sought without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testFundingSoughtDeniedIfNoGlobalRolesAtAll() {

		try {
			service.getFundingSought(null);
			fail("Should not have been able to get funding sought without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Override
	protected Class<? extends ApplicationSummarisationService> getServiceClass() {
		return TestApplicationSummarisationService.class;
	}

	public static class TestApplicationSummarisationService implements ApplicationSummarisationService {

		@Override
		public ServiceResult<BigDecimal> getTotalProjectCost(Application application) {
			return null;
		}

		@Override
		public ServiceResult<BigDecimal> getFundingSought(Application application) {
			return null;
		}
	}
}
