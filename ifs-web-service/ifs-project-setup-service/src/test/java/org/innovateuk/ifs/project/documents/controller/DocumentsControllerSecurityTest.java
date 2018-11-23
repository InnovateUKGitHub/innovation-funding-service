package org.innovateuk.ifs.project.documents.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class DocumentsControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<DocumentsController> {

    private ProjectLookupStrategy projectLookupStrategy;
    private ProjectCompositeId projectCompositeId;

    @Override
    @Before
    public void lookupPermissionRules() {
        super.lookupPermissionRules();
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        projectCompositeId = ProjectCompositeId.id(123l);
        when(projectLookupStrategy.getProjectCompositeId(projectCompositeId.id())).thenReturn(projectCompositeId);
    }

    @Override
    protected Class<? extends DocumentsController> getClassUnderTest() {
        return DocumentsController.class;
    }

    @Test
    public void viewAllDocuments() {
        assertSecured(() -> classUnderTest.viewAllDocuments(projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.canAccessDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void viewDocument() {
        assertSecured(() -> classUnderTest.viewDocument(projectCompositeId.id(), 2L, null, null),
                permissionRules -> permissionRules.canAccessDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void uploadDocument() {
        assertSecured(() -> classUnderTest.uploadDocument(projectCompositeId.id(), 2L, null, null, null, null, null),
                permissionRules -> permissionRules.projectManagerCanEditDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void downloadDocument() {
        assertSecured(() -> classUnderTest.downloadDocument(projectCompositeId.id(), 2L),
                permissionRules -> permissionRules.canAccessDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void deleteDocument() {
        assertSecured(() -> classUnderTest.deleteDocument(projectCompositeId.id(), 2L, null, null, null, null, null),
                permissionRules -> permissionRules.projectManagerCanEditDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void submitDocument() {
        assertSecured(() -> classUnderTest.submitDocument(projectCompositeId.id(), 2L, null, null, null, null, null),
                permissionRules -> permissionRules.projectManagerCanEditDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }
}
