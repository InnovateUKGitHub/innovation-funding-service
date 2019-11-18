package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

import static freemarker.template.utility.Collections12.singletonList;
import static java.lang.String.format;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.builders.NotificationBuilder.newNotification;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.project.core.transactional.RemovePartnerNotificationServiceImpl.Notifications.REMOVE_PROJECT_ORGANISATION;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newMonitoringOfficer;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemovePartnerNotificationServiceImplTest extends BaseServiceUnitTest<RemovePartnerNotificationService> {

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private NotificationService notificationService;

    private Organisation ludlow;
    private Organisation empire;
    private Application application;
    private Project project;
    private User orville;
    private List<Notification> notifications;
    private MonitoringOfficer monitoringOfficer;
    private ProjectUser projectUser;
    private PartnerOrganisation leadPartnerOrganisation;
    private PartnerOrganisation partnerOrganisation;
    private Map<String, Object> notificationArguments = new HashMap<>();

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Before
    public void setup() {
        ludlow = newOrganisation()
                .withId(43L)
                .withOrganisationType(BUSINESS)
                .withName("Ludlow")
                .build();
        empire = newOrganisation()
                .withId(12L)
                .withOrganisationType(BUSINESS)
                .withName("empire")
                .build();
        application = newApplication().withId(77L).build();
        project = newProject()
                .withId(99L)
                .withName("Smart ideas for plastic recycling")
                .withApplication(application)
                .build();
        leadPartnerOrganisation = newPartnerOrganisation()
                .withLeadOrganisation(true)
                .withOrganisation(empire)
                .withProject(project)
                .build();
        partnerOrganisation = newPartnerOrganisation()
                .withLeadOrganisation(false)
                .withOrganisation(ludlow)
                .withProject(project)
                .build();

        orville = newUser().withId(3L).withFirstName("Orville").withLastName("Gibbs").build();
        monitoringOfficer = newMonitoringOfficer()
                .withId(81L)
                .withUser(orville)
                .withProject(project)
                .build();

        notificationArguments.put("applicationId", application.getId());
        notificationArguments.put("projectName", project.getName());
        notificationArguments.put("organisationName", ludlow.getName());
        notificationArguments.put("projectTeamLink", getProjectTeamLink(project.getId()));
    }

    @Test
    public void sendNotificationsWhenProjectManagerAndMonitoringOfficerArePresents() {
        User user = newUser().withId(23L).withFirstName("Rick").withLastName("McDonald").build();
        projectUser = newProjectUser()
                .withId(88L)
                .withProject(project)
                .withRole(PROJECT_MANAGER)
                .withOrganisation(empire)
                .withUser(user)
                .build();

        project.addProjectUser(projectUser);
        project.setProjectMonitoringOfficer(monitoringOfficer);

        NotificationSource from = systemNotificationSource;
        NotificationTarget recipientPM = createProjectNotificationTarget(user);
        NotificationTarget recipientMO = createProjectNotificationTarget(orville);

        notifications = newNotification()
                .withMessageKey(REMOVE_PROJECT_ORGANISATION)
                .withSource(from)
                .withTargets(singletonList(recipientPM), singletonList(recipientMO))
                .withGlobalArguments(notificationArguments)
                .build(2);

        when(projectUserRepository.findByProjectIdAndRole(project.getId(), PROJECT_MANAGER)).thenReturn(Optional.of(projectUser));
        when(notificationService.sendNotificationWithFlush(notifications.get(0), EMAIL)).thenReturn(serviceSuccess());
        when(notificationService.sendNotificationWithFlush(notifications.get(1), EMAIL)).thenReturn(serviceSuccess());

        service.sendNotifications(project, ludlow);

        verify(notificationService, times(1)).sendNotificationWithFlush(notifications.get(0), EMAIL);
        verify(notificationService, times(1)).sendNotificationWithFlush(notifications.get(1), EMAIL);
    }

    @Test
    public void sendNotificationWhenNoMonitoringOfficerIsAssigned() {
        User user = newUser().withId(23L).withFirstName("Rick").withLastName("McDonald").build();
        projectUser = newProjectUser()
                .withId(88L)
                .withProject(project)
                .withRole(PROJECT_MANAGER)
                .withOrganisation(empire)
                .withUser(user)
                .build();

        project.addProjectUser(projectUser);

        NotificationSource from = systemNotificationSource;
        NotificationTarget recipientPM = createProjectNotificationTarget(user);
        NotificationTarget recipientMO = createProjectNotificationTarget(orville);

        notifications = singletonList(newNotification()
                .withMessageKey(REMOVE_PROJECT_ORGANISATION)
                .withSource(from)
                .withTargets(singletonList(recipientPM))
                .withGlobalArguments(notificationArguments)
                .build());

        when(projectUserRepository.findByProjectIdAndRole(project.getId(), PROJECT_MANAGER)).thenReturn(Optional.of(projectUser));
        when(notificationService.sendNotificationWithFlush(notifications.get(0), EMAIL)).thenReturn(serviceSuccess());

        service.sendNotifications(project, ludlow);

        verify(notificationService, times(1)).sendNotificationWithFlush(notifications.get(0), EMAIL);
    }

    @Test
    public void sendNotificationsWhenNoProjectManagerIsAssigned() {
        User user = newUser().withId(23L).withFirstName("Rick").withLastName("McDonald").build();
        projectUser = newProjectUser()
                .withId(88L)
                .withProject(project)
                .withRole(PROJECT_PARTNER)
                .withOrganisation(empire)
                .withUser(user)
                .build();

        project.addProjectUser(projectUser);
        project.setProjectMonitoringOfficer(monitoringOfficer);

        NotificationSource from = systemNotificationSource;
        NotificationTarget recipientPP = createProjectNotificationTarget(user);
        NotificationTarget recipientMO = createProjectNotificationTarget(orville);

        notifications = newNotification()
                .withMessageKey(REMOVE_PROJECT_ORGANISATION)
                .withSource(from)
                .withTargets(singletonList(recipientPP), singletonList(recipientMO))
                .withGlobalArguments(notificationArguments)
                .build(2);

        when(projectUserRepository.findByProjectIdAndRole(project.getId(), PROJECT_MANAGER)).thenReturn(Optional.empty());
        when(projectUserRepository.findByProjectIdAndOrganisationId(project.getId(), empire.getId())).thenReturn(singletonList(projectUser));
        when(notificationService.sendNotificationWithFlush(notifications.get(0), EMAIL)).thenReturn(serviceSuccess());
        when(notificationService.sendNotificationWithFlush(notifications.get(1), EMAIL)).thenReturn(serviceSuccess());

        service.sendNotifications(project, ludlow);

        verify(notificationService, times(1)).sendNotificationWithFlush(notifications.get(0), EMAIL);
        verify(notificationService, times(1)).sendNotificationWithFlush(notifications.get(1), EMAIL);
    }

    private NotificationTarget createProjectNotificationTarget(User user) {
        String fullName = getProjectManagerFullName(user);
        return new UserNotificationTarget(fullName, user.getEmail());
    }

    private String getProjectManagerFullName(User projectManager) {
        return projectManager.getFirstName() + " " + projectManager.getLastName();
    }

    private String getProjectTeamLink(long projectId) {
        return format(webBaseUrl + "/project-setup/project/%d/team", projectId);
    }

    @Override
    protected RemovePartnerNotificationService supplyServiceUnderTest() {
        return new RemovePartnerNotificationServiceImpl();
    }
}