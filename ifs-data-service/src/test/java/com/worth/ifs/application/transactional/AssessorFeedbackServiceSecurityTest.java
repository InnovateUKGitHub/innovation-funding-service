package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.application.security.ApplicationLookupStrategy;
import com.worth.ifs.application.security.ApplicationPermissionRules;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.function.Supplier;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing the Spring Security and CustomPermissionEvaluator integration with AssessorFeedbackService with regards to the
 * security rules that are
 */
public class AssessorFeedbackServiceSecurityTest extends BaseServiceSecurityTest<AssessorFeedbackService> {

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

        assertAccessDenied(() -> service.createAssessorFeedbackFileEntry(123L, newFileEntryResource().build(), () -> null), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).compAdminCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, getLoggedInUser());
        });
    }

    @Test
    public void testUpdateAssessorFeedbackFileEntry() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> service.updateAssessorFeedbackFileEntry(123L, newFileEntryResource().build(), () -> null), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).compAdminCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(application, getLoggedInUser());
        });
    }

    @Test
    public void testDeleteAssessorFeedbackFileEntry() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> service.deleteAssessorFeedbackFileEntry(123L), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(application, getLoggedInUser());
        });
    }

    @Test
    public void testGetAssessorFeedbackFileEntry() {

        ApplicationResource application = newApplicationResource().build();
        when(lookupStrategy.getApplicationResource(123L)).thenReturn(application);

        assertAccessDenied(() -> service.getAssessorFeedbackFileEntry(123L), () -> {
            verify(lookupStrategy).getApplicationResource(123L);
            verify(rules).compAdminCanSeeAndDownloadAllAssessorFeedback(application, getLoggedInUser());
            verify(rules).leadApplicantCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(application, getLoggedInUser());
        });
    }

    @Override
    protected Class<? extends AssessorFeedbackService> getServiceClass() {
        return TestAssessorFeedbackService.class;
    }

    public static class TestAssessorFeedbackService implements AssessorFeedbackService {

        @Override
        public ServiceResult<AssessorFeedbackResource> findOne(Long id) {
            return null;
        }

        @Override
        public ServiceResult<AssessorFeedbackResource> findByAssessorId(Long assessorId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> createAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<Pair<FileEntryResource, Supplier<InputStream>>> getAssessorFeedbackFileEntry(long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> updateAssessorFeedbackFileEntry(long applicationId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteAssessorFeedbackFileEntry(long applicationId) {
            return null;
        }
    }
}
