package org.innovateuk.ifs.project.otherdocuments.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.otherdocuments.transactional.OtherDocumentsService;
import org.innovateuk.ifs.project.otherdocuments.transactional.OtherDocumentsServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.*;

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
    protected Class<? extends OtherDocumentsService> getClassUnderTest() {
        return OtherDocumentsServiceImpl.class;
    }
}

