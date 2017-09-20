package org.innovateuk.ifs.project.projectdetails.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.project.builder.ProjectBuilder;
import org.innovateuk.ifs.project.builder.SpendProfileBuilder;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.transactional.EmailService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeBuilder.newAddressType;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.OPERATING;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.PROJECT;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_ALREADY_COMPLETE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_START_DATE_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectInviteBuilder.newProjectInvite;
import static org.innovateuk.ifs.invite.builder.ProjectInviteResourceBuilder.newInviteProjectResource;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectDetailsServiceImplTest extends BaseServiceUnitTest<ProjectDetailsService> {

    @Mock
    private EmailService projectEmailService;

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;
    private Long otherUserId = 8L;

    private Application application;
    private Organisation organisation;
    private Role leadApplicantRole;
    private Role projectManagerRole;
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

        leadApplicantRole = newRole(LEADAPPLICANT).build();
        projectManagerRole = newRole(UserRoleType.PROJECT_MANAGER).build();

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

        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        o = organisation;
        o.setOrganisationType(businessOrganisationType);

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());
    }

    @Test
    public void testInvalidProjectManagerProvided() {

        ServiceResult<Void> result = service.setProjectManager(projectId, otherUserId);
        assertFalse(result.isSuccess());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER));
    }

    @Test
    public void testSetProjectManagerWhenProjectDetailsAlreadySubmitted() {

        Project existingProject = newProject().build();

        assertTrue(existingProject.getProjectUsers().isEmpty());

        when(projectRepositoryMock.findOne(projectId)).thenReturn(existingProject);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(existingProject)).thenReturn(true);

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED));

        assertTrue(existingProject.getProjectUsers().isEmpty());
    }

    @Test
    public void testValidProjectManagerProvided() {

        when(roleRepositoryMock.findOneByName(PROJECT_MANAGER.getName())).thenReturn(projectManagerRole);

        when(projectDetailsWorkflowHandlerMock.projectManagerAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

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
    public void testValidProjectManagerProvidedWithExistingProjectManager() {

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

        when(roleRepositoryMock.findOneByName(PROJECT_MANAGER.getName())).thenReturn(projectManagerRole);

        when(projectDetailsWorkflowHandlerMock.projectManagerAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(leadPartnerProjectUser.getId()).build());

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

        verify(projectDetailsWorkflowHandlerMock).projectManagerAdded(project, leadPartnerProjectUser);
    }

    @Test
    public void testUpdateProjectStartDateButStartDateDoesntBeginOnFirstDayOfMonth() {

        LocalDate now = LocalDate.now();
        LocalDate dateNotOnFirstDayOfMonth = LocalDate.of(now.getYear(), now.getMonthValue(), 2).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, dateNotOnFirstDayOfMonth);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));

        verify(projectRepositoryMock, never()).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateButStartDateNotInFuture() {

        LocalDate now = LocalDate.now();
        LocalDate pastDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).minusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, pastDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));

        verify(projectRepositoryMock, never()).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateWhenSpendProfileHasAlreadyBeenGenerated() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        List<SpendProfile> spendProfiles = SpendProfileBuilder.newSpendProfile().build(2);

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);
        when(spendProfileRepositoryMock.findByProjectId(123L)).thenReturn(spendProfiles);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_START_DATE_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED));

        verify(projectRepositoryMock, never()).findOne(123L);
        verify(spendProfileRepositoryMock).findByProjectId(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateButProjectDoesntExist() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        when(projectRepositoryMock.findOne(123L)).thenReturn(null);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(notFoundError(Project.class, 123L)));
    }

    @Test
    public void testUpdateProjectStartDateSuccess() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isSuccess());

        verify(projectRepositoryMock).findOne(123L);
        assertEquals(validDate, existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateFinanceContact() {

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withId(7L).build();

        newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(PROJECT_PARTNER).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(organisationRepositoryMock.findOne(5L)).thenReturn(organisation);

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
    public void testUpdateFinanceContactButUserIsNotExistingPartner() {

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withId(7L).build();
        newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(PROJECT_MANAGER).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(organisationRepositoryMock.findOne(5L)).thenReturn(organisation);

        ServiceResult<Void> updateResult = service.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 5L), 7L);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION));

        verify(processRoleRepositoryMock, never()).save(isA(ProcessRole.class));
    }

    @Test
    public void testUpdateFinanceContactWhenNotPresentOnTheProject() {

        long userIdForUserNotOnProject = 6L;

        Project existingProject = newProject().withId(123L).build();
        Project anotherProject = newProject().withId(9999L).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);
        when(projectWorkflowHandlerMock.getState(existingProject)).thenReturn(ProjectState.SETUP);

        Organisation organisation = newOrganisation().withId(5L).build();
        when(organisationRepositoryMock.findOne(5L)).thenReturn(organisation);

        User user = newUser().withId(7L).build();
        newProjectUser().withOrganisation(organisation).withUser(user).withProject(anotherProject).withRole(PROJECT_PARTNER).build();

        ServiceResult<Void> updateResult = service.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 5L), userIdForUserNotOnProject);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION));
    }

    @Test
    public void testUpdateFinanceContactAllowedWhenFinanceContactAlreadySet() {

        User anotherUser = newUser().build();
        Project existingProject = newProject().build();
        when(projectRepositoryMock.findOne(existingProject.getId())).thenReturn(existingProject);
        when(projectWorkflowHandlerMock.getState(existingProject)).thenReturn(ProjectState.SETUP);

        Organisation organisation = newOrganisation().build();
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);

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
    public void testUpdateFinanceContactNotAllowedWhenProjectLive() {

        User anotherUser = newUser().build();
        Project existingProject = newProject().build();
        when(projectRepositoryMock.findOne(existingProject.getId())).thenReturn(existingProject);
        when(projectWorkflowHandlerMock.getState(existingProject)).thenReturn(ProjectState.LIVE);

        Organisation organisation = newOrganisation().build();
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);

        newProjectUser().
                withOrganisation(organisation).
                withUser(user, anotherUser).
                withProject(existingProject).
                withRole(PROJECT_FINANCE_CONTACT, PROJECT_PARTNER).build(2);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> updateResult = service.updateFinanceContact(new ProjectOrganisationCompositeId(existingProject.getId(), organisation.getId()), anotherUser.getId());

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

    @Test
    public void testInviteProjectManagerWhenProjectNotInDB() {

        Long projectId = 1L;

        InviteProjectResource inviteResource = newInviteProjectResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withInviteOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();


        when(inviteProjectMapperMock.mapToDomain(inviteResource)).thenReturn(newProjectInvite().withEmail("a@b.com").withName("A B").build());

        when(projectRepositoryMock.findOne(projectId)).thenThrow(new IllegalArgumentException());

        ServiceResult<Void> result = null;

        try {
            result = service.inviteProjectManager(projectId, inviteResource);
        } catch (Exception e) {

            // We expect an exception to be thrown
            assertTrue(e instanceof IllegalArgumentException);

            assertNull(result);
            verify(projectEmailService, never()).sendEmail(any(), any(), any());

            // This exception flow is the only expected flow, so return from here and assertFalse if no exception
            return;
        }

        // Should not reach here - we must get an exception
        assertFalse(true);
    }

    @Test
    public void testInviteProjectManagerWhenProjectDetailsAlreadySubmitted() {

        Long projectId = 1L;

        InviteProjectResource inviteResource = newInviteProjectResource()
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withId(projectId)
                .build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(projectInDB)).thenReturn(true);

        ServiceResult<Void> result = service.inviteProjectManager(projectId, inviteResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED));
    }

    @Test
    public void testInviteProjectManagerWhenUnableToSendNotification() {

        Long projectId = 1L;

        InviteProjectResource inviteResource = newInviteProjectResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withInviteOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");
        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("projectName", "Project 1");
        globalArgs.put("leadOrganisation", organisation.getName());
        globalArgs.put("inviteOrganisationName", "Invite Organisation 1");
        globalArgs.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());
        when(projectEmailService.sendEmail(singletonList(to), globalArgs, ProjectDetailsServiceImpl.Notifications.INVITE_PROJECT_MANAGER)).
                thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)));

        when(inviteProjectMapperMock.mapToDomain(inviteResource)).thenReturn(newProjectInvite().withEmail("a@b.com").withName("A B").build());

        ServiceResult<Void> result = service.inviteProjectManager(projectId, inviteResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE));
    }

    @Test
    public void testInviteProjectManagerSuccess() {

        Long projectId = 1L;

        InviteProjectResource inviteResource = newInviteProjectResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withInviteOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");
        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("projectName", "Project 1");
        globalArgs.put("leadOrganisation", organisation.getName());
        globalArgs.put("inviteOrganisationName", "Invite Organisation 1");
        globalArgs.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());
        when(projectEmailService.sendEmail(singletonList(to), globalArgs, ProjectDetailsServiceImpl.Notifications.INVITE_PROJECT_MANAGER)).thenReturn(serviceSuccess());

        when(inviteProjectMapperMock.mapToDomain(inviteResource)).thenReturn(newProjectInvite().withEmail("a@b.com").withName("A B").build());

        ServiceResult<Void> result = service.inviteProjectManager(projectId, inviteResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testInviteFinanceContactSuccess() {

        Long projectId = 1L;

        InviteProjectResource inviteResource = newInviteProjectResource()
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(17L)
                .withInviteOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .build();

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);

        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("projectName", "Project 1");
        globalArgs.put("leadOrganisation", organisation.getName());
        globalArgs.put("inviteOrganisationName", "Invite Organisation 1");
        globalArgs.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());
        when(projectEmailService.sendEmail(singletonList(to), globalArgs, ProjectDetailsServiceImpl.Notifications.INVITE_FINANCE_CONTACT)).thenReturn(serviceSuccess());

        when(inviteProjectMapperMock.mapToDomain(inviteResource)).thenReturn(newProjectInvite().withName("A B").withEmail("a@b.com").build());

        ServiceResult<Void> result = service.inviteFinanceContact(projectId, inviteResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateProjectAddressToBeRegisteredAddress() {

        Organisation leadOrganisation = newOrganisation().withId(1L).build();
        AddressResource existingRegisteredAddressResource = newAddressResource().build();
        Address registeredAddress = newAddress().build();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(addressRepositoryMock.exists(existingRegisteredAddressResource.getId())).thenReturn(true);
        when(addressRepositoryMock.findOne(existingRegisteredAddressResource.getId())).thenReturn(registeredAddress);

        when(projectDetailsWorkflowHandlerMock.projectAddressAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), REGISTERED, existingRegisteredAddressResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateProjectAddressToBeOperatingAddress() {

        Organisation leadOrganisation = newOrganisation().withId(1L).build();
        AddressResource existingOperatingAddressResource = newAddressResource().build();
        Address operatingAddress = newAddress().build();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(addressRepositoryMock.exists(existingOperatingAddressResource.getId())).thenReturn(true);
        when(addressRepositoryMock.findOne(existingOperatingAddressResource.getId())).thenReturn(operatingAddress);

        when(projectDetailsWorkflowHandlerMock.projectAddressAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), OPERATING, existingOperatingAddressResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateProjectAddressToNewProjectAddress() {

        Organisation leadOrganisation = newOrganisation().withId(organisation.getId()).build();
        AddressResource newAddressResource = newAddressResource().build();
        Address newAddress = newAddress().build();
        AddressType projectAddressType = newAddressType().withId((long) PROJECT.getOrdinal()).withName(PROJECT.name()).build();
        OrganisationAddress organisationAddress = newOrganisationAddress().withOrganisation(leadOrganisation).withAddress(newAddress).withAddressType(projectAddressType).build();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(addressRepositoryMock.exists(newAddressResource.getId())).thenReturn(false);
        when(addressMapperMock.mapToDomain(newAddressResource)).thenReturn(newAddress);
        when(addressTypeRepositoryMock.findOne(PROJECT.getOrdinal())).thenReturn(projectAddressType);
        when(organisationAddressRepositoryMock.findByOrganisationIdAndAddressType(leadOrganisation.getId(), projectAddressType)).thenReturn(emptyList());
        when(organisationAddressRepositoryMock.save(organisationAddress)).thenReturn(organisationAddress);

        when(projectDetailsWorkflowHandlerMock.projectAddressAdded(project, leadPartnerProjectUser)).thenReturn(true);

        setLoggedInUser(newUserResource().withId(user.getId()).build());

        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), PROJECT, newAddressResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testInviteProjectFinanceUser(){
        InviteProjectResource invite = newInviteProjectResource().withInviteOrganisationName("Invite Organisation 1").build();
        ProcessRole[] roles = newProcessRole()
                .withOrganisationId(o.getId())
                .withRole(LEADAPPLICANT)
                .build(1)
                .toArray(new ProcessRole[0]);
        Application a = newApplication().withProcessRoles(roles).build();

        Project project = newProject().withId(projectId).withName("Project 1").withApplication(a).build();

        NotificationTarget to = new ExternalUserNotificationTarget("A B", "a@b.com");

        when(organisationRepositoryMock.findOne(o.getId())).thenReturn(o);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("projectName", "Project 1");
        globalArgs.put("leadOrganisation", organisation.getName());
        globalArgs.put("inviteOrganisationName", "Invite Organisation 1");
        globalArgs.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + invite.getHash());
        when(projectEmailService.sendEmail(singletonList(to), globalArgs, ProjectDetailsServiceImpl.Notifications.INVITE_FINANCE_CONTACT)).thenReturn(serviceSuccess());
        when(inviteProjectMapperMock.mapToDomain(invite)).thenReturn(newProjectInvite().withEmail("a@b.com").withName("A B").build());

        ServiceResult<Void> success = service.inviteFinanceContact(project.getId(), invite);

        assertTrue(success.isSuccess());
    }

    @Override
    protected ProjectDetailsService supplyServiceUnderTest() {
        ProjectDetailsServiceImpl projectDetailsService =  new ProjectDetailsServiceImpl();
        ReflectionTestUtils.setField(projectDetailsService, "webBaseUrl", webBaseUrl);
        return projectDetailsService;
    }
}
