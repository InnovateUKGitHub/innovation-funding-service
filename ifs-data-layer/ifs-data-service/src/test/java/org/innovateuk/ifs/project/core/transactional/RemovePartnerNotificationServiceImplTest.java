package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;

import static freemarker.template.utility.Collections12.singletonList;
import static java.lang.String.format;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.builders.NotificationBuilder.newNotification;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.*;
import static org.innovateuk.ifs.project.core.transactional.PartnerOrganisationServiceImpl.Notifications.REMOVE_PROJECT_ORGANISATION;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newMonitoringOfficer;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RemovePartnerNotificationServiceImplTest extends BaseServiceUnitTest<RemovePartnerNotificationService> {

    @Mock
    private ProjectUserRepository projectUserRepositoryMock;

    @Mock
    private MonitoringOfficerRepository monitoringOfficerRepositoryMock;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Mock
    private NotificationService notificationServiceMock;

    private Organisation organisation;
    private Application application;
    private Project project;
    private Notification notification;
    private MonitoringOfficer monitoringOfficer;
    private List<ProjectUser> projectUsers = new ArrayList<>();
    private Map<String, Object> notificationArguments = new HashMap<>();

    @Before
    public void setup() {

        organisation = newOrganisation()
                .withId(43L)
                .withOrganisationType(BUSINESS)
                .withName("Ludlow")
                .build();
        application = newApplication().withId(77L).build();

        notificationArguments.put("applicationId", application.getId());
        notificationArguments.put("projectName", project.getName());
        notificationArguments.put("organisationName", organisation.getName());
        notificationArguments.put("projectTeamLink", getProjectTeamLink(project.getId()));
    }

    @Test
    public void sendNotifications() {
        User orville = newUser().withId(3L).withFirstName("Orville").withLastName("Gibbs").build();
        monitoringOfficer = newMonitoringOfficer()
                .withId(81L)
                .withUser(orville)
                .build();

        User lyn = newUser().withId(50L).withFirstName("Lyn").withLastName("Brown").build();
        ProjectUser projectManager = newProjectUser()
                .withId(88L)
                .withProject(project)
                .withRole(PROJECT_MANAGER)
                .withOrganisation(organisation)
                .withUser(lyn)
                .build();
        ProjectUser projectPartner = newProjectUser()
                .withId(32L)
                .withRole(PROJECT_PARTNER)
                .build();
        projectUsers.add(projectManager);
        projectUsers.add(projectPartner);

        project = newProject()
                .withId(99L)
                .withName("Smart ideas for plastic recycling")
                .withApplication(application)
                .withProjectMonitoringOfficer(monitoringOfficer)
                .withProjectUsers(projectUsers)
                .build();

        NotificationSource from = systemNotificationSourceMock;
        NotificationTarget recipientPM = createProjectNotificationTarget(lyn);
        NotificationTarget recipientMO = createProjectNotificationTarget(orville);

        notification = newNotification()
                .withMessageKey(REMOVE_PROJECT_ORGANISATION)
                .withSource(from)
                .withTargets(singletonList(recipientPM))
                .withGlobalArguments(notificationArguments)
                .build();

        notification = newNotification()
                .withMessageKey(REMOVE_PROJECT_ORGANISATION)
                .withSource(from)
                .withTargets(singletonList(recipientMO))
                .withGlobalArguments(notificationArguments)
                .build();

        when(projectUserRepositoryMock.findByProjectIdAndRole(project.getId(), PROJECT_MANAGER)).thenReturn(Optional.of(projectManager));
        when(monitoringOfficerRepositoryMock.existsByProjectIdAndUserId(project.getId(), orville.getId())).thenReturn(true);
        when(service.sendNotifications(project, organisation)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.sendNotifications(project, organisation );

        assertTrue(result.isSuccess());
        verify(notificationServiceMock, times(1)).sendNotificationWithFlush(notification, EMAIL);
    }


    private NotificationTarget createProjectNotificationTarget(User user) {
        String fullName = getProjectManagerFullName(user);
        return new UserNotificationTarget(fullName, user.getEmail());
    }

    private String getProjectManagerFullName(User projectManager) {
        return projectManager.getFirstName() + " " + projectManager.getLastName();
    }

    private String getProjectTeamLink(long projectId) {
        return format("/project-setup/project/%d/team", projectId);
    }

    @Override
    protected RemovePartnerNotificationService supplyServiceUnderTest() {
        return new RemovePartnerNotificationServiceImpl();
    }

}