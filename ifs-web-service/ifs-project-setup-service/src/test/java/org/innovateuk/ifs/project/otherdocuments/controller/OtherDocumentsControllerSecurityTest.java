package org.innovateuk.ifs.project.otherdocuments.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class OtherDocumentsControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<OtherDocumentsController> {

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
    protected Class<? extends OtherDocumentsController> getClassUnderTest() {
        return OtherDocumentsController.class;
    }

    @Test
    public void testViewOtherDocumentsPage() {
        assertSecured(() -> classUnderTest.viewOtherDocumentsPage(projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testDownloadCollaborationAgreementFile() {
        assertSecured(() -> classUnderTest.downloadCollaborationAgreementFile(projectCompositeId.id()),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testDownloadExploitationPlanFile() {
        assertSecured(() -> classUnderTest.downloadExploitationPlanFile(projectCompositeId.id()),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testRemoveCollaborationAgreementFile() {
        assertSecured(() -> classUnderTest.removeCollaborationAgreementFile(projectCompositeId.id(), null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testRemoveExploitationPlanFile() {
        assertSecured(() -> classUnderTest.removeExploitationPlanFile(projectCompositeId.id(), null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testSubmitPartnerDocuments() {
        assertSecured(() -> classUnderTest.submitPartnerDocuments(null, projectCompositeId.id()),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testUploadCollaborationAgreementFile() {
        assertSecured(() -> classUnderTest.uploadCollaborationAgreementFile(projectCompositeId.id(), null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testUploadExploitationPlanFile() {
        assertSecured(() -> classUnderTest.uploadExploitationPlanFile(projectCompositeId.id(), null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testViewConfirmDocumentsPage() {
        assertSecured(() -> classUnderTest.viewConfirmDocumentsPage(projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.projectManagerCanSubmitOtherDocumentsSection(eq(projectCompositeId), isA(UserResource.class)));
    }
}
