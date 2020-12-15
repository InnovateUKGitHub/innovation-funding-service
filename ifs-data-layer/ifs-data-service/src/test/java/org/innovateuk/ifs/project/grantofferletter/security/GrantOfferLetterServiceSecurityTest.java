package org.innovateuk.ifs.project.grantofferletter.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterServiceImpl;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.Mockito.*;

public class GrantOfferLetterServiceSecurityTest extends BaseServiceSecurityTest<GrantOfferLetterService> {

    private GrantOfferLetterPermissionRules projectGrantOfferPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        projectGrantOfferPermissionRules = getMockPermissionRulesBean(GrantOfferLetterPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void signedGetGrantOfferLetterFileEntryDetails() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getSignedGrantOfferLetterFileEntryDetails(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).internalUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanViewGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).stakeholdersCanViewGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).monitoringOfficerCanViewGrantOfferLetter(project,
                    getLoggedInUser());

            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }

    @Test
    public void getSignedAdditionalContractFileEntryDetails() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getSignedAdditionalContractFileEntryDetails(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).internalUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanViewGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).stakeholdersCanViewGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).monitoringOfficerCanViewGrantOfferLetter(project,
                    getLoggedInUser());

            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }

    @Test
    public void getGrantOfferLetterFileEntryDetails() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getGrantOfferLetterFileEntryDetails(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).internalUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanViewGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).stakeholdersCanViewGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).monitoringOfficerCanViewGrantOfferLetter(project,
                    getLoggedInUser());

            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }

    @Test
    public void getAdditionalContractFileEntryDetails() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getAdditionalContractFileEntryDetails(projectId), () -> {

            verify(projectGrantOfferPermissionRules).partnersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).internalUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanViewGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).stakeholdersCanViewGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).monitoringOfficerCanViewGrantOfferLetter(project,
                    getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void createSignedGrantOfferLetterFileEntry() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.createSignedGrantOfferLetterFileEntry(projectId, null, null), () -> {
            verify(projectGrantOfferPermissionRules).leadPartnerCanUploadGrantOfferLetter(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).projectManagerCanUploadGrantOfferLetter(project, getLoggedInUser
                    ());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void createSignedAdditionalContractFileEntry() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.createSignedAdditionalContractFileEntry(projectId, null, null), () -> {
            verify(projectGrantOfferPermissionRules).leadPartnerCanUploadSignedAdditionalContract(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).financeContactCanUploadSignedAdditionalContract(project, getLoggedInUser
                    ());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void getGrantOfferLetterFileEntryContents() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getGrantOfferLetterFileAndContents(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).internalUsersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanDownloadGrantOfferLetter(project, getLoggedInUser
                    ());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).stakeholdersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }

    @Test
    public void getSignedGrantOfferLetterFileEntryContents() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getSignedGrantOfferLetterFileAndContents(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).internalUsersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanDownloadGrantOfferLetter(project, getLoggedInUser
                    ());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).stakeholdersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }

    @Test
    public void getAdditionalContractFileEntryContents() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getAdditionalContractFileAndContents(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).internalUsersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanDownloadGrantOfferLetter(project, getLoggedInUser
                    ());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).stakeholdersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void getSignedAdditionalContractFileEntryContents() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getSignedAdditionalContractFileAndContents(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).internalUsersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanDownloadGrantOfferLetter(project, getLoggedInUser
                    ());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).stakeholdersCanDownloadGrantOfferLetter(project,
                    getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });

    }

    @Test
    public void submitGrantOfferLetter() {
        final ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        when(projectLookupStrategy.getProjectCompositeId(projectId.id())).thenReturn(projectId);
        assertAccessDenied(() -> classUnderTest.submitGrantOfferLetter(projectId.id()), () -> {
            verify(projectGrantOfferPermissionRules).projectManagerSubmitGrantOfferLetter(projectId, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void deleteSignedGrantOfferLetterFileEntry() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.removeSignedGrantOfferLetterFileEntry(projectId), () -> {
            verify(projectGrantOfferPermissionRules).leadPartnerCanDeleteSignedGrantOfferLetter(project,
                    getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void deleteSignedAdditionalContractFileEntry() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.removeSignedAdditionalContractFileEntry(projectId), () -> {
            verify(projectGrantOfferPermissionRules).leadPartnerCanDeleteSignedGrantOfferLetter(project,
                    getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void sendGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.sendGrantOfferLetter(123L), () -> {
            verify(projectGrantOfferPermissionRules).internalUserCanSendGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void approveSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.approveOrRejectSignedGrantOfferLetter(123L, new
                GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null)), () -> {
            verify(projectGrantOfferPermissionRules).internalUsersCanApproveOrRejectSignedGrantOfferLetter(project,
                    getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void getGrantOfferLetterState() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.getGrantOfferLetterState(123L), () -> {
            verify(projectGrantOfferPermissionRules).internalAdminUserCanViewSendGrantOfferLetterStatus(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).supportUserCanViewSendGrantOfferLetterStatus(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).innovationLeadUserCanViewSendGrantOfferLetterStatus(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).stakeholdersCanViewSendGrantOfferLetterStatus(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).externalUserCanViewSendGrantOfferLetterStatus(project,
                    getLoggedInUser());
            verify(projectGrantOfferPermissionRules).monitoringOfficerCanViewSendGrantOfferLetterStatus(project,
                    getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Override
    protected Class<? extends GrantOfferLetterService> getClassUnderTest() {
        return GrantOfferLetterServiceImpl.class;
    }
}
