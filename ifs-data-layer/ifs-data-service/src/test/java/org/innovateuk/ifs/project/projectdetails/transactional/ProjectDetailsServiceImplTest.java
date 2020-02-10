package org.innovateuk.ifs.project.projectdetails.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.invite.mapper.ProjectUserInviteMapper;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.project.monitoringofficer.domain.LegacyMonitoringOfficer;
import org.innovateuk.ifs.project.monitoringofficer.repository.LegacyMonitoringOfficerRepository;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.spendprofile.builder.SpendProfileBuilder;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeBuilder.newAddressType;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.PROJECT;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.commons.validation.ValidationConstants.MAX_POSTCODE_LENGTH;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.project.resource.ProjectState.WITHDRAWN;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

public class ProjectDetailsServiceImplTest extends BaseServiceUnitTest<ProjectDetailsService> {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    private ProjectUserMapper projectUserMapperMock;

    @Mock
    private ProjectUserRepository projectUserRepositoryMock;

    @Mock
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandlerMock;

    @Mock
    private SpendProfileRepository spendProfileRepositoryMock;

    @Mock
    private ProjectWorkflowHandler projectWorkflowHandlerMock;

    @Mock
    private ProjectUserInviteRepository projectUserInviteRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private LegacyMonitoringOfficerRepository monitoringOfficerRepositoryMock;

    @Mock
    private PartnerOrganisationRepository partnerOrganisationRepositoryMock;

    @Mock
    private ProjectUserInviteMapper projectInviteMapperMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private AddressRepository addressRepositoryMock;

    @Mock
    private AddressMapper addressMapperMock;

    @Mock
    private OrganisationAddressRepository organisationAddressRepositoryMock;

    @Mock
    private AddressTypeRepository addressTypeRepositoryMock;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;
    private Long otherUserId = 8L;

    private Application application;
    private Organisation organisation;
    private User user;
    private ProcessRole leadApplicantProcessRole;
    private ProjectUser leadPartnerProjectUser;
    private Organisation o;
    private Project project;

    private static final String webBaseUrl = "https://ifs-local-dev/dashboard";

    @Before
    public void setUp() {

        organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        user = newUser().
                withId(userId).
                build();

        leadApplicantProcessRole = newProcessRole().
                withOrganisationId(organisation.getId()).
                withRole(Role.LEADAPPLICANT).
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
                withGrantOfferLetter(null).
                build();

        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        o = organisation;
        o.setOrganisationType(businessOrganisationType);

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());

