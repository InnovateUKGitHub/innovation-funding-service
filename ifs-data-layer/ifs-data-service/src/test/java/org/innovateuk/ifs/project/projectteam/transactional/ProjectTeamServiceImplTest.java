package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
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
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.project.builder.ProjectStatusResourceBuilder.newProjectStatusResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjectTeamServiceImplTest extends BaseServiceUnitTest<ProjectTeamServiceImpl> {

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
    private StatusService statusServiceMock;

    @Mock
    private ProjectUserInviteRepository projectUserInviteRepositoryMock;

    @Mock
    private ProjectUserInviteMapper projectInviteMapperMock;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private UserRepository userRepository;


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
        when(statusServiceMock.getProjectStatusByProject(any(Project.class))).thenReturn(serviceSuccess(newProjectStatusResource().withSpendProfileStatus(ProjectActivityStates.COMPLETE).build()));

        ServiceResult<Void> result = service.inviteTeamMember(projectId, inviteResource);

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

        Notification notification = new Notification(systemNotificationSource, to, ProjectTeamServiceImpl.Notifications.INVITE_PROJECT_MEMBER, globalArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(
                serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)));

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withEmail("a@b.com")
                .withName("A B")
                .build();

        when(projectInviteMapperMock.mapToDomain(inviteResource)).thenReturn(projectInvite);

        when(statusServiceMock.getProjectStatusByProject(any(Project.class))).thenReturn(serviceSuccess(newProjectStatusResource()
                .withSpendProfileStatus(ProjectActivityStates.PENDING)
                .build()));

        ServiceResult<Void> result = service.inviteTeamMember(projectInDB.getId(), inviteResource);

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

        when(statusServiceMock.getProjectStatusByProject(any(Project.class))).thenReturn(serviceSuccess(newProjectStatusResource().withSpendProfileStatus(ProjectActivityStates.PENDING).build()));

        NotificationTarget to = new UserNotificationTarget("A B", "a@b.com");
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", projectInDB.getName());
        globalArguments.put("competitionName", "Competition 1");
        globalArguments.put("leadOrganisation", organisation.getName());
        globalArguments.put("applicationId", application.getId());
        globalArguments.put("inviteOrganisationName", "Invite Organisation 1");
        globalArguments.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());

        Notification notification = new Notification(systemNotificationSource, to, ProjectTeamServiceImpl.Notifications.INVITE_PROJECT_MEMBER, globalArguments);
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        ProjectUserInvite projectInvite = newProjectUserInvite().
                withEmail("a@b.com").
                withName("A B").
                build();

        when(projectInviteMapperMock.mapToDomain(inviteResource)).thenReturn(projectInvite);

        ServiceResult<Void> result = service.inviteTeamMember(projectInDB.getId(), inviteResource);

        assertTrue(result.isSuccess());

        verify(notificationService).sendNotificationWithFlush(notification, EMAIL);

        verify(projectUserInviteRepositoryMock).save(projectInvite);
    }

    @Test
    public void removeUser() {

        User loggedInUser = newUser().build();
        setLoggedInUser(newUserResource()
                                .withId(loggedInUser.getId())
                                .withRolesGlobal(Collections.singletonList(Role.PARTNER))
                                .build());

        User userToRemove = newUser().build();
        ProjectUser projectUserToRemove = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(userToRemove)
                .build();

        Project project = newProject().withProjectUsers(singletonList(projectUserToRemove)).build();

        when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(loggedInUser));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectUserRepository.findByProjectIdAndUserId(project.getId(), userToRemove.getId())).thenReturn(singletonList(projectUserToRemove));

        service.removeUser(new ProjectUserCompositeId(project.getId(), userToRemove.getId()));

        verify(projectRepository).findById(project.getId());
        verify(projectUserRepository).findByProjectIdAndUserId(project.getId(), userToRemove.getId());

        assertTrue(project.getProjectUsers().isEmpty());
    }

    @Test
    public void removeUserFailsFinanceContact() {

        User loggedInUser = newUser().build();
        setLoggedInUser(newUserResource()
                                .withId(loggedInUser.getId())
                                .withRolesGlobal(Collections.singletonList(Role.PARTNER))
                                .build());

        User userToRemove = newUser().build();
        ProjectUser projectUserToRemove = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(userToRemove)
                .build();

        Project project = newProject().withProjectUsers(singletonList(projectUserToRemove)).build();

        when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(loggedInUser));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectUserRepository.findByProjectIdAndUserId(project.getId(), userToRemove.getId())).thenReturn(
                singletonList(projectUserToRemove));

        service.removeUser(new ProjectUserCompositeId(project.getId(), userToRemove.getId()));

        verify(projectRepository).findById(project.getId());
        verifyZeroInteractions(projectUserRepository);

        assertTrue(project.getProjectUsers().contains(projectUserToRemove));
    }

    @Test
    public void removeUserFailsProjectManager() {

        User loggedInUser = newUser().build();
        setLoggedInUser(newUserResource()
                                .withId(loggedInUser.getId())
                                .withRolesGlobal(Collections.singletonList(Role.PARTNER))
                                .build());

        User userToRemove = newUser().build();
        ProjectUser projectUserToRemove = newProjectUser()
                .withRole(PROJECT_MANAGER)
                .withUser(userToRemove)
                .build();

        Project project = newProject().withProjectUsers(singletonList(projectUserToRemove)).build();

        when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(loggedInUser));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectUserRepository.findByProjectIdAndUserId(project.getId(), userToRemove.getId())).thenReturn(
                singletonList(projectUserToRemove));

        service.removeUser(new ProjectUserCompositeId(project.getId(), userToRemove.getId()));

        verify(projectRepository).findById(project.getId());
        verifyZeroInteractions(projectUserRepository);

        assertTrue(project.getProjectUsers().contains(projectUserToRemove));
    }

    @Override
    protected ProjectTeamServiceImpl supplyServiceUnderTest() {
        ProjectTeamServiceImpl projectTeamService =  new ProjectTeamServiceImpl();
        ReflectionTestUtils.setField(projectTeamService, "webBaseUrl", webBaseUrl);
        return projectTeamService;
    }
}
