package org.innovateuk.ifs.project.otherdocuments.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.otherdocuments.transactional.OtherDocumentsService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Testing how the secured methods in ProjectOtherDocumentsService interact with Spring Security
 */
public class OtherDocumentsServiceSecurityTest extends BaseServiceSecurityTest<OtherDocumentsService> {

    private OtherDocumentsPermissionRules projectPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        projectPermissionRules = getMockPermissionRulesBean(OtherDocumentsPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testCreateCollaborationAgreementFileEntry() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.createCollaborationAgreementFileEntry(123L, null, null), () -> {
            verify(projectPermissionRules).leadPartnersCanUploadOtherDocuments(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testGetCollaborationAgreementFileEntryDetails() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getCollaborationAgreementFileEntryDetails(123L), () -> {
            verify(projectPermissionRules).partnersCanViewOtherDocumentsDetails(project, getLoggedInUser());
            verify(projectPermissionRules).internalUserCanViewOtherDocumentsDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testGetCollaborationAgreementFileEntryContents() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getCollaborationAgreementFileContents(123L), () -> {
            verify(projectPermissionRules).internalUserCanDownloadOtherDocuments(project, getLoggedInUser());
            verify(projectPermissionRules).partnersCanDownloadOtherDocuments(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testDeleteCollaborationAgreementFileEntry() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.deleteCollaborationAgreementFile(123L), () -> {
            verify(projectPermissionRules).leadPartnersCanDeleteOtherDocuments(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testCreateExploitationPlanFileEntry() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.createExploitationPlanFileEntry(123L, null, null), () -> {
            verify(projectPermissionRules).leadPartnersCanUploadOtherDocuments(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testGetExploitationPlanFileEntryDetails() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getExploitationPlanFileEntryDetails(123L), () -> {
            verify(projectPermissionRules).internalUserCanViewOtherDocumentsDetails(project, getLoggedInUser());
            verify(projectPermissionRules).partnersCanViewOtherDocumentsDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testGetExploitationPlanFileEntryContents() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getExploitationPlanFileContents(123L), () -> {
            verify(projectPermissionRules).internalUserCanDownloadOtherDocuments(project, getLoggedInUser());
            verify(projectPermissionRules).partnersCanDownloadOtherDocuments(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testDeleteExploitationPlanFileEntry() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.deleteExploitationPlanFile(123L), () -> {
            verify(projectPermissionRules).leadPartnersCanDeleteOtherDocuments(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testAcceptOrRejectOtherDocuments() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.acceptOrRejectOtherDocuments(123L, true), () -> {
            verify(projectPermissionRules).internalUserCanAcceptOrRejectOtherDocuments(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Override
    protected Class<TestProjectService> getClassUnderTest() {
        return TestProjectService.class;
    }

    public static class TestProjectService implements OtherDocumentsService {

        @Override
        public ServiceResult<Void> saveDocumentsSubmitDateTime(Long projectId, ZonedDateTime date) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isOtherDocumentsSubmitAllowed(Long projectId, Long userId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> createCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<FileAndContents> getCollaborationAgreementFileContents(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> getCollaborationAgreementFileEntryDetails(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateCollaborationAgreementFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteCollaborationAgreementFile(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> createExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<FileAndContents> getExploitationPlanFileContents(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> getExploitationPlanFileEntryDetails(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateExploitationPlanFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteExploitationPlanFile(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptOrRejectOtherDocuments(Long projectId, Boolean approved) {
            return null;
        }
    }
}

