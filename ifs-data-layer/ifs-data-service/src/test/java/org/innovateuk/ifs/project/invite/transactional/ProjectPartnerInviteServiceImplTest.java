package org.innovateuk.ifs.project.invite.transactional;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;


import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.PendingPartnerProgressRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.ProjectPartnerChangeService;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.invite.domain.ProjectPartnerInvite;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProjectPartnerInviteServiceImplTest {

    @InjectMocks
    private ProjectPartnerInviteServiceImpl service;

    @Mock
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private ProjectInviteValidator projectInviteValidator;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplier;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Mock
    private PendingPartnerProgressRepository pendingPartnerProgressRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Mock
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Mock
    private ProjectPartnerChangeService projectPartnerChangeService;

    @Mock
    private ActivityLogService activityLogService;

    @Test
    public void invite() {
        setField(service, "webBaseUrl", "webBaseUrl");
        long projectId = 1L;
        String organisationName = "Org";
        String userName = "Someone";
        String email = "someone@gmail.com";
        Application application = newApplication().build();
        Organisation leadOrg = newOrganisation()
                .withName("Lead org")
                .build();
        SendProjectPartnerInviteResource invite = new SendProjectPartnerInviteResource(organisationName, userName, email);
        Project project = newProject()
                .withName("Project")
                .withApplication(application)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(leadOrg)
                        .withLeadOrganisation(true)
                        .build(1))
                .build();

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationId", application.getId());
        notificationArguments.put("projectName", "Project");
        notificationArguments.put("leadOrganisationName", "Lead org");

        when(projectRepository.findById(projectId)).thenReturn(of(project));
        when(projectInviteValidator.validate(projectId, invite)).thenReturn(serviceSuccess());
        when(inviteOrganisationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(projectPartnerInviteRepository.save(any())).thenAnswer(invocation -> {
            ProjectPartnerInvite projectPartnerInvite = invocation.getArgument(0);
            notificationArguments.put("inviteUrl", String.format("webBaseUrl/project-setup/project/%d/partner-invite/%s/accept", project.getId(), projectPartnerInvite.getHash()));
            return projectPartnerInvite;
        });

        NotificationTarget to = new UserNotificationTarget(userName, email);

        Notification notification = new Notification(systemNotificationSource, singletonList(to), ProjectPartnerInviteServiceImpl.Notifications.INVITE_PROJECT_PARTNER_ORGANISATION, notificationArguments);
        when(notificationService.sendNotificationWithFlush(notification, NotificationMedium.EMAIL)).thenReturn(serviceSuccess());

        User loggedInUser = newUser().build();
        when(loggedInUserSupplier.get()).thenReturn(loggedInUser);

        ServiceResult<Void> result = service.invitePartnerOrganisation(projectId, invite);

        assertTrue(result.isSuccess());
        verify(notificationService).sendNotificationWithFlush(notification, NotificationMedium.EMAIL);
    }

    @Test
    public void getPartnerInvites() {
        long projectId = 1L;
        long inviteId = 2L;
        String email = "Partner@gmail.com";
        String userName = "Partner";
        String organisationName = "Partners Ltd.";
        ZonedDateTime sentOn = now();
        ProjectPartnerInvite invite = new ProjectPartnerInvite();
        InviteOrganisation inviteOrganisation = new InviteOrganisation();
        invite.setEmail(email);
        invite.setId(inviteId);
        invite.setName(userName);
        invite.setProject(newProject().withName("project").build());
        setField(invite, "status", InviteStatus.SENT);
        setField(invite, "sentOn", sentOn);
        invite.setInviteOrganisation(inviteOrganisation);
        inviteOrganisation.setOrganisationName(organisationName);

        when(projectPartnerInviteRepository.findByProjectId(projectId)).thenReturn(singletonList(invite));

        ServiceResult<List<SentProjectPartnerInviteResource>> result = service.getPartnerInvites(projectId);

        SentProjectPartnerInviteResource resource = result.getSuccess().get(0);
        assertEquals(inviteId, resource.getId());
        assertEquals(email, resource.getEmail());
        assertEquals(userName, resource.getUserName());
        assertEquals(organisationName, resource.getOrganisationName());
        assertEquals(sentOn, resource.getSentOn());
        assertEquals("project", resource.getProjectName());
    }

    @Test
    public void resendInvite() {
        setField(service, "webBaseUrl", "webBaseUrl");
        long inviteId = 2L;
        String userName = "Someone";
        String email = "someone@gmail.com";
        Application application = newApplication().build();
        Organisation leadOrg = newOrganisation()
                .withName("Lead org")
                .build();
        Project project = newProject()
                .withName("Project")
                .withApplication(application)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(leadOrg)
                        .withLeadOrganisation(true)
                        .build(1))
                .build();
        ProjectPartnerInvite invite = spy(new ProjectPartnerInvite());
        invite.setEmail(email);
        invite.setName(userName);
        invite.setTarget(project);
        invite.setHash("hash");

        when(projectPartnerInviteRepository.findById(inviteId)).thenReturn(of(invite));
        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("inviteUrl", String.format("webBaseUrl/project-setup/project/%d/partner-invite/%s/accept", project.getId(), invite.getHash()));
        notificationArguments.put("applicationId", application.getId());
        notificationArguments.put("projectName", "Project");
        notificationArguments.put("leadOrganisationName", "Lead org");

        NotificationTarget to = new UserNotificationTarget(userName, email);

        Notification notification = new Notification(systemNotificationSource, singletonList(to), ProjectPartnerInviteServiceImpl.Notifications.INVITE_PROJECT_PARTNER_ORGANISATION, notificationArguments);
        when(notificationService.sendNotificationWithFlush(notification, NotificationMedium.EMAIL)).thenReturn(serviceSuccess());
        User loggedInUser = newUser().build();
        when(loggedInUserSupplier.get()).thenReturn(loggedInUser);

        service.resendInvite(inviteId);

        verify(invite).resend(eq(loggedInUser), any());
        verify(notificationService).sendNotificationWithFlush(notification, NotificationMedium.EMAIL);
    }

    @Test
    public void deleteInvite() {
        long inviteId = 2L;
        ProjectPartnerInvite invite = new ProjectPartnerInvite();
        when(projectPartnerInviteRepository.findById(inviteId)).thenReturn(of(invite));

        service.deleteInvite(inviteId);

        verify(projectPartnerInviteRepository).delete(invite);
    }

    @Test
    public void getInviteByHash() {
        String hash = "hash";
        String email = "Partner@gmail.com";
        String userName = "Partner";
        String organisationName = "Partners Ltd.";
        ZonedDateTime sentOn = now();
        ProjectPartnerInvite invite = new ProjectPartnerInvite();
        InviteOrganisation inviteOrganisation = new InviteOrganisation();
        invite.setEmail(email);
        invite.setId(1L);
        invite.setName(userName);
        invite.setProject(newProject().withName("project").build());
        setField(invite, "status", InviteStatus.SENT);
        setField(invite, "sentOn", sentOn);
        invite.setInviteOrganisation(inviteOrganisation);
        inviteOrganisation.setOrganisationName(organisationName);
        when(projectPartnerInviteRepository.getByHash("hash")).thenReturn(invite);

        ServiceResult<SentProjectPartnerInviteResource> result = service.getInviteByHash(hash);

        SentProjectPartnerInviteResource resource = result.getSuccess();
        assertEquals(1L, resource.getId());
        assertEquals(email, resource.getEmail());
        assertEquals(userName, resource.getUserName());
        assertEquals(organisationName, resource.getOrganisationName());
        assertEquals(sentOn, resource.getSentOn());
        assertEquals("project", resource.getProjectName());
    }

    @Test
    public void acceptInvite() {
        long inviteId = 1L;
        long organisationId = 2L;

        Competition competition = newCompetition().withIncludeJesForm(true).build();
        Application application = newApplication().withCompetition(competition).build();
        Project project = newProject().withApplication(application).build();
        ProjectPartnerInvite invite = new ProjectPartnerInvite();
        InviteOrganisation inviteOrganisation = newInviteOrganisation().build();
        invite.setInviteOrganisation(inviteOrganisation);
        invite.setProject(project);
        invite.send(newUser().withId(1l).build(), ZonedDateTime.now());
        Organisation organisation = newOrganisation().withId(organisationId).withOrganisationType(OrganisationTypeEnum.RESEARCH) .build();

        when(organisationRepository.findById(organisationId)).thenReturn(of(organisation));
        when(projectPartnerInviteRepository.findById(inviteId)).thenReturn(of(invite));
        when(partnerOrganisationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(projectUserRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResult<Void> result = service.acceptInvite(inviteId, organisationId);

        assertTrue(result.isSuccess());
        assertEquals(inviteOrganisation.getOrganisation(), organisation);
        verify(projectPartnerChangeService, times(1)).updateProjectWhenPartnersChange(project.getId());
        verify(projectFinanceService, times(1)).createProjectFinance(project.getId(), organisationId);
        verify(viabilityWorkflowHandler, times(1)).projectCreated(any(), any());
        verify(eligibilityWorkflowHandler, times(1)).projectCreated(any(), any());
        verify(viabilityWorkflowHandler, times(1)).viabilityNotApplicable(any(), any());
        verify(pendingPartnerProgressRepository, times(1)).save(any());
        verifyNoMoreInteractions(projectPartnerChangeService, projectFinanceService, viabilityWorkflowHandler, eligibilityWorkflowHandler, pendingPartnerProgressRepository);
    }
}
