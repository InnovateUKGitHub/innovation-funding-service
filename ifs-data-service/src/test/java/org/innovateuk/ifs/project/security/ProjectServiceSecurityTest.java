package org.innovateuk.ifs.project.security;

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
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.project.transactional.SaveMonitoringOfficerResult;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.method.P;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectService interact with Spring Security
 */
public class ProjectServiceSecurityTest extends BaseServiceSecurityTest<ProjectService> {

    private ProjectPermissionRules projectPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        projectPermissionRules = getMockPermissionRulesBean(ProjectPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testGetProjectById() {
        final Long projectId = 1L;

        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(newProjectResource().build());

        assertAccessDenied(
                () -> classUnderTest.getProjectById(projectId),
                () -> {
                    verify(projectPermissionRules, times(1)).partnersOnProjectCanView(isA(ProjectResource.class), isA(UserResource.class));
                    verify(projectPermissionRules, times(1)).internalUsersCanViewProjects(isA(ProjectResource.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void testCreateProjectFromApplicationAllowedIfCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());
        classUnderTest.createProjectFromApplication(123L);
    }

    @Test
    public void testCreateProjectFromApplicationDeniedForApplicant() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(APPLICANT).build())).build());
        try {
            classUnderTest.createProjectFromApplication(123L);
            fail("Should not have been able to create project from application as applicant");
        } catch(AccessDeniedException ade){
            //expected behaviour
        }
    }

    @Test
    public void testCreateProjectFromFundingDecisionsAllowedIfGlobalCompAdminRole() {
        RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
        classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
    }

    @Test
    public void testCreateProjectFromFundingDecisionsAllowedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
            Assert.fail("Should not have been able to create project from application without the global Comp Admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testCreateProjectFromFundingDecisionsDeniedIfNotCorrectGlobalRoles() {

        List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
                Assert.fail("Should not have been able to create project from application without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testUpdateProjectStartDate() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.updateProjectStartDate(123L, LocalDate.now()), () -> {
            verify(projectPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testUpdateProjectAddress() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(456L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.updateProjectAddress(123L, 456L, OrganisationAddressType.ADD_NEW, newAddressResource().build()), () -> {
            verify(projectPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testUpdateFinanceContact() {

        ProjectResource project = newProjectResource().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(123L, 456L);

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.updateFinanceContact(composite, 789L), () -> {
            verify(projectPermissionRules).partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testSetProjectManager() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.setProjectManager(123L, 456L), () -> {
            verify(projectPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testGetProjectUsers() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getProjectUsers(123L), () -> {
            verify(projectPermissionRules).partnersOnProjectCanView(project, getLoggedInUser());
            verify(projectPermissionRules).internalUsersCanViewProjects(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
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


    @Test
    public void testAddPartnerDeniedIfNotSystemRegistrar() {
        EnumSet<UserRoleType> nonSystemRegistrationRoles = complementOf(of(SYSTEM_REGISTRATION_USER));
        nonSystemRegistrationRoles.forEach(role -> {
            setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.addPartner(1L, 2L, 3L);
                Assert.fail("Should not have been able to add a partner without the system registrar role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
    @Test
    public void testAddPartnerAllowedIfSystemRegistrar() {
            setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build())).build());
            classUnderTest.addPartner(1L, 2L, 3L);
            // There should be no exception thrown
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotCorrectGlobalRolesOrASystemRegistrar() {
        EnumSet<UserRoleType> nonSystemRegistrationRoles = complementOf(of(SYSTEM_REGISTRATION_USER));
        nonSystemRegistrationRoles.forEach(role -> {
            setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.addPartner(1L, 2L, 3L);
                Assert.fail("Should not have been able to add a partner without the system registrar role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testGetProjectTeamStatus(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getProjectTeamStatus(123L, Optional.empty()), () -> {
            verify(projectPermissionRules).partnersCanViewTeamStatus(project, getLoggedInUser());
            verify(projectPermissionRules).internalUsersCanViewTeamStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testSendGrantOfferLetter(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.sendGrantOfferLetter(123L), () -> {
            verify(projectPermissionRules).internalUserCanSendGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testIsSendGrantOfferLetterAllowed(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.isSendGrantOfferLetterAllowed(123L), () -> {
            verify(projectPermissionRules).internalUserCanSendGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testIsSendGrantOfferLetterAlreadySent(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.isGrantOfferLetterAlreadySent(123L), () -> {
            verify(projectPermissionRules).internalUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectPermissionRules).externalUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testApproveSignedGrantOfferLetter(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.approveOrRejectSignedGrantOfferLetter(123L, ApprovalType.APPROVED), () -> {
            verify(projectPermissionRules).internalUsersCanApproveSignedGrantOfferLetter(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testSignedGrantOfferLetterApproved(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.isSignedGrantOfferLetterApproved(123L), () -> {
            verify(projectPermissionRules).partnersOnProjectCanViewGrantOfferApprovedStatus(project, getLoggedInUser());
            verify(projectPermissionRules).internalUsersCanViewGrantOfferApprovedStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testGetGrantOfferLetterWorkflowState() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(() -> classUnderTest.getGrantOfferLetterWorkflowState(123L), () -> {
            verify(projectPermissionRules).internalUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verify(projectPermissionRules).externalUserCanViewSendGrantOfferLetterStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testGetProjectManager(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(
                () -> classUnderTest.getProjectById(123L),
                () -> {
                    verify(projectPermissionRules, times(1)).partnersOnProjectCanView(isA(ProjectResource.class), isA(UserResource.class));
                    verify(projectPermissionRules, times(1)).internalUsersCanViewProjects(isA(ProjectResource.class), isA(UserResource.class));
                    verifyNoMoreInteractions(projectPermissionRules);
                }
        );
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

