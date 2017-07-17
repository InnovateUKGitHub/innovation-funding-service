package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;

import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static java.util.Collections.singletonList;
import static org.junit.Assert.fail;

public class ApplicationSummarisationServiceSecurityTest extends BaseServiceSecurityTest<ApplicationSummarisationService> {

	@Test
	public void testTotalProjectCostAllowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		classUnderTest.getTotalProjectCost(null);
	}

	@Test
	public void testTotalProjectCostDeniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.getTotalProjectCost(null);
			fail("Should not have been able to get total project cost without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testTotalCostDeniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.getTotalProjectCost(null);
			fail("Should not have been able to get total project cost without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}
	
	@Test
	public void testFundingSoughtAllowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		classUnderTest.getFundingSought(null);
	}

	@Test
	public void testFundingSoughtDeniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.getFundingSought(null);
			fail("Should not have been able to get funding sought without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testFundingSoughtDeniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.getFundingSought(null);
			fail("Should not have been able to get funding sought without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Override
	protected Class<? extends ApplicationSummarisationService> getClassUnderTest() {
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
