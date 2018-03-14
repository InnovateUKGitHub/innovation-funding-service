package org.innovateuk.ifs.project.grantofferletter.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
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
    public void testSignedGetGrantOfferLetterFileEntryDetails() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getSignedGrantOfferLetterFileEntryDetails(projectId), () -> {
            verify(projectGrantOfferPermissionRules).partnersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).internalUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanViewGrantOfferLetter(project, getLoggedInUser());

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

            verify(projectGrantOfferPermissionRules).internalUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanViewGrantOfferLetter(project, getLoggedInUser());

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

            verify(projectGrantOfferPermissionRules).internalUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanViewGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanViewGrantOfferLetter(project, getLoggedInUser());

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

            verify(projectGrantOfferPermissionRules).internalUsersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanDownloadGrantOfferLetter(project, getLoggedInUser());

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

            verify(projectGrantOfferPermissionRules).internalUsersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanDownloadGrantOfferLetter(project, getLoggedInUser());

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

            verify(projectGrantOfferPermissionRules).internalUsersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).supportUsersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verify(projectGrantOfferPermissionRules).innovationLeadUsersCanDownloadGrantOfferLetter(project, getLoggedInUser());

            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testSubmitGrantOfferLetter() {
        final ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        when(projectLookupStrategy.getProjectCompositeId(projectId.id())).thenReturn(projectId);
        assertAccessDenied(() -> classUnderTest.submitGrantOfferLetter(projectId.id()), () -> {
            verify(projectGrantOfferPermissionRules).projectManagerSubmitGrantOfferLetter(projectId, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testGenerateGrantOfferLetterDeniedIfNotCorrectGlobalRoles() {

        final Long projectId = 1L;

        FileEntryResource fileEntryResource = newFileEntryResource().build();

        List<UserRoleType> nonCompAdminRoles = Arrays.stream(UserRoleType.values()).filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.generateGrantOfferLetter(projectId, fileEntryResource);
                Assert.fail("Should not have been able to generate GOL without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testGenerateGrantOfferLetterIfReadyDeniedIfNotCorrectGlobalRoles() {

        final Long projectId = 1L;

        List<UserRoleType> nonCompAdminRoles = Arrays.stream(UserRoleType.values()).filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.generateGrantOfferLetterIfReady(projectId);
                Assert.fail("Should not have been able to generate GOL automatically without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testDeleteSignedGrantOfferLetterFileEntry() {

        final Long projectId = 1L;

        ProjectResource project = newProjectResource().build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.removeSignedGrantOfferLetterFileEntry(projectId), () -> {
            verify(projectGrantOfferPermissionRules).leadPartnerCanDeleteSignedGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testSendGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.sendGrantOfferLetter(123L), () -> {
            verify(projectGrantOfferPermissionRules).internalUserCanSendGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testIsSendGrantOfferLetterAllowed() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.isSendGrantOfferLetterAllowed(123L), () -> {
            verify(projectGrantOfferPermissionRules).internalUserCanSendGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testIsSendGrantOfferLetterAlreadySent() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.isGrantOfferLetterAlreadySent(123L), () -> {
            verify(projectGrantOfferPermissionRules).internalAdminUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).supportUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).innovationLeadUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).externalUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testApproveSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.approveOrRejectSignedGrantOfferLetter(123L, new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null)), () -> {
            verify(projectGrantOfferPermissionRules).internalUsersCanApproveSignedGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testSignedGrantOfferLetterApproved() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.isSignedGrantOfferLetterApproved(123L), () -> {
            verify(projectGrantOfferPermissionRules).partnersOnProjectCanViewSignedGrantOfferLetterApprovedStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).internalUsersCanViewSignedGrantOfferLetterApprovedStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testIsSignedGrantOfferLetterRejected() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.isSignedGrantOfferLetterRejected(123L), () -> {
            verify(projectGrantOfferPermissionRules).internalUsersCanViewSignedGrantOfferLetterRejectedStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).projectManagerCanViewSignedGrantOfferLetterRejectedStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testGetGrantOfferLetterWorkflowState() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.getGrantOfferLetterWorkflowState(123L), () -> {
            verify(projectGrantOfferPermissionRules).internalAdminUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).supportUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).innovationLeadUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).externalUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Test
    public void testGetGrantOfferLetterState() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.getGrantOfferLetterState(123L), () -> {
            verify(projectGrantOfferPermissionRules).internalAdminUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).supportUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).innovationLeadUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectGrantOfferPermissionRules).externalUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(projectGrantOfferPermissionRules);
        });
    }

    @Override
    protected Class<? extends GrantOfferLetterService> getClassUnderTest() {
        return TestGrantOfferLetterService.class;
    }

    public static class TestGrantOfferLetterService implements GrantOfferLetterService {

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
        public ServiceResult<Void> removeGrantOfferLetterFileEntry(Long projectId) {
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

        @Override
        public ServiceResult<Void> generateGrantOfferLetterIfReady(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Void> removeSignedGrantOfferLetterFileEntry(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Void> sendGrantOfferLetter(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, GrantOfferLetterApprovalResource grantOfferLetterApprovalResource) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isSignedGrantOfferLetterRejected(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<GrantOfferLetterStateResource> getGrantOfferLetterState(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<GrantOfferLetterState> getGrantOfferLetterWorkflowState(Long projectId) {
            return null;
        }
    }
}
