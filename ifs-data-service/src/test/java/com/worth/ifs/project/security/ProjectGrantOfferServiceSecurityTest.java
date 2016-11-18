package com.worth.ifs.project.security;


import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.transactional.ProjectGrantOfferService;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.function.Supplier;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ProjectGrantOfferServiceSecurityTest extends BaseServiceSecurityTest<ProjectGrantOfferService> {

    private ProjectGrantOfferPermissionRules projectGrantOfferPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        projectGrantOfferPermissionRules = getMockPermissionRulesBean(ProjectGrantOfferPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testSignedGetGrantOfferLetterFileEntryDetails() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getSignedGrantOfferLetterFileEntryDetails(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanViewGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }

    @Test
    public void testGetGrantOfferLetterFileEntryDetails() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getGrantOfferLetterFileEntryDetails(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanViewGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }


    @Test
    public void testGetAdditionalContractFileEntryDetails() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getAdditionalContractFileEntryDetails(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanViewGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testCreateSignedGrantOfferLetterFileEntry() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.createSignedGrantOfferLetterFileEntry(projectId, null, null), () -> {
            verify(projectGrantOfferPermissionRules).leadPartnerCanUploadGrantOfferLetter(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).projectManagerCanUploadGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testGetGrantOfferLetterFileEntryContents() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getGrantOfferLetterFileAndContents(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanDownloadGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }

    @Test
    public void testGetSignedGrantOfferLetterFileEntryContents() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getSignedGrantOfferLetterFileAndContents(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanDownloadGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }

    @Test
    public void testGetAdditionalContractFileEntryContents() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getAdditionalContractFileAndContents(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanDownloadGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testSubmitGrantOfferLetter() {
        final Long projectId = 1L;

        assertAccessDenied(() -> classUnderTest.submitGrantOfferLetter(projectId), () -> {
            verify(projectGrantOfferPermissionRules).projectManagerSubmitGrantOfferLetter(projectId, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }


    @Override
    protected Class<? extends ProjectGrantOfferService> getClassUnderTest() {
        return TestProjectGrantOfferService.class;
    }

    public static class TestProjectGrantOfferService implements ProjectGrantOfferService {

        @Override
        public ServiceResult<FileAndContents> getSignedGrantOfferLetterFileAndContents(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FileAndContents> getGrantOfferLetterFileAndContents(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FileAndContents> getAdditionalContractFileAndContents(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> getSignedGrantOfferLetterFileEntryDetails(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> getGrantOfferLetterFileEntryDetails(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> getAdditionalContractFileEntryDetails(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> createSignedGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> createGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> generateGrantOfferLetter(Long projectId, FileEntryResource fileEntryResource) {
            return null;
        }

        @Override
        public ServiceResult<FileEntryResource> createAdditionalContractFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateSignedGrantOfferLetterFile(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
            return null;
        }

        @Override
        public ServiceResult<Void> submitGrantOfferLetter(Long projectId) {
            return null;
        }
    }
}
