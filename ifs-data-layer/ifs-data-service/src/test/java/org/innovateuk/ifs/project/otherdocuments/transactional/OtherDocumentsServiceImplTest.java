package org.innovateuk.ifs.project.otherdocuments.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_ALREADY_COMPLETE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OtherDocumentsServiceImplTest extends BaseServiceUnitTest<OtherDocumentsService> {

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;

    private Application application;
    private Organisation organisation;
    private Role leadApplicantRole;

    private User user;
    private ProcessRole leadApplicantProcessRole;
    private ProjectUser leadPartnerProjectUser;
    private Project project;

    private static final String webBaseUrl = "https://ifs-local-dev/dashboard";

    @Before
    public void setUp() {

        organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        leadApplicantRole = newRole(LEADAPPLICANT).build();


        user = newUser().
                withId(userId).
                build();

        leadApplicantProcessRole = newProcessRole().
                withOrganisationId(organisation.getId()).
                withRole(leadApplicantRole).
                withUser(user).
                build();

        leadPartnerProjectUser = newProjectUser().
                withOrganisation(organisation).
                withRole(PROJECT_PARTNER).
                withUser(user).
                build();

        application = newApplication().
                withId(applicationId).
                withProcessRoles(leadApplicantProcessRole).
                withName("My Application").
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                build();

        project = newProject().
                withId(projectId).
                withApplication(application).
                withProjectUsers(singletonList(leadPartnerProjectUser)).
                build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());
    }

    @Test
    public void testCannotSubmitDocumentsAlreadySubmitted() {

        Long projectId = 1L;
        ProjectUser projectUserToSet = newProjectUser()
                .withId(1L)
                .withUser(newUser().withId(1L).build())
                .withRole(PROJECT_MANAGER)
                .build();

        List<ProjectUser> pu = Collections.singletonList(projectUserToSet);

        Project projectInDB = newProject().withId(projectId).withProjectUsers(pu)
                .withOtherDocumentsApproved(ApprovalType.UNSET).withOtherDocumentsSubmittedDate(ZonedDateTime.now()).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        ServiceResult<Boolean> result = service.isOtherDocumentsSubmitAllowed(projectId, 1L);

        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());

        assertThat(projectInDB.getOtherDocumentsApproved(), Matchers.equalTo(ApprovalType.UNSET));

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsWhenProjectNotInDB() {

        Long projectId = 1L;

        when(projectRepositoryMock.findOne(projectId)).thenReturn(null);

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, true);

        assertTrue(result.isFailure());

        assertTrue(result.getFailure().is(CommonErrors.notFoundError(Project.class, projectId)));

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsWithoutDecisionError() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, null);

        assertTrue(result.isFailure());

        assertThat(projectInDB.getOtherDocumentsApproved(), Matchers.equalTo(ApprovalType.UNSET));

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsAlreadyApprovedError() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId)
                .withOtherDocumentsApproved(ApprovalType.APPROVED).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, null);

        assertTrue(result.isFailure());

        assertThat(projectInDB.getOtherDocumentsApproved(), Matchers.equalTo(ApprovalType.APPROVED));

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsSuccess() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(grantOfferLetterServiceMock.generateGrantOfferLetterIfReady(1L)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, true);

        assertTrue(result.isSuccess());

        assertEquals(ApprovalType.APPROVED, projectInDB.getOtherDocumentsApproved());
        verify(grantOfferLetterServiceMock).generateGrantOfferLetterIfReady(1L);

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsRejectSuccess() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).withOtherDocumentsSubmittedDate(ZonedDateTime.now()).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(grantOfferLetterServiceMock.generateGrantOfferLetterIfReady(1L)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, false);

        assertTrue(result.isSuccess());

        assertEquals(ApprovalType.REJECTED, projectInDB.getOtherDocumentsApproved());
        assertEquals(null, projectInDB.getDocumentsSubmittedDate());
        verify(grantOfferLetterServiceMock).generateGrantOfferLetterIfReady(1L);

    }

    @Test
    public void testAcceptOrRejectOtherDocumentsFailureGenerateGolFails() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(grantOfferLetterServiceMock.generateGrantOfferLetterIfReady(1L)).thenReturn(serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE));

        ServiceResult<Void> result = service.acceptOrRejectOtherDocuments(projectId, true);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_FAILURE));

        assertEquals(ApprovalType.APPROVED, projectInDB.getOtherDocumentsApproved());
        verify(grantOfferLetterServiceMock).generateGrantOfferLetterIfReady(1L);

    }

    @Test
    public void testUpdateDocumentsResetApproval() {

        Long projectId = 1L;

        Project projectInDB = newProject().withId(projectId).withOtherDocumentsApproved(ApprovalType.REJECTED).build();
        FileEntry entry = newFileEntry().build();
        FileEntryResource entryResource = newFileEntryResource().build();
        Supplier<InputStream> input = () -> null;

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(projectWorkflowHandlerMock.getState(projectInDB)).thenReturn(ProjectState.SETUP);

        ServiceResult<Pair<File, FileEntry>> successfulFileUpdateResult = serviceSuccess(Pair.of(new File("updatedfile"), entry));
        when(fileServiceMock.updateFile(any(), any())).thenReturn(successfulFileUpdateResult);

        ServiceResult<Void> result = service.updateCollaborationAgreementFileEntry(projectId, entryResource, input);

        assertTrue(result.isSuccess());

        assertEquals(ApprovalType.UNSET, projectInDB.getOtherDocumentsApproved());
        verify(fileServiceMock).updateFile(entryResource, input);

    }

    @Test
    public void testCreateCollaborationAgreementFileEntry() {
        assertCreateFile(
                project::getCollaborationAgreement,
                (fileToCreate, inputStreamSupplier) ->
                        service.createCollaborationAgreementFileEntry(123L, fileToCreate, inputStreamSupplier));
    }

    @Test
    public void testUpdateCollaborationAgreementFileEntry() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        assertUpdateFile(
                project::getCollaborationAgreement,
                (fileToUpdate, inputStreamSupplier) ->
                        service.updateCollaborationAgreementFileEntry(123L, fileToUpdate, inputStreamSupplier));
    }

    @Test
    public void testFailureUpdateCollaborationAgreementFileEntryProjectLive() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);

        FileEntryResource fileToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        ServiceResult<Void> result = service.updateCollaborationAgreementFileEntry(123L, fileToUpdate, inputStreamSupplier);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));

    }

    @Test
    public void testGetCollaborationAgreementFileEntryDetails() {
        assertGetFileDetails(
                project::setCollaborationAgreement,
                () -> service.getCollaborationAgreementFileEntryDetails(123L));
    }

    @Test
    public void testGetCollaborationAgreementFileContents() {
        assertGetFileContents(
                project::setCollaborationAgreement,
                () -> service.getCollaborationAgreementFileContents(123L));
    }

    @Test
    public void testDeleteCollaborationAgreementFile() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        assertDeleteFile(
                project::getCollaborationAgreement,
                project::setCollaborationAgreement,
                () -> service.deleteCollaborationAgreementFile(123L));
    }

    private void assertDeleteFile(Supplier<FileEntry> fileGetter, Consumer<FileEntry> fileSetter, Supplier<ServiceResult<Void>> deleteFileFn) {
        FileEntry fileToDelete = newFileEntry().build();

        fileSetter.accept(fileToDelete);
        when(fileServiceMock.deleteFileIgnoreNotFound(fileToDelete.getId())).thenReturn(serviceSuccess(fileToDelete));

        ServiceResult<Void> result = deleteFileFn.get();
        assertTrue(result.isSuccess());
        assertNull(fileGetter.get());

        verify(fileServiceMock).deleteFileIgnoreNotFound(fileToDelete.getId());
    }

    @Test
    public void testFailureDeleteCollaborationAgreementFileEntryProjectLive() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);

        ServiceResult<Void> result = service.deleteCollaborationAgreementFile(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

    @Test
    public void testCreateExploitationPlanFileEntry() {
        assertCreateFile(
                project::getExploitationPlan,
                (fileToCreate, inputStreamSupplier) ->
                        service.createExploitationPlanFileEntry(123L, fileToCreate, inputStreamSupplier));
    }

    @Test
    public void testUpdateExploitationPlanFileEntry() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);

        assertUpdateFile(
                project::getExploitationPlan,
                (fileToUpdate, inputStreamSupplier) ->
                        service.updateExploitationPlanFileEntry(123L, fileToUpdate, inputStreamSupplier));
    }

    @Test
    public void testFailureUpdateExploitationPlanFileProjectLive() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);

        FileEntryResource fileToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        ServiceResult<Void> result = service.updateExploitationPlanFileEntry(project.getId(), fileToUpdate, inputStreamSupplier);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));

    }

    @Test
    public void testGetExploitationPlanFileEntryDetails() {
        assertGetFileDetails(
                project::setExploitationPlan,
                () -> service.getExploitationPlanFileEntryDetails(123L));
    }

    @Test
    public void testGetExploitationPlanFileContents() {
        assertGetFileContents(
                project::setExploitationPlan,
                () -> service.getExploitationPlanFileContents(123L));
    }

    @Test
    public void testDeleteExploitationPlanFile() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);

        assertDeleteFile(
                project::getExploitationPlan,
                project::setExploitationPlan,
                () -> service.deleteExploitationPlanFile(123L));
    }

    @Test
    public void testFailureDeleteExploitationPlanFileProjectLive() {
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);

        ServiceResult<Void> result = service.deleteCollaborationAgreementFile(123L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

    @Test
    public void testFilesCanBeSubmitted() {
        assertFilesCanBeSubmittedByProjectManagerAndFilesExist(
                project::setCollaborationAgreement,
                project::setExploitationPlan,
                () -> service.isOtherDocumentsSubmitAllowed(123L, 1L));

    }

    private void assertFilesCanBeSubmittedByProjectManagerAndFilesExist(Consumer<FileEntry> fileSetter1,
                                                                        Consumer<FileEntry> fileSetter2,
                                                                        Supplier<ServiceResult<Boolean>> getConditionFn) {
        ProjectUser projectUserToSet = newProjectUser()
                .withId(1L)
                .withUser(newUser().withId(1L).build())
                .withRole(PROJECT_MANAGER)
                .build();

        project.addProjectUser(projectUserToSet);

        Supplier<InputStream> inputStreamSupplier1 = () -> null;
        Supplier<InputStream> inputStreamSupplier2 = () -> null;

        getFileEntryResources(fileSetter1, fileSetter2, inputStreamSupplier1, inputStreamSupplier2);
        ServiceResult<Boolean> result = getConditionFn.get();

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject());

    }

    @Test
    public void testFilesCannotBeSubmittedIfUserNotProjectManager() {
        assertFilesCannotBeSubmittedIfNotByProjectManager(
                project::setCollaborationAgreement,
                project::setExploitationPlan,
                () -> service.isOtherDocumentsSubmitAllowed(123L, 1L));

    }

    private void assertFilesCannotBeSubmittedIfNotByProjectManager(Consumer<FileEntry> fileSetter1,
                                                                   Consumer<FileEntry> fileSetter2,
                                                                   Supplier<ServiceResult<Boolean>> getConditionFn) {
        List<ProjectUser> projectUsers = new ArrayList<>();
        Arrays.stream(ProjectParticipantRole.values())
                .filter(roleType -> roleType != PROJECT_MANAGER)
                .forEach(roleType -> {
                    ProjectUser projectUser = newProjectUser()
                            .withId(3L)
                            .withRole(roleType)
                            .build();
                    projectUsers.add(projectUser);

                });

        when(projectUserRepositoryMock.findByProjectId(123L)).thenReturn(projectUsers);

        Supplier<InputStream> inputStreamSupplier1 = () -> null;
        Supplier<InputStream> inputStreamSupplier2 = () -> null;

        getFileEntryResources(fileSetter1, fileSetter2, inputStreamSupplier1, inputStreamSupplier2);
        ServiceResult<Boolean> result = getConditionFn.get();

        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());

    }

    @Test
    public void testSaveDocumentsSubmitDateTimeIsSuccessfulWhenUploadsComplete() {
        ProjectUser projectUserToSet = newProjectUser()
                .withId(1L)
                .withUser(newUser().withId(1L).build())
                .withRole(ProjectParticipantRole.PROJECT_MANAGER)
                .build();
        List<ProjectUser> projectUsers = new ArrayList<>();
        projectUsers.add(projectUserToSet);
        Project project = newProject().build();
        project.setProjectUsers(projectUsers);

        when(projectUserRepositoryMock.findByProjectId(project.getId())).thenReturn(projectUsers);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);

        assertSetDocumentsDateTimeIfProjectManagerAndFilesExist(
                project::setCollaborationAgreement,
                project::setExploitationPlan,
                () -> service.saveDocumentsSubmitDateTime(project.getId(), ZonedDateTime.now()));

        assertNotNull(project.getCollaborationAgreement());
        assertNotNull(project.getExploitationPlan());
        assertTrue(project.getProjectUsers().get(0).getRole().getName()
                .equals(UserRoleType.PROJECT_MANAGER.getName()));
        assertNotNull(project.getDocumentsSubmittedDate());
    }

    private void assertSetDocumentsDateTimeIfProjectManagerAndFilesExist(Consumer<FileEntry> fileSetter1,
                                                                         Consumer<FileEntry> fileSetter2,
                                                                         Supplier<ServiceResult<Void>> getConditionFn) {
        Supplier<InputStream> inputStreamSupplier1 = () -> null;
        Supplier<InputStream> inputStreamSupplier2 = () -> null;

        getFileEntryResources(fileSetter1, fileSetter2, inputStreamSupplier1, inputStreamSupplier2);
        ServiceResult<Void> result = getConditionFn.get();

        assertTrue(result.isSuccess());

    }

    private List<FileEntryResource> getFileEntryResources(Consumer<FileEntry> fileSetter1, Consumer<FileEntry> fileSetter2,
                                                          Supplier<InputStream> inputStreamSupplier1,
                                                          Supplier<InputStream> inputStreamSupplier2) {
        FileEntry fileEntry1ToGet = newFileEntry().build();
        FileEntry fileEntry2ToGet = newFileEntry().build();

        List<FileEntryResource> fileEntryResourcesToGet = newFileEntryResource().withFilesizeBytes(100).build(2);

        fileSetter1.accept(fileEntry1ToGet);
        fileSetter2.accept(fileEntry2ToGet);

        when(fileServiceMock.getFileByFileEntryId(fileEntry1ToGet.getId())).thenReturn(serviceSuccess(inputStreamSupplier1));
        when(fileServiceMock.getFileByFileEntryId(fileEntry2ToGet.getId())).thenReturn(serviceSuccess(inputStreamSupplier2));

        when(fileEntryMapperMock.mapToResource(fileEntry1ToGet)).thenReturn(fileEntryResourcesToGet.get(0));
        when(fileEntryMapperMock.mapToResource(fileEntry2ToGet)).thenReturn(fileEntryResourcesToGet.get(1));
        return fileEntryResourcesToGet;
    }

    @Test
    public void testSaveDocumentsSubmitDateTimeFailsWhenUploadsIncomplete() {
        ProjectUser projectUserToSet = newProjectUser()
                .withId(1L)
                .withUser(newUser().withId(1L).build())
                .withRole(ProjectParticipantRole.PROJECT_MANAGER)
                .build();
        List<ProjectUser> projectUsers = new ArrayList<>();
        projectUsers.add(projectUserToSet);
        Project project = newProject().build();
        project.setProjectUsers(projectUsers);

        when(projectUserRepositoryMock.findByProjectId(project.getId())).thenReturn(projectUsers);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);

        ServiceResult<Void> result = service.saveDocumentsSubmitDateTime(project.getId(), ZonedDateTime.now());

        assertTrue(result.isFailure());
        assertNull(project.getCollaborationAgreement());
        assertNull(project.getExploitationPlan());
        assertTrue(project.getProjectUsers().get(0).getRole().getName()
                .equals(UserRoleType.PROJECT_MANAGER.getName()));
        assertNull(project.getDocumentsSubmittedDate());
    }

    @Override
    protected OtherDocumentsService supplyServiceUnderTest() {

        OtherDocumentsService otherDocumentsService =  new OtherDocumentsServiceImpl();
        ReflectionTestUtils.setField(otherDocumentsService, "webBaseUrl", webBaseUrl);
        return otherDocumentsService;
    }
}
