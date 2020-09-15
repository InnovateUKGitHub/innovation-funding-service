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
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.invite.transactional.ProjectInviteValidator;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
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
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjectTeamServiceImplTest extends BaseServiceUnitTest<ProjectTeamServiceImpl> {

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    private ProjectUserInviteRepository projectUserInviteRepositoryMock;

    @Mock
    private ProjectUserInviteMapper projectInviteMapperMock;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Mock
    private ProjectUserRepository projectUserRepositoryMock;

    @Mock
    private ProjectMapper projectMapperMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private ProjectInviteValidator projectInviteValidator;

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;

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
                withId(234L).
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        user = newUser().
                withId(userId).
                withEmailAddress("email@example.com").
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

        ServiceResult<Void> result = service.inviteTeamMember(projectId, inviteResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED));
    }

    @Test
    public void inviteProjectManagerWhenUnableToSendNotification() {
        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        Organisation leadOrganisation = newOrganisation()
                .withId(89L)
                .withName("Lead Organisation")
                .build();

        ProjectUserInviteResource inviteResource = newProjectUserInviteResource()
                .withCompetitionName("Competition 1")
                .withApplicationId(application.getId())
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(leadOrganisation.getId())
                .withLeadOrganisation(leadOrganisation.getName())
                .withOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .withProject(projectInDB.getId())
                .withOrganisation(organisation.getId())
                .build();

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withId(333L)
                .withEmail("Abc.xyz@gmail.com")
                .withName("Abc Xyz")
                .withOrganisation(organisation)
                .withProject(projectInDB)
                .build();

        ProjectResource projectResource = new ProjectResource();
        projectResource.setName("Project 1");
        projectResource.setApplication(application.getId());

        when(organisationRepositoryMock.findDistinctByProcessRolesUser(any(User.class))).thenReturn(singletonList(organisation));
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(projectUserInviteRepositoryMock.findByProjectId(projectInDB.getId())).thenReturn(singletonList(projectInvite));
        when(projectMapperMock.mapToResource(projectInDB)).thenReturn(projectResource);
        when(organisationRepositoryMock.findById(inviteResource.getLeadOrganisationId())).thenReturn(Optional.of(leadOrganisation));

        when(projectServiceMock.getProjectById(projectInDB.getId())).thenReturn(serviceSuccess(projectResource));
        when(projectRepositoryMock.findById(projectInDB.getId())).thenReturn(Optional.of(projectInDB));
        when(projectInviteMapperMock.mapToResource(projectInvite)).thenReturn(inviteResource);
        when(projectInviteMapperMock.mapToDomain(inviteResource)).thenReturn(projectInvite);
        when(projectInviteValidator.validate(any())).thenReturn(serviceSuccess());

        NotificationTarget to = new UserNotificationTarget("Abc Xyz", "Abc.xyz@gmail.com");

        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", projectInDB.getName());
        globalArguments.put("applicationId", application.getId());
        globalArguments.put("leadOrganisation", organisation.getName());
        globalArguments.put("inviteOrganisationName", "Invite Organisation 1");
        globalArguments.put("competitionName", "Competition 1");
        globalArguments.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());

        Notification notification = new Notification(systemNotificationSourceMock, to, ProjectTeamServiceImpl.Notifications.INVITE_PROJECT_MEMBER, globalArguments);

        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL))
                .thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)));

        ServiceResult<Void> result = service.inviteTeamMember(projectInDB.getId(), inviteResource);

        verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL);
        assertTrue(result.isFailure());
        assertEquals(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(projectUserInviteRepositoryMock, times(1)).save(projectInvite);
    }

    @Test
    public void inviteProjectManagerSuccess() {

        Project projectInDB = ProjectBuilder.newProject()
                .withName("Project 1")
                .withApplication(application)
                .build();

        Organisation leadOrganisation = newOrganisation()
                .withId(89L)
                .withName("Lead Organisation")
                .build();

        ProjectUserInviteResource inviteResource = newProjectUserInviteResource()
                .withCompetitionName("Competition 1")
                .withApplicationId(application.getId())
                .withName("Abc Xyz")
                .withEmail("Abc.xyz@gmail.com")
                .withLeadOrganisation(organisation.getId())
                .withLeadOrganisation(leadOrganisation.getId())
                .withLeadOrganisation(leadOrganisation.getName())
                .withOrganisationName("Invite Organisation 1")
                .withHash("sample/url")
                .withProject(projectInDB.getId())
                .withOrganisation(organisation.getId())
                .build();

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withId(333L)
                .withEmail("Abc.xyz@gmail.com")
                .withName("Abc Xyz")
                .withOrganisation(organisation)
                .withProject(projectInDB)
                .build();

        ProjectResource projectResource = new ProjectResource();
        projectResource.setName("Project 1");
        projectResource.setApplication(application.getId());

        when(projectRepositoryMock.findById(projectInDB.getId())).thenReturn(Optional.of(projectInDB));
        ProjectUserInviteResource projectUserInviteResource = getMapper(ProjectUserInviteMapper.class).mapToResource(projectInvite);

        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(projectUserInviteRepositoryMock.findByProjectId(projectInDB.getId())).thenReturn(singletonList(projectInvite));
        when(projectMapperMock.mapToResource(projectInDB)).thenReturn(projectResource);
        when(organisationRepositoryMock.findById(inviteResource.getLeadOrganisationId())).thenReturn(Optional.of(leadOrganisation));

        when(projectServiceMock.getProjectById(projectInDB.getId())).thenReturn(serviceSuccess(projectResource));
        when(projectInviteMapperMock.mapToResource(projectInvite)).thenReturn(inviteResource);
        when(projectInviteMapperMock.mapToDomain(projectUserInviteResource)).thenReturn(projectInvite);
        when(projectInviteValidator.validate(any())).thenReturn(serviceSuccess());

        NotificationTarget to = new UserNotificationTarget("Abc Xyz", "Abc.xyz@gmail.com");
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", projectInDB.getName());
        globalArguments.put("applicationId", application.getId());
        globalArguments.put("leadOrganisation", organisation.getName());
        globalArguments.put("inviteOrganisationName", "Invite Organisation 1");
        globalArguments.put("competitionName", "Competition 1");
        globalArguments.put("inviteUrl", webBaseUrl + "/project-setup/accept-invite/" + inviteResource.getHash());

        Notification notification = new Notification(systemNotificationSourceMock, to, ProjectTeamServiceImpl.Notifications.INVITE_PROJECT_MEMBER, globalArguments);
        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());
        
        when(projectInviteMapperMock.mapToDomain(inviteResource)).thenReturn(projectInvite);

        ServiceResult<Void> result = service.inviteTeamMember(projectInDB.getId(), inviteResource);

        assertTrue(result.isSuccess());
        verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL);
        verify(projectUserInviteRepositoryMock, times(1)).save(projectInvite);
    }

    @Test
    public void removeUser() {

        User loggedInUser = newUser().build();
        setLoggedInUser(newUserResource()
                                .withId(loggedInUser.getId())
                                .withRolesGlobal(singletonList(Role.PARTNER))
                                .build());

        User userToRemove = newUser().build();
        ProjectUser projectUserToRemove = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(userToRemove)
                .build();

        Project project = newProject()
                .withProjectProcess(newProjectProcess()
                                            .withProjectUser(newProjectUser()
                                                                     .withUser(newUser().build())
                                                                     .build())
                                            .build())
                .withProjectUsers(singletonList(projectUserToRemove)).build();

        when(userRepositoryMock.findById(loggedInUser.getId())).thenReturn(Optional.of(loggedInUser));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));

        service.removeUser(new ProjectUserCompositeId(project.getId(), userToRemove.getId()));

        verify(projectRepositoryMock).findById(project.getId());

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

        when(userRepositoryMock.findById(loggedInUser.getId())).thenReturn(Optional.of(loggedInUser));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectUserRepositoryMock.findByProjectIdAndUserId(project.getId(), userToRemove.getId())).thenReturn(
                singletonList(projectUserToRemove));

        service.removeUser(new ProjectUserCompositeId(project.getId(), userToRemove.getId()));

        verify(projectRepositoryMock).findById(project.getId());
        verifyZeroInteractions(projectUserRepositoryMock);

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

        when(userRepositoryMock.findById(loggedInUser.getId())).thenReturn(Optional.of(loggedInUser));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectUserRepositoryMock.findByProjectIdAndUserId(project.getId(), userToRemove.getId())).thenReturn(
                singletonList(projectUserToRemove));

        service.removeUser(new ProjectUserCompositeId(project.getId(), userToRemove.getId()));

        verify(projectRepositoryMock).findById(project.getId());
        verifyZeroInteractions(projectUserRepositoryMock);

        assertTrue(project.getProjectUsers().contains(projectUserToRemove));
    }

    @Test
    public void removeInviteFailsWrongProject() {

        Project project = newProject().build();
        Project wrongProject = newProject().build();
        ProjectUserInvite invite = newProjectUserInvite()
                .withProject(wrongProject)
                .withStatus(SENT)
                .build();

        when(projectUserInviteRepositoryMock.findById(invite.getId())).thenReturn(Optional.of(invite));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));

        ServiceResult<Void> result = service.removeInvite(invite.getId(), project.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_INVITE_NOT_FOR_CORRECT_PROJECT));

        verify(projectUserInviteRepositoryMock, never()).delete(invite);
    }

    @Test
    public void removeInviteFailsWrongStatus() {

        Project project = newProject().build();
        ProjectUserInvite invite = newProjectUserInvite()
                .withProject(project)
                .withStatus(OPENED)
                .build();

        when(projectUserInviteRepositoryMock.findById(invite.getId())).thenReturn(Optional.of(invite));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));

        ServiceResult<Void> result = service.removeInvite(invite.getId(), project.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_INVITE_ALREADY_OPENED));

        verify(projectUserInviteRepositoryMock, never()).delete(invite);
    }

    @Test
    public void removeInviteSucceeds() {

        Project project = newProject().build();
        ProjectUserInvite invite = newProjectUserInvite()
                .withProject(project)
                .withStatus(SENT)
                .build();

        when(projectUserInviteRepositoryMock.findById(invite.getId())).thenReturn(Optional.of(invite));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));

        ServiceResult<Void> result = service.removeInvite(invite.getId(), project.getId());
        assertTrue(result.isSuccess());

        verify(projectUserInviteRepositoryMock).delete(invite);
    }

    @Override
    protected ProjectTeamServiceImpl supplyServiceUnderTest() {
        ProjectTeamServiceImpl projectTeamService =  new ProjectTeamServiceImpl();
        ReflectionTestUtils.setField(projectTeamService, "webBaseUrl", webBaseUrl);
        return projectTeamService;
    }
}
