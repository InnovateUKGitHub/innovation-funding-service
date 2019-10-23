package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.MONITORING_OFFICER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.core.transactional.PartnerOrganisationServiceImpl.Notifications.REMOVE_PROJECT_ORGANISATION;

@Service
public class RemovePartnerNotificationServiceImpl implements  RemovePartnerNotificationService {

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ServiceResult<Void> sendNotifications(long projectId, Organisation organisation) {
        sendNotificationToProjectTeam(projectId, organisation);
        sendNotificationToMonitoringOfficer(projectId, organisation);
        return serviceSuccess();
    }

    private void sendNotificationToProjectTeam(long projectId, Organisation organisation) {
        Optional<ProjectUser> projectManager = projectUserRepository.findByProjectIdAndRole(projectId, PROJECT_MANAGER);
        if (projectManager.isPresent()) {
            sendNotificationToUser(projectManager.get(), organisation);
        } else {
            sendNotificationToProjectUsers(projectId, organisation);
        }
    }

    private void sendNotificationToMonitoringOfficer(long projectId, Organisation organisation) {
        Optional<ProjectUser> monitoringOfficer = projectUserRepository.findByProjectIdAndRole(projectId, MONITORING_OFFICER);
        if (monitoringOfficer.isPresent()) {
            sendNotificationToUser(monitoringOfficer.get(), organisation);
        }
    }

    private void sendNotificationToProjectUsers(long projectId, Organisation organisation) {
        long leadOrganisationId = partnerOrganisationRepository.findByProjectId(projectId)
                .stream()
                .filter(PartnerOrganisation::isLeadOrganisation)
                .map(partnerOrganisation -> partnerOrganisation.getOrganisation().getId())
                .findAny()
                .get();

        List<ProjectUser> projectUsers = projectUserRepository.findByProjectIdAndOrganisationId(projectId, leadOrganisationId);
        projectUsers.forEach(user -> sendNotificationToUser(user, organisation));
    }

    private void sendNotificationToUser(ProjectUser projectUser, Organisation organisation) {
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = createProjectNotificationTarget(projectUser.getUser());

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("applicationId", projectUser.getProcess().getApplication().getId());
        notificationArguments.put("projectName", projectUser.getProcess().getName());
        notificationArguments.put("organisationName", organisation.getName());
        notificationArguments.put("projectTeamLink", getProjectTeamLink(projectUser.getProcess().getId()));

        Notification notification = new Notification(from, singletonList(to), REMOVE_PROJECT_ORGANISATION, notificationArguments);
        notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private NotificationTarget createProjectNotificationTarget(User user) {
        String fullName = getProjectManagerFullName(user);
        return new UserNotificationTarget(fullName, user.getEmail());
    }

    private String getProjectTeamLink(long projectId) {
        return format("/project-setup/project/%d/team", projectId);
    }

    private String getProjectManagerFullName(User projectManager) {
        return projectManager.getFirstName() + " " + projectManager.getLastName();
    }
}