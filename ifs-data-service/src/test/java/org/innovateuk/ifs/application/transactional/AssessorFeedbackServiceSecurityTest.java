package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
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
            verify(rules).internalUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, getLoggedInUser());
        });
    }

    @Test
    public void testUpdateAssessorFeedbackFileEntry() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> classUnderTest.updateAssessorFeedbackFileEntry(123L, newFileEntryResource().build(), () -> null), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).internalUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, getLoggedInUser());
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
            verify(rules).internalUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, getLoggedInUser());
            verify(rules).applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, getLoggedInUser());
        });
    }

    @Test
    public void testGetAssessorFeedbackFileEntryDetails() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> classUnderTest.getAssessorFeedbackFileEntryDetails(123L), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).internalUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(application, getLoggedInUser());
            verify(rules).applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, getLoggedInUser());
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
    }
}
