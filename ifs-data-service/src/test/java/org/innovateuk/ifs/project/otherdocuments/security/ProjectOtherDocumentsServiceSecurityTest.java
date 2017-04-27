package org.innovateuk.ifs.project.otherdocuments.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Testing how the secured methods in ProjectService interact with Spring Security
 */
public class ProjectOtherDocumentsServiceSecurityTest extends BaseServiceSecurityTest<ProjectService> {

    private ProjectOtherDocumentsPermissionRules projectPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        projectPermissionRules = getMockPermissionRulesBean(ProjectOtherDocumentsPermissionRules.class);
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

    public static class TestProjectService implements ProjectService {

        static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

        @Override
        public ServiceResult<ProjectResource> getProjectById(@P("projectId") Long projectId) {
            return serviceSuccess(newProjectResource().withId(1L).build());
        }

        @Override
        public ServiceResult<ProjectResource> getByApplicationId(@P("applicationId") Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<List<ProjectResource>> findAll() {
            return null;
        }

        @Override
        public ServiceResult<ProjectResource> createProjectFromApplication(Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions) {
            return null;
        }

		@Override
		public ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId) {
			return null;
		}

		@Override
        public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateProjectAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource projectAddress) {
            return null;
        }

        @Override
        public ServiceResult<List<ProjectResource>> findByUserId(Long userId) {
            return null;
        }

		@Override
		public ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
			return null;
		}

        @Override
        public ServiceResult<List<ProjectUserResource>> getProjectUsers(Long projectId) {
            return serviceSuccess(newProjectUserResource().build(2));
        }

        @Override
        public ServiceResult<Void> submitProjectDetails(Long id, ZonedDateTime date) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isSubmitAllowed(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Void> saveDocumentsSubmitDateTime(Long projectId, ZonedDateTime date) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isOtherDocumentsSubmitAllowed(Long projectId, Long userId) {
            return null;
        }

        @Override
        public ServiceResult<OrganisationResource> getOrganisationByProjectAndUser(Long projectId, Long userId) {
            return null;
        }

        @Override
        public ServiceResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource) {
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

        @Override
        public ServiceResult<ProjectUser> addPartner(Long projectId, Long userId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId) {
            return null;
        }
        @Override
        public ServiceResult<Void> sendGrantOfferLetter(Long projectId) { return null; }

        @Override
        public ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId) { return null; }

        @Override
        public ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId) { return null; }

        @Override
        public ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType) { return null; }

        @Override
        public ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId) { return null; }

        @Override
        public ServiceResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<ProjectUserResource> getProjectManager(Long projectId) {
            return serviceSuccess(newProjectUserResource().withProject(projectId).withRoleName("project-manager").build());
        }

    }
}

