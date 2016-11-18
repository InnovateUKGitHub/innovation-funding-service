package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.security.ApplicationLookupStrategy;
import com.worth.ifs.application.security.ApplicationPermissionRules;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing the Spring Security and CustomPermissionEvaluator integration with AssessorFeedbackService with regards to the
 * security rules that are
 */
public class AssessorFeedbackServiceSecurityTest extends BaseServiceSecurityTest<AssessorFeedbackService> {

	private static final Long COMPETITION_ID = 123L;
	 
    private ApplicationPermissionRules rules;
    private ApplicationLookupStrategy lookupStrategy;
    
    @Before
    public void lookupMockPermissionBeans() {
        rules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        lookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void testCreateAssessorFeedbackFileEntry() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> classUnderTest.createAssessorFeedbackFileEntry(123L, newFileEntryResource().build(), () -> null), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).compAdminCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, getLoggedInUser());
        });
    }

    @Test
    public void testUpdateAssessorFeedbackFileEntry() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> classUnderTest.updateAssessorFeedbackFileEntry(123L, newFileEntryResource().build(), () -> null), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).compAdminCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, getLoggedInUser());
        });
    }

    @Test
    public void testDeleteAssessorFeedbackFileEntry() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> classUnderTest.deleteAssessorFeedbackFileEntry(123L), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(application, getLoggedInUser());
        });
    }

    @Test
    public void testGetAssessorFeedbackFileEntryContents() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> classUnderTest.getAssessorFeedbackFileEntryContents(123L), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).compAdminCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, getLoggedInUser());
            verify(rules).applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, getLoggedInUser());
        });
    }

    @Test
    public void testGetAssessorFeedbackFileEntryDetails() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> classUnderTest.getAssessorFeedbackFileEntryDetails(123L), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).compAdminCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, getLoggedInUser());
            verify(rules).applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, getLoggedInUser());
        });
    }
    
    @Test
	public void test_assessorFeedbackUploaded_allowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		classUnderTest.assessorFeedbackUploaded(COMPETITION_ID);
	}

	@Test
	public void test_assessorFeedbackUploaded_deniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.assessorFeedbackUploaded(COMPETITION_ID);
			fail("Should not have been able to check if assessor feedback uploaded without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_assessorFeedbackUploaded_deniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.assessorFeedbackUploaded(COMPETITION_ID);
			fail("Should not have been able to check if assessor feedback uploaded without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_assessorFeedbackUploaded_deniedIfNotCorrectGlobalRoles() {

		List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
				.collect(toList());

		nonCompAdminRoles.forEach(role -> {

			setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());

			try {
				classUnderTest.assessorFeedbackUploaded(COMPETITION_ID);
				fail("Should not have been able to check if assessor feedback uploaded without the global Comp Admin role");
			} catch (AccessDeniedException e) {
				// expected behaviour
			}
		});
	}

	@Test
	public void test_submitAssessorFeedback_allowedIfGlobalCompAdminRole() {

		RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
		setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
		classUnderTest.submitAssessorFeedback(COMPETITION_ID);
	}

	@Test
	public void test_submitAssessorFeedback_deniedIfNotLoggedIn() {

		setLoggedInUser(null);
		try {
			classUnderTest.submitAssessorFeedback(COMPETITION_ID);
			fail("Should not have been able to submit assessor feedback without first logging in");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_submitAssessorFeedback_deniedIfNoGlobalRolesAtAll() {

		try {
			classUnderTest.submitAssessorFeedback(COMPETITION_ID);
			fail("Should not have been able to submit assessor feedback without the global comp admin role");
		} catch (AccessDeniedException e) {
			// expected behaviour
		}
	}

	@Test
	public void test_submitAssessorFeedback_deniedIfNotCorrectGlobalRoles() {

		List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
				.collect(toList());

		nonCompAdminRoles.forEach(role -> {

			setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());

			try {
				classUnderTest.submitAssessorFeedback(COMPETITION_ID);
				fail("Should not have been able to submit assessor feedback without the global Comp Admin role");
			} catch (AccessDeniedException e) {
				// expected behaviour
			}
		});
	}
	
    @Override
    protected Class<? extends AssessorFeedbackService> getClassUnderTest() {
        return TestAssessorFeedbackService.class;
    }

    public static class TestAssessorFeedbackService implements AssessorFeedbackService {

        @Override
        public ServiceResult<FileEntryResource> createAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<FileAndContents> getAssessorFeedbackFileEntryContents(long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteAssessorFeedbackFileEntry(long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> getAssessorFeedbackFileEntryDetails(long applicationId) {
            return null;
        }

		@Override
		public ServiceResult<Boolean> assessorFeedbackUploaded(long competitionId) {
			return null;
		}

		@Override
		public ServiceResult<Void> submitAssessorFeedback(long competitionId) {
			return null;
		}

        @Override
        public ServiceResult<Void> notifyLeadApplicantsOfAssessorFeedback(long competitionId) {
            return null;
        }
    }
}
