package com.worth.ifs.application.transactional;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserRoleType;

public class ApplicationSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationSummaryService> {

	@Test
	public void test_applicationSummariesByCompetitionId_allowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		classUnderTest.getApplicationSummariesByCompetitionId(123L, null, 0, 20);
	}

	@Test
	public void test_applicationSummariesByCompetitionId_deniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.getApplicationSummariesByCompetitionId(123L, null, 0, 20);
			fail("Should not have been able to get application summaries without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_applicationSummariesByCompetitionId_deniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.getApplicationSummariesByCompetitionId(123L, null, 0, 20);
			fail("Should not have been able to get application summaries without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_applicationSummariesByCompetitionId_deniedIfNotCorrectGlobalRoles() {

		List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
				.collect(toList());

		nonCompAdminRoles.forEach(role -> {

			setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());

			try {
				classUnderTest.getApplicationSummariesByCompetitionId(123L, null, 0, 20);
				fail("Should not have been able to get application summaries without the global Comp Admin role");
			} catch (AccessDeniedException e) {
				// expected behaviour
			}
		});
	}
	
	@Test
	public void test_submittedApplicationSummariesByClosedCompetitionId_allowedIfGlobalCompAdminRole() {

		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());
		classUnderTest.getSubmittedApplicationSummariesByCompetitionId(123L, null, 0, 20);
	}

	@Test
	public void test_aubmittedApplicationSummariesByClosedCompetitionId_deniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.getSubmittedApplicationSummariesByCompetitionId(123L, null, 0, 20);
			fail("Should not have been able to get application summaries without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_submittedApplicationSummariesByClosedCompeititonId_deniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.getSubmittedApplicationSummariesByCompetitionId(123L, null, 0, 20);
			fail("Should not have been able to get application summaries without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_submittedApplicationSummariesByClosedCompetitionId_deniedIfNotCorrectGlobalRoles() {

		List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
				.collect(toList());

		nonCompAdminRoles.forEach(role -> {

			setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());

			try {
				classUnderTest.getSubmittedApplicationSummariesByCompetitionId(123L, null, 0, 20);
				fail("Should not have been able to get application summaries without the global Comp Admin role");
			} catch (AccessDeniedException e) {
				// expected behaviour
			}
		});
	}
	@Test
	public void test_notSubmittedApplicationSummariesByClosedCompetitionId_allowedIfGlobalCompAdminRole() {

		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());
		classUnderTest.getNotSubmittedApplicationSummariesByCompetitionId(123L, null, 0, 20);
	}

	@Test
	public void test_notSubmittedApplicationSummariesByClosedCompetitionId_deniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.getNotSubmittedApplicationSummariesByCompetitionId(123L, null, 0, 20);
			fail("Should not have been able to get application summaries without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_notSubmittedApplicationSummariesByClosedCompeititonId_deniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.getNotSubmittedApplicationSummariesByCompetitionId(123L, null, 0, 20);
			fail("Should not have been able to get application summaries without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_notSubmittedApplicationSummariesByClosedCompetitionId_deniedIfNotCorrectGlobalRoles() {

		List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
				.collect(toList());

		nonCompAdminRoles.forEach(role -> {

			setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());

			try {
				classUnderTest.getNotSubmittedApplicationSummariesByCompetitionId(123L, null, 0, 20);
				fail("Should not have been able to get application summaries without the global Comp Admin role");
			} catch (AccessDeniedException e) {
				// expected behaviour
			}
		});
	}
	
	@Test
	public void test_feedbackRequiredApplicationSummariesByCompetitionId_allowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		classUnderTest.getFeedbackRequiredApplicationSummariesByCompetitionId(123L, null, 0, 20);
	}

	@Test
	public void test_feedbackRequiredApplicationSummariesByCompetitionId_deniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.getFeedbackRequiredApplicationSummariesByCompetitionId(123L, null, 0, 20);
			fail("Should not have been able to get feedback required application summaries without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_feedbackRequiredApplicationSummariesByCompetitionId_deniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.getFeedbackRequiredApplicationSummariesByCompetitionId(123L, null, 0, 20);
			fail("Should not have been able to get feedback required application summaries without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_feedbackRequiredundedApplicationSummariesByCompetitionId_deniedIfNotCorrectGlobalRoles() {

		List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
				.collect(toList());

		nonCompAdminRoles.forEach(role -> {

			setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());

			try {
				classUnderTest.getFeedbackRequiredApplicationSummariesByCompetitionId(123L, null, 0, 20);
				fail("Should not have been able to get feedback required application summaries without the global Comp Admin role");
			} catch (AccessDeniedException e) {
				// expected behaviour
			}
		});
	}

	@Override
	protected Class<? extends ApplicationSummaryService> getClassUnderTest() {
		return TestApplicationSummaryService.class;
	}

	public static class TestApplicationSummaryService implements ApplicationSummaryService {

		@Override
		public ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId,
				String sortBy, int pageIndex, int pageSize) {
			return null;
		}

		@Override
		public ServiceResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(
				Long competitionId, String sortBy, int pageIndex, int pageSize) {
			return null;
		}

		@Override
		public ServiceResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(
				Long competitionId, String sortBy, int pageIndex, int pageSize) {
			return null;
		}

		@Override
		public ServiceResult<ApplicationSummaryPageResource> getFeedbackRequiredApplicationSummariesByCompetitionId(
				Long competitionId, String sortBy, int pageIndex, int pageSize) {
			return null;
		}
	}
}
