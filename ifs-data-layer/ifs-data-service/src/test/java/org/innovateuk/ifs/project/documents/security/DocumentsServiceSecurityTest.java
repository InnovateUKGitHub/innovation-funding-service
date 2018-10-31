package org.innovateuk.ifs.project.documents.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.documents.transactional.DocumentsService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DocumentsServiceSecurityTest extends BaseServiceSecurityTest<DocumentsService> {

    private DocumentPermissionRules documentPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        documentPermissionRules = getMockPermissionRulesBean(DocumentPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void createDocumentFileEntry() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.createDocumentFileEntry(123L, 2L, null, null), () -> {
            verify(documentPermissionRules).projectManagerCanUploadDocument(project, getLoggedInUser());
            verifyNoMoreInteractions(documentPermissionRules);
        });
    }

    @Test
    public void getFileContents() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getFileContents(123L, 2L), () -> {
            verify(documentPermissionRules).partnersCanDownloadDocument(project, getLoggedInUser());
            verify(documentPermissionRules).internalUserCanDownloadDocument(project, getLoggedInUser());
            verifyNoMoreInteractions(documentPermissionRules);
        });
    }

    @Test
    public void getFileEntryDetails() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getFileEntryDetails(123L, 2L), () -> {
            verify(documentPermissionRules).partnersCanDownloadDocument(project, getLoggedInUser());
            verify(documentPermissionRules).internalUserCanDownloadDocument(project, getLoggedInUser());
            verifyNoMoreInteractions(documentPermissionRules);
        });
    }

    @Test
    public void deleteDocument() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.deleteDocument(123L, 2L), () -> {
            verify(documentPermissionRules).projectManagerCanDeleteDocument(project, getLoggedInUser());
            verifyNoMoreInteractions(documentPermissionRules);
        });
    }


    @Test
    public void submitDocument() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.submitDocument(123L, 2L), () -> {
            verify(documentPermissionRules).projectManagerCanSubmitDocument(project, getLoggedInUser());
            verifyNoMoreInteractions(documentPermissionRules);
        });
    }

    @Test
    public void documentDecision() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.documentDecision(123L, 2L, null), () -> {
            verify(documentPermissionRules).internalAdminCanApproveDocument(project, getLoggedInUser());
            verifyNoMoreInteractions(documentPermissionRules);
        });
    }

    @Override
    protected Class<? extends DocumentsService> getClassUnderTest() {
        return DocumentsService.class;
    }
}


