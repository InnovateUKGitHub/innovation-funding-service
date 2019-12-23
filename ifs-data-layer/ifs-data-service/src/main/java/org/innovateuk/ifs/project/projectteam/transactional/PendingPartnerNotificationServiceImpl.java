package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.projectteam.transactional.PendingPartnerNotificationServiceImpl.Notifications.NEW_PARTNER_ORGANISATION_JOINED;

@Service
public class PendingPartnerNotificationServiceImpl implements PendingPartnerNotificationService {

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        NEW_PARTNER_ORGANISATION_JOINED
    }

    @Override
    public void sendNotifications(PartnerOrganisation partnerOrganisation) {
        Project project = partnerOrganisation.getProject();
        Organisation organisation = partnerOrganisation.getOrganisation();
        sendNotificationToProjectTeam(project, organisation);
        sendNotificationToMonitoringOfficer(project, organisation);
    }

    private void sendNotificationToProjectTeam(Project project, Organisation organisation) {
        Optional<ProjectUser> projectManager = projectUserRepository.findByProjectIdAndRole(project.getId(), PROJECT_MANAGER);
        if (projectManager.isPresent()) {
            sendNotificationToUser(projectManager.get().getUser(), project, organisation);
        } else {
            sendNotificationToProjectUsers(project, organisation);
        }
    }

    private void sendNotificationToMonitoringOfficer(Project project, Organisation organisation) {
        Optional<MonitoringOfficer> monitoringOfficer = project.getProjectMonitoringOfficer();
        if (monitoringOfficer.isPresent()) {
            sendNotificationToUser(monitoringOfficer.get().getUser(), project, organisation);
        }
    }

    private void sendNotificationToProjectUsers(Project project, Organisation organisation) {
        long leadOrganisationId = project.getLeadOrganisation().get().getOrganisation().getId();
        List<ProjectUser> projectUsers = projectUserRepository.findByProjectIdAndOrganisationId(project.getId(), leadOrganisationId);
        projectUsers.forEach(pu -> sendNotificationToUser(pu.getUser(), project, organisation));
    }

    private void sendNotificationToUser(User user, Project project, Organisation organisation) {
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = createProjectNotificationTarget(user);

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationId", project.getApplication().getId());
        notificationArguments.put("projectName", project.getName());
        notificationArguments.put("organisationName", organisation.getName());
        notificationArguments.put("projectTeamLink", getProjectTeamLink(project.getId()));

        Notification notification = new Notification(from, singletonList(to), NEW_PARTNER_ORGANISATION_JOINED, notificationArguments);
        notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private NotificationTarget createProjectNotificationTarget(User user) {
        String fullName = getProjectManagerFullName(user);
        return new UserNotificationTarget(fullName, user.getEmail());
    }

    private String getProjectTeamLink(long projectId) {
        return format(webBaseUrl + "/project-setup/project/%d/team", projectId);
    }

    private String getProjectManagerFullName(User projectManager) {
        return projectManager.getFirstName() + " " + projectManager.getLastName();
    }
}