        setLoggedInUser(newUserResource().withId(user.getId()).build());
    }

    @Test
    public void getProjectManager() {
        final Long projectId = 123L;
        final Project project = newProject().withId(projectId).build();
        final ProjectUser projectManager = newProjectUser().withProject(project).withRole(PROJECT_MANAGER).build();
        final ProjectUserResource projectManagerResource = newProjectUserResource().withProject(projectId).withRoleName(PROJECT_MANAGER.getName()).build();

        when(projectUserMapperMock.mapToResource(projectManager)).thenReturn(projectManagerResource);
        when(projectUserRepositoryMock.findByProjectIdAndRole(projectId, PROJECT_MANAGER)).thenReturn(Optional.of(projectManager));

        ServiceResult<ProjectUserResource> foundProjectManager = service.getProjectManager(projectId);
        assertTrue(foundProjectManager.isSuccess());
        assertTrue(foundProjectManager.getSuccess().getRoleName().equals(PROJECT_MANAGER.getName()));
        assertTrue(foundProjectManager.getSuccess().getProject().equals(projectId));
    }

    @Test
    public void invalidProjectManagerProvided() {
        ServiceResult<Void> result = service.setProjectManager(projectId, otherUserId);
        assertFalse(result.isSuccess());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER));
    }

    @Test
    public void setProjectManagerWhenGOLAlreadyGenerated() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();
        Project existingProject = newProject().withId(projectId).withGrantOfferLetter(golFile).build();

        assertTrue(existingProject.getProjectUsers().isEmpty());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(existingProject));

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED));

        assertTrue(existingProject.getProjectUsers().isEmpty());
    }

    @Test
    public void validProjectManagerProvided() {

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isSuccess());

        ProjectUser expectedProjectManager = newProjectUser().
                withId().
                withProject(project).
                withOrganisation(organisation).
                withRole(PROJECT_MANAGER).
                withUser(user).
                build();

        assertEquals(expectedProjectManager, project.getProjectUsers().get(project.getProjectUsers().size() - 1));
    }

    @Test
    public void validProjectManagerProvidedWithExistingProjectManager() {

        User differentUser = newUser().build();
        Organisation differentOrganisation = newOrganisation().build();

        @SuppressWarnings("unused")
        ProjectUser existingProjectManager = newProjectUser().
                withId(456L).
                withProject(project).
                withRole(PROJECT_MANAGER).
                withOrganisation(differentOrganisation).
                withUser(differentUser).
                build();

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isSuccess());

        ProjectUser expectedProjectManager = newProjectUser().
                withId(456L).
                withProject(project).
                withOrganisation(organisation).
                withRole(PROJECT_MANAGER).
                withUser(user).
                build();

        assertEquals(expectedProjectManager, project.getProjectUsers().get(project.getProjectUsers().size() - 1));
    }

    @Test
    public void updateProjectStartDateButStartDateDoesntBeginOnFirstDayOfMonth() {

        LocalDate now = LocalDate.now();
        LocalDate dateNotOnFirstDayOfMonth = LocalDate.of(now.getYear(), now.getMonthValue(), 2).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findById(123L)).thenReturn(Optional.of(existingProject));

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, dateNotOnFirstDayOfMonth);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));

        verify(projectRepositoryMock, never()).findById(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void updateProjectStartDateButStartDateNotInFuture() {

        LocalDate now = LocalDate.now();
        LocalDate pastDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).minusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findById(123L)).thenReturn(Optional.of(existingProject));

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, pastDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));

        verify(projectRepositoryMock, never()).findById(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void updateProjectStartDateWhenSpendProfileHasAlreadyBeenGenerated() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        List<SpendProfile> spendProfiles = SpendProfileBuilder.newSpendProfile().build(2);

        when(projectRepositoryMock.findById(123L)).thenReturn(Optional.of(existingProject));
        when(spendProfileRepositoryMock.findByProjectId(123L)).thenReturn(spendProfiles);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_START_DATE_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED));

        verify(projectRepositoryMock, never()).findById(123L);
        verify(spendProfileRepositoryMock).findByProjectId(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void updateProjectStartDateButProjectDoesntExist() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        when(projectRepositoryMock.findById(123L)).thenReturn(Optional.empty());

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(notFoundError(Project.class, 123L)));
    }

    @Test
    public void updateProjectStartDateSuccess() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findById(123L)).thenReturn(Optional.of(existingProject));

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isSuccess());

        verify(projectRepositoryMock).findById(123L);
        assertEquals(validDate, existingProject.getTargetStartDate());
    }

    @Test
    public void updateProjectDurationWhenDurationLessThanAMonth() {

        long projectId = 123L;
        ServiceResult<Void> updateResult = service.updateProjectDuration(projectId, 0L);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_PROJECT_DURATION_MUST_BE_MINIMUM_ONE_MONTH));

        ServiceResult<Void> updateResult2 = service.updateProjectDuration(projectId, -3L);
        assertTrue(updateResult2.isFailure());
        assertTrue(updateResult2.getFailure().is(PROJECT_SETUP_PROJECT_DURATION_MUST_BE_MINIMUM_ONE_MONTH));
    }

    @Test
    public void updateProjectDurationWhenProjectDoesNotExist() {

        long projectId = 123L;
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());

        ServiceResult<Void> updateResult = service.updateProjectDuration(projectId, 36L);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(notFoundError(Project.class, 123L)));
    }

    @Test
    public void updateProjectDurationWhenSpendProfileAlreadyGenerated() {

        long projectId = 123L;

        List<SpendProfile> spendProfiles = SpendProfileBuilder.newSpendProfile().build(2);
        when(spendProfileRepositoryMock.findByProjectId(projectId)).thenReturn(spendProfiles);

        ServiceResult<Void> updateResult = service.updateProjectDuration(projectId, 36L);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_PROJECT_DURATION_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED));
    }

    @Test
    public void updateProjectDurationWhenProjectIsAlreadyWithdrawn() {

        long projectId = 123L;
        Project existingProject = newProject().build();
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectWorkflowHandlerMock.getState(existingProject)).thenReturn(WITHDRAWN);

        ServiceResult<Void> updateResult = service.updateProjectDuration(projectId, 36L);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(GENERAL_FORBIDDEN));
    }

    @Test
    public void updateProjectDurationSuccess() {

        long projectId = 123L;
        long durationInMonths = 36L;
        Project existingProject = newProject().build();
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(existingProject));

        ServiceResult<Void> updateResult = service.updateProjectDuration(projectId, durationInMonths);
        assertTrue(updateResult.isSuccess());
        assertEquals(durationInMonths, (long) existingProject.getDurationInMonths());
    }

    @Test
    public void updateFinanceContact() {

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withId(7L).build();

        newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(PROJECT_PARTNER).build();

        when(projectRepositoryMock.findById(123L)).thenReturn(Optional.of(project));
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(organisationRepositoryMock.findById(5L)).thenReturn(Optional.of(organisation));

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> updateResult = service.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 5L), 7L);

        assertTrue(updateResult.isSuccess());

        List<ProjectUser> foundFinanceContacts = simpleFilter(project.getProjectUsers(), projectUser ->
                projectUser.getOrganisation().equals(organisation) &&
                        projectUser.getUser().equals(user) &&
                        projectUser.getProcess().equals(project) &&
                        projectUser.getRole().equals(PROJECT_FINANCE_CONTACT));

        assertEquals(1, foundFinanceContacts.size());
    }

    @Test
    public void updateFinanceContactWhenGOLAlreadyGenerated() {

        FileEntry golFileEntry = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        Project project = newProject()
                .withId(123L)
                .withGrantOfferLetter(golFileEntry)
                .build();

        when(projectRepositoryMock.findById(123L)).thenReturn(Optional.of(project));

        ServiceResult<Void> updateResult = service.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 5L), 7L);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_CANNOT_BE_UPDATED_IF_GOL_GENERATED));

        verify(processRoleRepositoryMock, never()).save(isA(ProcessRole.class));
    }

    @Test
    public void updateFinanceContactButUserIsNotExistingPartner() {

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withId(7L).build();
        newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(PROJECT_MANAGER).build();

        when(projectRepositoryMock.findById(123L)).thenReturn(Optional.of(project));
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(organisationRepositoryMock.findById(5L)).thenReturn(Optional.of(organisation));

        ServiceResult<Void> updateResult = service.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 5L), 7L);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION));

        verify(processRoleRepositoryMock, never()).save(isA(ProcessRole.class));
    }

    @Test
    public void updateFinanceContactWhenNotPresentOnTheProject() {

        long userIdForUserNotOnProject = 6L;

        Project existingProject = newProject().withId(123L).build();
        Project anotherProject = newProject().withId(9999L).build();

        when(projectRepositoryMock.findById(123L)).thenReturn(Optional.of(existingProject));
        when(projectWorkflowHandlerMock.getState(existingProject)).thenReturn(ProjectState.SETUP);

        Organisation organisation = newOrganisation().withId(5L).build();
        when(organisationRepositoryMock.findById(5L)).thenReturn(Optional.of(organisation));

        User user = newUser().withId(7L).build();
        newProjectUser().withOrganisation(organisation).withUser(user).withProject(anotherProject).withRole(PROJECT_PARTNER).build();

        ServiceResult<Void> updateResult = service.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 5L), userIdForUserNotOnProject);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION));
    }

    @Test
    public void updateFinanceContactAllowedWhenFinanceContactAlreadySet() {

        User anotherUser = newUser().build();
        Project existingProject = newProject().build();
        when(projectRepositoryMock.findById(existingProject.getId())).thenReturn(Optional.of(existingProject));
        when(projectWorkflowHandlerMock.getState(existingProject)).thenReturn(ProjectState.SETUP);

        Organisation organisation = newOrganisation().build();
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));

        newProjectUser().
                withOrganisation(organisation).
                withUser(user, anotherUser).
                withProject(existingProject).
                withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).build(2);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> updateResult = service.updateFinanceContact(new ProjectOrganisationCompositeId(existingProject.getId(), organisation.getId()), anotherUser.getId());
        assertTrue(updateResult.isSuccess());

        List<ProjectUser> organisationFinanceContacts = existingProject.getProjectUsers(pu -> pu.getRole().equals(PROJECT_FINANCE_CONTACT) &&
                pu.getOrganisation().equals(organisation));

        assertEquals(1, organisationFinanceContacts.size());
        assertEquals(anotherUser, organisationFinanceContacts.get(0).getUser());
    }

    @Test
    public void updatePartnerProjectLocationWhenPostcodeIsNullOrEmpty() {

        long projectId = 1L;
        long organisationId = 2L;
        String postcode = null;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        ServiceResult<Void> updateResult = service.updatePartnerProjectLocation(projectOrganisationCompositeId, postcode);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(new Error("validation.field.must.not.be.blank", HttpStatus.BAD_REQUEST)));

        postcode = "";
        updateResult = service.updatePartnerProjectLocation(projectOrganisationCompositeId, postcode);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(new Error("validation.field.must.not.be.blank", HttpStatus.BAD_REQUEST)));

        postcode = "    ";
        updateResult = service.updatePartnerProjectLocation(projectOrganisationCompositeId, postcode);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(new Error("validation.field.must.not.be.blank", HttpStatus.BAD_REQUEST)));

    }

    @Test
    public void updatePartnerProjectLocationWhenPostcodeEnteredExceedsMaxLength() {

        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "SOME LONG POSTCODE";

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        ServiceResult<Void> updateResult = service.updatePartnerProjectLocation(projectOrganisationCompositeId, postcode);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(new Error("validation.field.too.many.characters", asList("", MAX_POSTCODE_LENGTH), HttpStatus.BAD_REQUEST)));
    }

    @Test
    public void updatePartnerProjectLocationWhenMonitoringOfficerAssigned() {

        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "TW14 9QG";

        Project existingProject = newProject().withId(projectId).withGrantOfferLetter(newFileEntry().build()).build();
        when(projectRepositoryMock.findById(existingProject.getId())).thenReturn(Optional.of(existingProject));

        when(monitoringOfficerRepositoryMock.findOneByProjectId(projectId)).thenReturn(new LegacyMonitoringOfficer());

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        ServiceResult<Void> updateResult = service.updatePartnerProjectLocation(projectOrganisationCompositeId, postcode);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_LOCATION_CANNOT_BE_UPDATED_IF_GOL_GENERATED));
    }

    @Test
    public void updatePartnerProjectLocationWhenPartnerOrganisationDoesNotExist() {

        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "TW14 9QG";

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        Project existingProject = newProject().withId(projectId).build();
        when(projectRepositoryMock.findById(existingProject.getId())).thenReturn(Optional.of(existingProject));

        ServiceResult<Void> updateResult = service.updatePartnerProjectLocation(projectOrganisationCompositeId, postcode);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(notFoundError(PartnerOrganisation.class, projectId, organisationId)));
    }

    @Test
    public void updatePartnerProjectLocationEnsureLowerCasePostcodeIsSavedAsUpperCase() {

        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "tw14 9qg";

        PartnerOrganisation partnerOrganisationInDb = new PartnerOrganisation();

        Project existingProject = newProject().withId(projectId).build();
        when(projectRepositoryMock.findById(existingProject.getId())).thenReturn(Optional.of(existingProject));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDb);

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> updateResult = service.updatePartnerProjectLocation(projectOrganisationCompositeId, postcode);
        assertTrue(updateResult.isSuccess());

        assertEquals(postcode.toUpperCase(), partnerOrganisationInDb.getPostcode());
    }

    @Test
    public void updatePartnerProjectLocationSuccess() {
        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "UB7 8QF";

        PartnerOrganisation partnerOrganisationInDb = new PartnerOrganisation(project, null, true);

        Project existingProject = newProject().withId(projectId).build();
        when(projectRepositoryMock.findById(existingProject.getId())).thenReturn(Optional.of(existingProject));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisationInDb);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        ServiceResult<Void> updateResult = service.updatePartnerProjectLocation(projectOrganisationCompositeId, postcode);
        assertTrue(updateResult.isSuccess());

        assertEquals(postcode, partnerOrganisationInDb.getPostcode());
        verify(projectDetailsWorkflowHandlerMock).projectLocationAdded(eq(project), eq(leadPartnerProjectUser));
    }

    @Test
    public void inviteProjectManagerWhenProjectNotInDB() {

        Long projectId = 1L;

        ProjectUserInviteResource inviteResource = newProjectUserInviteResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();


        when(projectInviteMapperMock.mapToDomain(inviteResource)).thenReturn(newProjectUserInvite().withEmail("a@b.com").withName("A B").build());

        when(projectRepositoryMock.findById(projectId)).thenThrow(new IllegalArgumentException());

        ServiceResult<Void> result = null;

        try {
            result = service.inviteProjectManager(projectId, inviteResource);
        } catch (Exception e) {

            // We expect an exception to be thrown
            assertTrue(e instanceof IllegalArgumentException);

            assertNull(result);
            verify(notificationService, never()).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));

            // This exception flow is the only expected flow, so return from here and assertFalse if no exception
            return;
        }

        // Should not reach here - we must get an exception
        assertFalse(true);
    }

    @Test
    public void inviteProjectManagerWhenGOLAlreadyGenerated() {

        Long projectId = 1L;

        ProjectUserInviteResource inviteResource = newProjectUserInviteResource()
                .build();

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        Project projectInDB = ProjectBuilder.newProject()
                .withId(projectId)
                .withGrantOfferLetter(golFile)
                .build();

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(projectInDB));

        ServiceResult<Void> result = service.inviteProjectManager(projectId, inviteResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED));
    }

    @Test
    public void inviteProjectManagerWhenUnableToSendNotification() {

        ProjectUserInviteResource inviteResource = newProjectUserInviteResource()
                .withCompetitionName("Competition 1")
                .withApplicationId(application.getId())
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(organisation.getId())
                .withOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        when(projectRepositoryMock.findById(projectInDB.getId())).thenReturn(Optional.of(projectInDB));

        NotificationTarget to = new UserNotificationTarget("A B", "a@b.com");

        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", projectInDB.getName());
        globalArguments.put("competitionName", "Competition 1");
        globalArguments.put("leadOrganisation", organisation.getName());
        globalArguments.put("applicationId", application.getId());
        globalArguments.put("inviteOrganisationName", "Invite Organisation 1");
        globalArguments.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());

        Notification notification = new Notification(systemNotificationSource, to, ProjectDetailsServiceImpl.Notifications.INVITE_PROJECT_MANAGER, globalArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(
                serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)));

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withEmail("a@b.com")
                .withName("A B")
                .build();

        when(projectInviteMapperMock.mapToDomain(inviteResource)).thenReturn(projectInvite);

        ServiceResult<Void> result = service.inviteProjectManager(projectInDB.getId(), inviteResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE));

        verify(projectUserInviteRepositoryMock).save(projectInvite);
    }

    @Test
    public void inviteProjectManagerSuccess() {

        ProjectUserInviteResource inviteResource = newProjectUserInviteResource()
                .withCompetitionName("Competition 1")
                .withApplicationId(application.getId())
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(organisation.getId())
                .withOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        when(projectRepositoryMock.findById(projectInDB.getId())).thenReturn(Optional.of(projectInDB));

        NotificationTarget to = new UserNotificationTarget("A B", "a@b.com");
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", projectInDB.getName());
        globalArguments.put("competitionName", "Competition 1");
        globalArguments.put("leadOrganisation", organisation.getName());
        globalArguments.put("applicationId", application.getId());
        globalArguments.put("inviteOrganisationName", "Invite Organisation 1");
        globalArguments.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());

        Notification notification = new Notification(systemNotificationSource, to, ProjectDetailsServiceImpl.Notifications.INVITE_PROJECT_MANAGER, globalArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        ProjectUserInvite projectInvite = newProjectUserInvite().
                withEmail("a@b.com").
                withName("A B").
                build();

        when(projectInviteMapperMock.mapToDomain(inviteResource)).thenReturn(projectInvite);

        ServiceResult<Void> result = service.inviteProjectManager(projectInDB.getId(), inviteResource);

        assertTrue(result.isSuccess());

        verify(notificationService).sendNotificationWithFlush(notification, EMAIL);

        verify(projectUserInviteRepositoryMock).save(projectInvite);
    }

    @Test
    public void inviteFinanceContactWhenGOLAlreadyGenerated() {

        Long projectId = 1L;

        ProjectUserInviteResource inviteResource = newProjectUserInviteResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        FileEntry golFileEntry = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        Project projectInDB = ProjectBuilder.newProject()
                .withId(projectId)
                .withGrantOfferLetter(golFileEntry)
                .build();

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(projectInDB));

        ServiceResult<Void> result = service.inviteFinanceContact(projectId, inviteResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_CANNOT_BE_UPDATED_IF_GOL_GENERATED));
    }

    @Test
    public void inviteFinanceContactSuccess() {

        ProjectUserInviteResource inviteResource = newProjectUserInviteResource()
                .withCompetitionName("Competition 1")
                .withApplicationId(application.getId())
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(organisation.getId())
                .withOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        NotificationTarget to = new UserNotificationTarget("A B", "a@b.com");

        when(projectRepositoryMock.findById(projectInDB.getId())).thenReturn(Optional.of(projectInDB));

        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", projectInDB.getName());
        globalArguments.put("competitionName", "Competition 1");
        globalArguments.put("leadOrganisation", organisation.getName());
        globalArguments.put("applicationId", application.getId());
        globalArguments.put("inviteOrganisationName", "Invite Organisation 1");
        globalArguments.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());

        Notification notification = new Notification(systemNotificationSource, to, ProjectDetailsServiceImpl.Notifications.INVITE_FINANCE_CONTACT, globalArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withName("A B")
                .withEmail("a@b.com")
                .build();

        when(projectInviteMapperMock.mapToDomain(inviteResource)).thenReturn(projectInvite);

        ServiceResult<Void> result = service.inviteFinanceContact(projectInDB.getId(), inviteResource);

        assertTrue(result.isSuccess());
        verify(notificationService).sendNotificationWithFlush(notification, EMAIL);

        verify(projectUserInviteRepositoryMock).save(projectInvite);
    }

    @Test
    public void updateProjectAddressToBeRegisteredAddress() {
        AddressResource existingRegisteredAddressResource = newAddressResource().build();
        Address registeredAddress = newAddress().build();

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(addressRepositoryMock.existsById(existingRegisteredAddressResource.getId())).thenReturn(true);
        when(addressRepositoryMock.findById(existingRegisteredAddressResource.getId())).thenReturn(Optional.of(registeredAddress));

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        assertNull(project.getAddress());
        ServiceResult<Void> result = service.updateProjectAddress(organisation.getId(), project.getId(), existingRegisteredAddressResource);
        assertTrue(result.isSuccess());
        assertEquals(registeredAddress, project.getAddress());
    }

    @Test
    public void updateProjectAddressToBeOperatingAddress() {
        AddressResource existingOperatingAddressResource = newAddressResource().build();
        Address operatingAddress = newAddress().build();

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(addressRepositoryMock.existsById(existingOperatingAddressResource.getId())).thenReturn(true);
        when(addressRepositoryMock.findById(existingOperatingAddressResource.getId())).thenReturn(Optional.of(operatingAddress));

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        assertNull(project.getAddress());
        ServiceResult<Void> result = service.updateProjectAddress(organisation.getId(), project.getId(), existingOperatingAddressResource);
        assertTrue(result.isSuccess());
        assertEquals(operatingAddress, project.getAddress());
    }

    @Test
    public void updateProjectAddressToNewProjectAddress() {

        Organisation leadOrganisation = newOrganisation()
                .withId(organisation.getId())
                .build();

        AddressResource newAddressResource = newAddressResource().build();
        Address newAddress = newAddress()
                .build();

        AddressType projectAddressType = newAddressType()
                .withId((long) PROJECT.getOrdinal())
                .withName(PROJECT.name())
                .build();

        OrganisationAddress organisationAddress = newOrganisationAddress()
                .withOrganisation(leadOrganisation)
                .withAddress(newAddress).withAddressType(projectAddressType)
                .build();

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(addressRepositoryMock.existsById(newAddressResource.getId())).thenReturn(false);
        when(addressMapperMock.mapToDomain(newAddressResource)).thenReturn(newAddress);
        when(addressTypeRepositoryMock.findById(PROJECT.getOrdinal())).thenReturn(Optional.of(projectAddressType));
        when(organisationAddressRepositoryMock.findByOrganisationIdAndAddressType(leadOrganisation.getId(), projectAddressType)).thenReturn(emptyList());
        when(organisationAddressRepositoryMock.save(organisationAddress)).thenReturn(organisationAddress);
        when(projectDetailsWorkflowHandlerMock.projectAddressAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        assertNull(project.getAddress());
        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), newAddressResource);
        assertTrue(result.isSuccess());
        verify(organisationAddressRepositoryMock, never()).delete(Mockito.any(OrganisationAddress.class));
        assertEquals(newAddress, project.getAddress());
    }

    @Test
    public void updateProjectAddressToNewProjectAddressAndExistingAddressAssociatedWithOrg() {

        Organisation leadOrganisation = newOrganisation().withId(organisation.getId()).build();
        AddressResource newAddressResource = newAddressResource().build();
        Address newAddress = newAddress().build();
        AddressType projectAddressType = newAddressType().withId((long) PROJECT.getOrdinal()).withName(PROJECT.name()).build();
        OrganisationAddress organisationAddress = newOrganisationAddress().withOrganisation(leadOrganisation).withAddress(newAddress).withAddressType(projectAddressType).build();

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(addressRepositoryMock.existsById(newAddressResource.getId())).thenReturn(false);
        when(addressMapperMock.mapToDomain(newAddressResource)).thenReturn(newAddress);
        when(addressTypeRepositoryMock.findById(PROJECT.getOrdinal())).thenReturn(Optional.of(projectAddressType));
        when(organisationAddressRepositoryMock.findByOrganisationIdAndAddressType(leadOrganisation.getId(), projectAddressType)).thenReturn(singletonList(organisationAddress));
        when(organisationAddressRepositoryMock.save(organisationAddress)).thenReturn(organisationAddress);
        when(projectDetailsWorkflowHandlerMock.projectAddressAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        assertNull(project.getAddress());
        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), newAddressResource);
        assertTrue(result.isSuccess());
        assertEquals(newAddress, project.getAddress());
    }

    @Test
    public void inviteProjectFinanceUser(){

        ProjectUserInviteResource inviteResource = newProjectUserInviteResource()
                .withCompetitionName("Competition 1")
                .withApplicationId(application.getId())
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(organisation.getId())
                .withOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        ProcessRole[] roles = newProcessRole()
                .withOrganisationId(organisation.getId())
                .withRole(Role.LEADAPPLICANT)
                .build(1)
                .toArray(new ProcessRole[0]);

        Application a = newApplication()
                .withProcessRoles(roles)
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(a)
                .build();

        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(projectRepositoryMock.findById(projectInDB.getId())).thenReturn(Optional.of(projectInDB));

        NotificationTarget to = new UserNotificationTarget("A B", "a@b.com");

        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", projectInDB.getName());
        globalArguments.put("competitionName", "Competition 1");
        globalArguments.put("leadOrganisation", organisation.getName());
        globalArguments.put("applicationId", application.getId());
        globalArguments.put("inviteOrganisationName", "Invite Organisation 1");
        globalArguments.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());

        Notification notification = new Notification(systemNotificationSource, to, ProjectDetailsServiceImpl.Notifications.INVITE_FINANCE_CONTACT, globalArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withEmail("a@b.com")
                .withName("A B")
                .build();

        when(projectInviteMapperMock.mapToDomain(inviteResource)).thenReturn(projectInvite);

        ServiceResult<Void> success = service.inviteFinanceContact(projectInDB.getId(), inviteResource);

        assertTrue(success.isSuccess());
        verify(notificationService).sendNotificationWithFlush(notification, EMAIL);

        verify(projectUserInviteRepositoryMock).save(projectInvite);
        verify(projectInviteMapperMock).mapToDomain(inviteResource);
    }

    @Override
    protected ProjectDetailsService supplyServiceUnderTest() {
        ProjectDetailsServiceImpl projectDetailsService =  new ProjectDetailsServiceImpl();
        ReflectionTestUtils.setField(projectDetailsService, "webBaseUrl", webBaseUrl);
        return projectDetailsService;
    }
}
