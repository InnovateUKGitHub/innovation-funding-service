package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.*;

public class ApplicationFundingServiceSecurityTest extends BaseServiceSecurityTest<ApplicationFundingService> {

	@Test
	public void testNotifyLeadApplicantAllowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		classUnderTest.notifyLeadApplicantsOfFundingDecisions(new FundingNotificationResource());
	}

	@Test
	public void testNotifyLeadApplicantDeniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.notifyLeadApplicantsOfFundingDecisions(new FundingNotificationResource());
			fail("Should not have been able to notify lead applicants of funding decision without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testNotifyLeadApplicantDeniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.notifyLeadApplicantsOfFundingDecisions(new FundingNotificationResource());
			fail("Should not have been able to notify lead applicants of funding decision without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void testNotifyLeadApplicantDeniedIfNotCorrectGlobalRoles() {

		List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
				.collect(toList());

		nonCompAdminRoles.forEach(role -> {

			setLoggedInUser(
					newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
			try {
				classUnderTest.notifyLeadApplicantsOfFundingDecisions(new FundingNotificationResource());
				fail("Should not have been able to notify lead applicants of funding decision without the global Comp Admin role");
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
		public ServiceResult<Void> notifyLeadApplicantsOfFundingDecisions(FundingNotificationResource fundingNotificationResource) {
			return null;
		}

		@Override
		public ServiceResult<Void> saveFundingDecisionData(Long competitionId, Map<Long, FundingDecision> decision) {
			return null;
		}

	}
}
