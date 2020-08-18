package org.innovateuk.ifs.project.grantofferletter.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.docusign.domain.DocusignDocument;
import org.innovateuk.ifs.docusign.resource.DocusignType;
import org.innovateuk.ifs.docusign.transactional.DocusignService;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.grant.repository.GrantProcessConfigurationRepository;
import org.innovateuk.ifs.grant.service.GrantProcessService;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.transactional.PartnerOrganisationService;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.repository.CostRepository;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.string.resource.StringResource;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_ALREADY_COMPLETE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.project.financecheck.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.SENT;
import static org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterServiceImpl.NotificationsGol.GRANT_OFFER_LETTER_PROJECT_MANAGER;
import static org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterServiceImpl.NotificationsGol.PROJECT_LIVE;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileBuilder.newSpendProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GrantOfferLetterServiceImplTest extends BaseServiceUnitTest<GrantOfferLetterService> {

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;
    private Application application;
    private List<Organisation> organisations;
    private Organisation nonAcademicUnfunded;
    private User user;
    private ProcessRole leadApplicantProcessRole;
    private ProjectUser leadPartnerProjectUser;
    private Project project;
    private List<OrganisationResource> organisationResources;
    private List<PartnerOrganisationResource> partnerOrganisationsResource;

    private Address address;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OrganisationMapper organisationMapper;

    @Mock
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Mock
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PartnerOrganisationService partnerOrganisationService;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private SpendProfileRepository spendProfileRepository;

    @Mock
    private CostRepository costRepository;

    @Mock
    private GrantProcessService grantProcessService;

    @Mock
    private DocusignService docusignService;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private GrantProcessConfigurationRepository grantProcessConfigurationRepository;

    @Before
    public void setUp() {
        organisations = newOrganisation().withOrganisationType(RESEARCH).withName("Org1&", "Org2\"", "Org3<").build(3);
        nonAcademicUnfunded = newOrganisation().withOrganisationType(BUSINESS).withName("Org4").build();
        organisationResources = newOrganisationResource().build(4);

        Competition competition = newCompetition()
                .build();

        address = newAddress().withAddressLine1("test1")
                .withAddressLine2("test2")
                .withPostcode("PST")
                .withTown("town").build();

        user = newUser().
                withId(userId).
                build();

        leadApplicantProcessRole = newProcessRole().
                withOrganisationId(organisations.get(0).getId()).
                withRole(Role.LEADAPPLICANT).
                withUser(user).
                build();

        leadPartnerProjectUser = newProjectUser().
                withOrganisation(organisations.get(0)).
                withRole(PROJECT_PARTNER).
                withUser(user).
                build();

        application = newApplication().
                withId(applicationId).
                withCompetition(competition).
                withProcessRoles(leadApplicantProcessRole).
                withName("My Application").
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                build();

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().withOrganisation(organisations.get(0)).build();
        PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisations.get(1)).build();
        PartnerOrganisation partnerOrganisation3 = newPartnerOrganisation().withOrganisation(organisations.get(2)).build();

        List<PartnerOrganisation> partnerOrganisations = new ArrayList<>();
        partnerOrganisations.add(partnerOrganisation);
        partnerOrganisations.add(partnerOrganisation2);
        partnerOrganisations.add(partnerOrganisation3);

        partnerOrganisationsResource = newPartnerOrganisationResource().build(2);

        project = newProject().
                withId(projectId).
                withPartnerOrganisations(partnerOrganisations).
                withAddress(address).
                withApplication(application).
                withProjectUsers(singletonList(leadPartnerProjectUser)).
                withProjectProcess(newProjectProcess().
                        withActivityState(SETUP).
                        build()).
                build();

        SpendProfile orgSpendProfile = newSpendProfile()
                .withSpendProfileFigures(singletonList(newCost().build()))
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(organisationRepository.findById(organisations.get(0).getId())).thenReturn(Optional.of(organisations.get(0)));
        when(organisationRepository.findById(organisations.get(1).getId())).thenReturn(Optional.of(organisations.get(1)));
        when(organisationRepository.findById(organisations.get(2).getId())).thenReturn(Optional.of(organisations.get(2)));
        when(organisationMapper.mapToResource(organisations.get(0))).thenReturn(organisationResources.get(0));
        when(organisationMapper.mapToResource(organisations.get(1))).thenReturn(organisationResources.get(1));
        when(organisationMapper.mapToResource(organisations.get(2))).thenReturn(organisationResources.get(2));
        when(spendProfileRepository.findOneByProjectIdAndOrganisationId(anyLong(), anyLong())).thenReturn(Optional.of(orgSpendProfile));
        when(costRepository.findByCostGroupId(anyLong())).thenReturn(singletonList(newCost().build()));
        when(partnerOrganisationService.getProjectPartnerOrganisations(anyLong())).thenReturn(serviceSuccess(partnerOrganisationsResource));
    }

    @Test
    public void createSignedGrantOfferLetterFileEntry() {
        assertCreateFile(
                project::getSignedGrantOfferLetter,
                (fileToCreate, inputStreamSupplier) ->
                        service.createSignedGrantOfferLetterFileEntry(123L, fileToCreate, inputStreamSupplier));
    }

    @Test
    public void createGrantOfferLetterFileEntry() {
        assertCreateFile(
                project::getGrantOfferLetter,
                (fileToCreate, inputStreamSupplier) ->
                        service.createGrantOfferLetterFileEntry(123L, fileToCreate, inputStreamSupplier));
    }

    @Test
    public void createAdditionalContractFileEntry() {
        assertCreateFile(
                project::getAdditionalContractFile,
                (fileToCreate, inputStreamSupplier) ->
                        service.createAdditionalContractFileEntry(123L, fileToCreate, inputStreamSupplier));
    }

    @Test
    public void getAdditionalContractFileEntryDetails() {
        assertGetFileDetails(
                project::setAdditionalContractFile,
                () -> service.getAdditionalContractFileEntryDetails(123L));
    }

    @Test
    public void getGrantOfferLetterFileEntryDetails() {
        assertGetFileDetails(
                project::setGrantOfferLetter,
                () -> service.getGrantOfferLetterFileEntryDetails(123L));
    }

    @Test
    public void getSignedGrantOfferLetterFileEntryDetails() {
        assertGetFileDetails(
                project::setSignedGrantOfferLetter,
                () -> service.getSignedGrantOfferLetterFileEntryDetails(123L));
    }

    @Test
    public void getAdditionalContractFileContents() {
        assertGetFileContents(
                project::setAdditionalContractFile,
                () -> service.getAdditionalContractFileAndContents(123L));
    }

    @Test
    public void getGrantOfferLetterFileContents() {
        assertGetFileContents(
                project::setGrantOfferLetter,
                () -> service.getGrantOfferLetterFileAndContents(123L));
    }

    @Test
    public void getSignedGrantOfferLetterFileContents() {
        assertGetFileContents(
                project::setSignedGrantOfferLetter,
                () -> service.getSignedGrantOfferLetterFileAndContents(123L));
    }

    @Test
    public void updateSignedGrantOfferLetterFileEntry() {
        when(golWorkflowHandler.isSent(any())).thenReturn(true);
        when(projectWorkflowHandler.getState(project)).thenReturn(SETUP);
        assertUpdateFile(
                project::getSignedGrantOfferLetter,
                (fileToUpdate, inputStreamSupplier) ->
                        service.updateSignedGrantOfferLetterFile(123L, fileToUpdate, inputStreamSupplier));
    }

    @Test
    public void updateSignedGrantOfferLetterFileEntryProjectLive() {

        FileEntryResource fileToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(projectWorkflowHandler.getState(any())).thenReturn(ProjectState.LIVE);
        when(golWorkflowHandler.isSent(any())).thenReturn(false);

        ServiceResult<Void> result = service.updateSignedGrantOfferLetterFile(123L, fileToUpdate, inputStreamSupplier);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

    @Test
    public void updateSignedGrantOfferLetterFileEntryGolNotSent() {

        FileEntryResource fileToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(projectWorkflowHandler.getState(any())).thenReturn(SETUP);
        when(golWorkflowHandler.isSent(any())).thenReturn(false);

        ServiceResult<Void> result = service.updateSignedGrantOfferLetterFile(123L, fileToUpdate, inputStreamSupplier);
        assertTrue(result.isFailure());
        assertEquals(result.getErrors().get(0).getErrorKey(), CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_SENT_BEFORE_UPLOADING_SIGNED_COPY.toString());
    }

    @Test
    public void submitGrantOfferLetterFailureNoSignedGolFile() {

        ServiceResult<Void> result = service.submitGrantOfferLetter(projectId);

        assertTrue(result.getFailure().is(CommonFailureKeys.SIGNED_GRANT_OFFER_LETTER_MUST_BE_UPLOADED_BEFORE_SUBMIT));
        Assert.assertThat(project.getOfferSubmittedDate(), nullValue());
    }

    @Test
    public void submitGrantOfferLetterFailureCannotReachSignedState() {
        project.setSignedGrantOfferLetter(mock(FileEntry.class));

        when(golWorkflowHandler.sign(any())).thenReturn(false);

        ServiceResult<Void> result = service.submitGrantOfferLetter(projectId);

        assertTrue(result.getFailure().is(CommonFailureKeys.GRANT_OFFER_LETTER_CANNOT_SET_SIGNED_STATE));
        Assert.assertThat(project.getOfferSubmittedDate(), nullValue());
    }

    @Test
    public void submitGrantOfferLetterSuccess() {
        project.setSignedGrantOfferLetter(mock(FileEntry.class));

        when(golWorkflowHandler.sign(any())).thenReturn(true);

        ServiceResult<Void> result = service.submitGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
        Assert.assertThat(project.getOfferSubmittedDate(), notNullValue());
    }

    @Test
    public void removeGrantOfferLetterFileEntry() {

        UserResource internalUserResource = newUserResource().build();
        User internalUser = newUser().withId(internalUserResource.getId()).build();
        setLoggedInUser(internalUserResource);

        FileEntry existingGOLFile = newFileEntry().build();
        project.setGrantOfferLetter(existingGOLFile);

        when(userRepository.findById(internalUserResource.getId())).thenReturn(Optional.of(internalUser));
        when(golWorkflowHandler.removeGrantOfferLetter(project, internalUser)).thenReturn(true);
        when(projectWorkflowHandler.getState(project)).thenReturn(SETUP);
        when(fileServiceMock.deleteFileIgnoreNotFound(existingGOLFile.getId())).thenReturn(serviceSuccess(existingGOLFile));

        ServiceResult<Void> result = service.removeGrantOfferLetterFileEntry(123L);

        assertTrue(result.isSuccess());
        assertNull(project.getGrantOfferLetter());

        verify(golWorkflowHandler).removeGrantOfferLetter(project, internalUser);
        verify(fileServiceMock).deleteFileIgnoreNotFound(existingGOLFile.getId());
    }

    @Test
    public void removeGrantOfferLetterFileEntryProjectLive() {

        UserResource internalUserResource = newUserResource().build();
        User internalUser = newUser().withId(internalUserResource.getId()).build();
        setLoggedInUser(internalUserResource);

        FileEntry existingGOLFile = newFileEntry().build();
        project.setGrantOfferLetter(existingGOLFile);

        when(userRepository.findById(internalUserResource.getId())).thenReturn(Optional.of(internalUser));
        when(golWorkflowHandler.removeGrantOfferLetter(project, internalUser)).thenReturn(true);
        when(projectWorkflowHandler.getState(project)).thenReturn(ProjectState.LIVE);
        when(fileServiceMock.deleteFileIgnoreNotFound(existingGOLFile.getId())).thenReturn(serviceSuccess(existingGOLFile));

        ServiceResult<Void> result = service.removeGrantOfferLetterFileEntry(123L);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

    @Test
    public void removeGrantOfferLetterFileEntryButWorkflowRejected() {

        UserResource internalUserResource = newUserResource().build();
        User internalUser = newUser().withId(internalUserResource.getId()).build();
        setLoggedInUser(internalUserResource);

        FileEntry existingGOLFile = newFileEntry().build();
        project.setGrantOfferLetter(existingGOLFile);

        when(userRepository.findById(internalUserResource.getId())).thenReturn(Optional.of(internalUser));
        when(projectWorkflowHandler.getState(project)).thenReturn(SETUP);
        when(golWorkflowHandler.removeGrantOfferLetter(project, internalUser)).thenReturn(false);

        ServiceResult<Void> result = service.removeGrantOfferLetterFileEntry(123L);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GRANT_OFFER_LETTER_CANNOT_BE_REMOVED));
        assertEquals(existingGOLFile, project.getGrantOfferLetter());

        verify(golWorkflowHandler).removeGrantOfferLetter(project, internalUser);
        verify(fileServiceMock, never()).deleteFile(existingGOLFile.getId());
    }

    @Test
    public void removeSignedGrantOfferLetterFileEntry() {

        UserResource externalUser = newUserResource().build();
        setLoggedInUser(externalUser);

        FileEntry existingSignedGOLFile = newFileEntry().build();
        project.setSignedGrantOfferLetter(existingSignedGOLFile);

        when(userRepository.findById(externalUser.getId())).thenReturn(Optional.of(user));
        when(projectWorkflowHandler.getState(project)).thenReturn(SETUP);
        when(fileServiceMock.deleteFileIgnoreNotFound(existingSignedGOLFile.getId())).thenReturn(serviceSuccess(existingSignedGOLFile));
        when(golWorkflowHandler.removeSignedGrantOfferLetter(project, user)).thenReturn(true);

        ServiceResult<Void> result = service.removeSignedGrantOfferLetterFileEntry(123L);
        assertTrue(result.isSuccess());
        assertNull(project.getSignedGrantOfferLetter());

        verify(golWorkflowHandler).removeSignedGrantOfferLetter(project, user);
        verify(fileServiceMock).deleteFileIgnoreNotFound(existingSignedGOLFile.getId());
    }

    @Test
    public void removeSignedGrantOfferLetterFileEntryProjectLive() {

        UserResource internalUserResource = newUserResource().build();
        User internalUser = newUser().withId(internalUserResource.getId()).build();
        setLoggedInUser(internalUserResource);

        FileEntry existingSignedGOLFile = newFileEntry().build();
        project.setSignedGrantOfferLetter(existingSignedGOLFile);

        when(userRepository.findById(internalUserResource.getId())).thenReturn(Optional.of(internalUser));
        when(projectWorkflowHandler.getState(project)).thenReturn(ProjectState.LIVE);
        when(fileServiceMock.deleteFileIgnoreNotFound(existingSignedGOLFile.getId())).thenReturn(serviceSuccess(existingSignedGOLFile));

        ServiceResult<Void> result = service.removeSignedGrantOfferLetterFileEntry(123L);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

//    @Test
//    public void resetGrantOfferLetter() {
//        Long userId = 1234L;
//        UserResource loggedInUser = newUserResource().withId(userId).build();
//        User user = newUser().withId(loggedInUser.getId()).build();
//        setLoggedInUser(loggedInUser);
//        Application application = newApplication().build();
//        ProcessRole processRole = newProcessRole().withUser(user).withRole(Role.PROJECT_FINANCE).withOrganisationId(null).withApplication(application).build();
//        FileEntry golFile = newFileEntry().build();
//        Long projectId = 4234L;
//        Project project = newProject()
//                .withId(projectId)
//                .withApplication(application)
//                .withGrantOfferLetter(golFile)
//                .withAdditionalContractFile(null)
//                .withSpendProfileSubmittedDate(ZonedDateTime.now())
//                .build();
//
//        GOLProcess currentGOLProcess = new GOLProcess((ProjectUser) null, project, SENT);
//
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(golWorkflowHandler.grantOfferLetterReset(project, user)).thenReturn(true);
//        when(projectWorkflowHandler.getState(project)).thenReturn(SETUP);
//        when(fileServiceMock.deleteFileIgnoreNotFound(golFile.getId())).thenReturn(serviceSuccess(golFile));
//
//        ServiceResult<Void> result = service.resetGrantOfferLetter(project.getId());
//
//        assertTrue(result.isSuccess());
//    }

    @Test
    public void sendGrantOfferLetterNoGol() {

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(user).withOrganisation(nonAcademicUnfunded).withInvite(newProjectUserInvite().build()).build(1);
        Project p = newProject()
                .withProjectUsers(pu)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(nonAcademicUnfunded)
                        .build(1))
                .withGrantOfferLetter(null)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(p));
        when(notificationService.sendNotification(any(), eq(EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isFailure());
    }

    @Test
    public void sendGrantOfferLetterSendFails() {

        List<ProjectUser> pu = newProjectUser()
                .withRole(PROJECT_MANAGER)
                .withUser(user)
                .withOrganisation(nonAcademicUnfunded)
                .withInvite(newProjectUserInvite()
                        .build())
                .build(1);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        FileEntry golFile = newFileEntry()
                .withMediaType("application/pdf")
                .withFilesizeBytes(10)
                .build();

        Project project = newProject()
                .withProjectUsers(pu)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(nonAcademicUnfunded)
                        .build(1)).withGrantOfferLetter(golFile)
                .withApplication(application)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        User projectManagerUser = pu.get(0).getUser();

        NotificationTarget to = new UserNotificationTarget(projectManagerUser.getName(), projectManagerUser.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard",
                "applicationId", application.getId(),
                "competitionName", "Competition 1"
        );

        Notification notification = new Notification(systemNotificationSource, to, GRANT_OFFER_LETTER_PROJECT_MANAGER, expectedNotificationArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceFailure(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE));

        User user = newUser().build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(golWorkflowHandler.grantOfferLetterSent(project, user)).thenReturn(true);

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE));
    }

    @Test
    public void sendGrantOfferLetterNoProject() {

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isFailure());
    }

    @Test
    public void sendGrantOfferLetterSuccess() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(user).withOrganisation(nonAcademicUnfunded).withInvite(newProjectUserInvite().build()).build(1);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project p = newProject()
                .withProjectUsers(pu)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(nonAcademicUnfunded)
                        .build(1))
                .withGrantOfferLetter(golFile)
                .withApplication(application)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(p));

        User projectManagerUser = pu.get(0).getUser();

        NotificationTarget to = new UserNotificationTarget(projectManagerUser.getName(), projectManagerUser.getEmail());

        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard",
                "applicationId", application.getId(),
                "competitionName", "Competition 1"
        );

        Notification notification = new Notification(systemNotificationSource, to, GRANT_OFFER_LETTER_PROJECT_MANAGER, expectedNotificationArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        User user = UserBuilder.newUser().build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(golWorkflowHandler.grantOfferLetterSent(p, user)).thenReturn(true);

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());

        verify(golWorkflowHandler).grantOfferLetterSent(p, user);
        verify(notificationService).sendNotificationWithFlush(notification, EMAIL);
    }


    @Test
    public void sendGrantOfferLetterSuccessDocusign() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(user).withOrganisation(nonAcademicUnfunded).withInvite(newProjectUserInvite().build()).build(1);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project p = newProject()
                .withProjectUsers(pu)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(nonAcademicUnfunded)
                        .build(1))
                .withGrantOfferLetter(golFile)
                .withApplication(application)
                .withUseDocusignForGrantOfferLetter(true)
                .build();

        when(projectRepository.findById(p.getId())).thenReturn(Optional.of(p));

        User projectManagerUser = pu.get(0).getUser();
        DocusignDocument document = new DocusignDocument(projectManagerUser.getId(), DocusignType.SIGNED_GRANT_OFFER_LETTER);

        when(golWorkflowHandler.grantOfferLetterSent(p, user)).thenReturn(true);
        when(docusignService.send(any())).thenReturn(serviceSuccess(document));

        User user = UserBuilder.newUser().build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(golWorkflowHandler.grantOfferLetterSent(p, user)).thenReturn(true);

        final Supplier<InputStream> contentSupplier = () -> null;
        FileEntryResource fileEntryResource = new FileEntryResource();
        when(fileServiceMock.getFileByFileEntryId(golFile.getId())).thenReturn(ServiceResult.serviceSuccess(contentSupplier));
        when(fileEntryMapperMock.mapToResource(golFile)).thenReturn(fileEntryResource);

        ServiceResult<Void> result = service.sendGrantOfferLetter(p.getId());

        assertTrue(result.isSuccess());

        verify(golWorkflowHandler).grantOfferLetterSent(p, user);
        verify(docusignService).send(any());
    }

    @Test
    public void sendGrantOfferLetterFailure() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(user).withOrganisation(nonAcademicUnfunded).withInvite(newProjectUserInvite().build()).build(1);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project project = newProject()
                .withProjectUsers(pu)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(nonAcademicUnfunded)
                        .build(1)).withGrantOfferLetter(golFile)
                .withApplication(application)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        User user = UserBuilder.newUser().build();
        setLoggedInUser(newUserResource().withId(user.getId()).build());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(golWorkflowHandler.grantOfferLetterSent(project, user)).thenReturn(false);

        ServiceResult<Void> result = service.sendGrantOfferLetter(projectId);

        assertTrue(result.isFailure());
        verify(golWorkflowHandler).grantOfferLetterSent(project, user);
        verify(notificationService, never()).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterRejectionSuccess() {
        User u = newUser().withFirstName("A").withLastName("B").withEmailAddress("a@b.com").build();
        setLoggedInUser(newUserResource().withId(u.getId()).build());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(golWorkflowHandler.isReadyToApprove(project)).thenReturn(true);
        when(userRepository.findById(u.getId())).thenReturn(Optional.of(u));
        when(golWorkflowHandler.grantOfferLetterRejected(project, u)).thenReturn(true);

        String rejectionReason = "No signature";
        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.REJECTED, rejectionReason);
        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        verify(projectRepository).findById(projectId);
        verify(golWorkflowHandler).isReadyToApprove(project);
        verify(golWorkflowHandler).grantOfferLetterRejected(project, u);
        verify(golWorkflowHandler, never()).grantOfferLetterApproved(project, u);
        verify(projectWorkflowHandler, never()).grantOfferLetterApproved(any(), any());
        verify(notificationService, never()).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));
        assertNull(project.getOfferSubmittedDate());
        assertEquals(project.getGrantOfferLetterRejectionReason(), rejectionReason);

        assertTrue(result.isSuccess());
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterWhenGOLRejectionFailure() {
        User u = newUser().withFirstName("A").withLastName("B").withEmailAddress("a@b.com").build();
        setLoggedInUser(newUserResource().withId(u.getId()).build());

        NotificationTarget to = new UserNotificationTarget("A B", "a@b.com");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(golWorkflowHandler.isReadyToApprove(project)).thenReturn(true);
        when(userRepository.findById(u.getId())).thenReturn(Optional.of(u));
        when(golWorkflowHandler.grantOfferLetterRejected(project, u)).thenReturn(false);

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.REJECTED, "No signature");
        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        verify(projectRepository).findById(projectId);
        verify(golWorkflowHandler).isReadyToApprove(project);
        verify(golWorkflowHandler).grantOfferLetterRejected(project, u);
        verify(golWorkflowHandler, never()).grantOfferLetterApproved(project, u);
        verify(projectWorkflowHandler, never()).grantOfferLetterApproved(any(), any());
        verify(notificationService, never()).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterApprovalSuccess() {
        User user = newUser()
                .withFirstName("A")
                .withLastName("B")
                .withEmailAddress("a@b.com")
                .build();

        setLoggedInUser(newUserResource()
                .withId(user.getId())
                .build());

        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();

        ProjectUser projectManager = newProjectUser()
                .withRole(PROJECT_MANAGER)
                .withUser(user)
                .withOrganisation(organisation1)
                .build();

        ProjectUser financeContactOrg1 = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(newUser().build())
                .withOrganisation(organisation1)
                .build();

        ProjectUser financeContactOrg2 = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(newUser().build())
                .withOrganisation(organisation2)
                .build();

        List<ProjectUser> projectUsers =
                asList(projectManager, financeContactOrg1, financeContactOrg2);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project project = newProject()
                .withId(projectId)
                .withProjectUsers(projectUsers)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(organisation1, organisation2)
                        .build(2)
                )
                .withApplication(application)
                .withProjectProcess(newProjectProcess()
                        .withActivityState(SETUP)
                        .build())
                .withTargetStartDate(LocalDate.now())
                .build();

        List<NotificationTarget> to = asList(
                new UserNotificationTarget(projectManager.getUser().getName(), projectManager.getUser().getEmail()),
                new UserNotificationTarget(financeContactOrg1.getUser().getName(), financeContactOrg1.getUser().getEmail()),
                new UserNotificationTarget(financeContactOrg2.getUser().getName(), financeContactOrg2.getUser().getEmail())
        );

        Map<String, Object> expectedNotificationArguments = asMap(
                "applicationId", project.getApplication().getId(),
                "projectName", project.getName(),
                "projectStartDate", LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                "projectSetupUrl", webBaseUrl + "/project-setup/project/" + project.getId()
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(golWorkflowHandler.isReadyToApprove(project)).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(golWorkflowHandler.grantOfferLetterApproved(project, user)).thenReturn(true);
        when(projectWorkflowHandler.grantOfferLetterApproved(project, project.getProjectUsersWithRole(PROJECT_MANAGER).get(0))).thenReturn(true);

        Notification notification = new Notification(systemNotificationSource, to, PROJECT_LIVE, expectedNotificationArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);

        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);
        assertTrue(result.isSuccess());

        verify(projectRepository, atLeast(2)).findById(projectId);
        verify(golWorkflowHandler).isReadyToApprove(project);
        verify(golWorkflowHandler).grantOfferLetterApproved(project, user);
        verify(projectWorkflowHandler).grantOfferLetterApproved(project, project.getProjectUsersWithRole(PROJECT_MANAGER).get(0));
        verify(notificationService).sendNotificationWithFlush(notification, EMAIL);
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterEnsureDuplicateEmailsAreNotSent() {

        User user = newUser()
                .withFirstName("A")
                .withLastName("B")
                .withEmailAddress("a@b.com")
                .build();

        setLoggedInUser(newUserResource()
                .withId(user.getId())
                .build());

        List<ProjectUser> projectUsers = newProjectUser()
                .withRole(PROJECT_MANAGER, PROJECT_FINANCE_CONTACT)
                .withUser(user).withOrganisation(nonAcademicUnfunded)
                .withInvite(newProjectUserInvite()
                        .build())
                .build(2);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project project = newProject()
                .withId(projectId)
                .withProjectUsers(projectUsers)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(nonAcademicUnfunded)
                        .build(1))
                .withApplication(application)
                .withProjectProcess(newProjectProcess()
                        .withActivityState(SETUP)
                        .build())
                .withTargetStartDate(LocalDate.now())
                .build();

        NotificationTarget to = new UserNotificationTarget("A B", "a@b.com");

        Map<String, Object> expectedNotificationArguments = asMap(
                "applicationId", project.getApplication().getId(),
                "projectName", project.getName(),
                "projectStartDate", LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                "projectSetupUrl", webBaseUrl + "/project-setup/project/" + project.getId()
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(golWorkflowHandler.isReadyToApprove(project)).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(golWorkflowHandler.grantOfferLetterApproved(project, user)).thenReturn(true);

        when(projectWorkflowHandler.grantOfferLetterApproved(project, project.getProjectUsersWithRole(PROJECT_MANAGER).get(0))).
                thenReturn(true);

        Notification notification = new Notification(systemNotificationSource, to, PROJECT_LIVE, expectedNotificationArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);

        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);
        assertTrue(result.isSuccess());

        verify(projectRepository, atLeast(2)).findById(projectId);
        verify(golWorkflowHandler).isReadyToApprove(project);
        verify(golWorkflowHandler).grantOfferLetterApproved(project, user);
        verify(projectWorkflowHandler).grantOfferLetterApproved(project, project.getProjectUsersWithRole(PROJECT_MANAGER).get(0));
        verify(notificationService).sendNotificationWithFlush(notification, EMAIL);

    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterWhenProjectGOLApprovalFailure() {
        User u = newUser().withFirstName("A").withLastName("B").withEmailAddress("a@b.com").build();
        setLoggedInUser(newUserResource().withId(u.getId()).build());
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(u).withOrganisation(nonAcademicUnfunded).withInvite(newProjectUserInvite().build()).build(1);
        Project project = newProject()
                .withId(projectId)
                .withProjectUsers(pu)
                .withPartnerOrganisations(newPartnerOrganisation().withOrganisation(nonAcademicUnfunded).build(1))
                .withProjectProcess(newProjectProcess()
                        .withActivityState(SETUP)
                        .build())
                .build();

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        project.setGrantOfferLetter(golFile);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(golWorkflowHandler.isReadyToApprove(project)).thenReturn(true);
        when(userRepository.findById(u.getId())).thenReturn(Optional.of(u));
        when(golWorkflowHandler.grantOfferLetterApproved(project, u)).thenReturn(true);
        when(projectWorkflowHandler.grantOfferLetterApproved(project, project.getProjectUsersWithRole(PROJECT_MANAGER).get(0))).thenReturn(false);

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);
        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        verify(projectRepository).findById(projectId);
        verify(golWorkflowHandler).isReadyToApprove(project);
        verify(golWorkflowHandler).grantOfferLetterApproved(project, u);
        verify(projectWorkflowHandler).grantOfferLetterApproved(project, project.getProjectUsersWithRole(PROJECT_MANAGER).get(0));
        verify(notificationService, never()).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterWhenGOLApprovalFailure() {
        User u = newUser().withFirstName("A").withLastName("B").withEmailAddress("a@b.com").build();
        setLoggedInUser(newUserResource().withId(u.getId()).build());

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        project.setGrantOfferLetter(golFile);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(golWorkflowHandler.isReadyToApprove(project)).thenReturn(true);
        when(userRepository.findById(u.getId())).thenReturn(Optional.of(u));
        when(golWorkflowHandler.grantOfferLetterApproved(project, u)).thenReturn(false);

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);
        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        verify(projectRepository).findById(projectId);
        verify(golWorkflowHandler).isReadyToApprove(project);
        verify(golWorkflowHandler).grantOfferLetterApproved(project, u);
        verify(projectWorkflowHandler, never()).grantOfferLetterApproved(any(), any());
        verify(notificationService, never()).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterWhenGOLNotReadyToApprove() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        project.setGrantOfferLetter(golFile);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(golWorkflowHandler.isReadyToApprove(project)).thenReturn(false);

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);
        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        verify(projectRepository).findById(projectId);
        verify(golWorkflowHandler).isReadyToApprove(project);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GRANT_OFFER_LETTER_NOT_READY_TO_APPROVE));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterWhenGOLRejectedButRejectionReasonIsAllWhitespaces() {

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.REJECTED, "          ");
        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GENERAL_INVALID_ARGUMENT));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterWhenGOLRejectedButRejectionReasonIsEmpty() {

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.REJECTED, "");
        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GENERAL_INVALID_ARGUMENT));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetterWhenGOLRejectedButNoRejectionReason() {

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.REJECTED, null);
        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GENERAL_INVALID_ARGUMENT));
    }

    @Test
    public void aproveOrRejectSignedGrantOfferLetterWhenGOLNeitherApprovedNorRejected() {

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(null, null);
        ServiceResult<Void> result = service.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GENERAL_INVALID_ARGUMENT));
    }

    @Test
    public void getGrantOfferLetterStateWhenProjectDoesNotExist() {

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ServiceResult<GrantOfferLetterStateResource> result = service.getGrantOfferLetterState(projectId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Project.class, projectId)));
    }

    @Test
    public void getGrantOfferLetterState() {

        GrantOfferLetterStateResource state = GrantOfferLetterStateResource.stateInformationForPartnersView(SENT, GrantOfferLetterEvent.GOL_SENT);

        when(golWorkflowHandler.getExtendedState(project)).thenReturn(serviceSuccess(state));
        ServiceResult<GrantOfferLetterStateResource> retrievedState = service.getGrantOfferLetterState(project.getId());
        assertSame(state, retrievedState.getSuccess());
    }

    @Test
    public void getDocusignUrl() {
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(user).withOrganisation(nonAcademicUnfunded).withInvite(newProjectUserInvite().build()).build(1);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project p = newProject()
                .withProjectUsers(pu)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(nonAcademicUnfunded)
                        .build(1))
                .withApplication(application)
                .withUseDocusignForGrantOfferLetter(true)
                .build();

        when(projectRepository.findById(p.getId())).thenReturn(Optional.of(p));

        DocusignDocument docusignDocument = new DocusignDocument(user.getId(), DocusignType.SIGNED_GRANT_OFFER_LETTER);
        docusignDocument.setEnvelopeId("Envelope");
        p.setSignedGolDocusignDocument(docusignDocument);

        when(docusignService.getDocusignUrl("Envelope", user.getId(), user.getName(), user.getEmail(), String.format("/project-setup/project/%d/offer", p.getId())))
                .thenReturn("redirectToDocusign");

        ServiceResult<StringResource> result = service.getDocusignUrl(p.getId());

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().getContent(), "redirectToDocusign");

    }
    @Test
    public void importGrantOfferLetter() {
        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_MANAGER).withUser(user).withOrganisation(nonAcademicUnfunded).withInvite(newProjectUserInvite().build()).build(1);

        Competition competition = newCompetition()
                .withName("Competition 1")
                .build();

        Application application = newApplication()
                .withName("Application 1")
                .withCompetition(competition)
                .build();

        Project p = newProject()
                .withProjectUsers(pu)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(nonAcademicUnfunded)
                        .build(1))
                .withApplication(application)
                .withUseDocusignForGrantOfferLetter(true)
                .build();

        when(projectRepository.findById(p.getId())).thenReturn(Optional.of(p));

        DocusignDocument docusignDocument = new DocusignDocument(user.getId(), DocusignType.SIGNED_GRANT_OFFER_LETTER);
        docusignDocument.setEnvelopeId("Envelope");
        p.setSignedGolDocusignDocument(docusignDocument);
        when(docusignService.importDocument("Envelope")).thenReturn(serviceSuccess());

        service.importGrantOfferLetter(p.getId());

        verify(docusignService).importDocument("Envelope");
    }
    private static final String webBaseUrl = "https://ifs-local-dev/dashboard";

    @Override
    protected GrantOfferLetterService supplyServiceUnderTest() {

        GrantOfferLetterServiceImpl projectGrantOfferService = new GrantOfferLetterServiceImpl();
        ReflectionTestUtils.setField(projectGrantOfferService, "webBaseUrl", webBaseUrl);
        return projectGrantOfferService;
    }
}
