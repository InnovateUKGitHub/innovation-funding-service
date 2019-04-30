package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
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
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;

/**
 * Transactional and secure service for Project Details processing work
 */
@Service
public class ProjectTeamServiceImpl extends AbstractProjectServiceImpl implements ProjectTeamService {

    private static final String WEB_CONTEXT = "/project-setup";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private ProjectUserInviteMapper projectUserInviteMapper;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_PROJECT_MEMBER
    }

    @Override
    @Transactional
    public ServiceResult<Void> inviteTeamMember(Long projectId, ProjectUserInviteResource inviteResource) {
        return getProject(projectId)
                .andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED))
                .andOnSuccess(() -> inviteContact(projectId, inviteResource, Notifications.INVITE_PROJECT_MEMBER));
    }

    private ServiceResult<Void> inviteContact(Long projectId, ProjectUserInviteResource projectResource, Notifications kindOfNotification) {

        ProjectUserInvite projectInvite = projectUserInviteMapper.mapToDomain(projectResource);
        projectInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now());
        projectUserInviteRepository.save(projectInvite);

        Notification notification = new Notification(systemNotificationSource, createInviteContactNotificationTarget(projectInvite), kindOfNotification, createGlobalArgsForInviteContactEmail(projectId, projectResource));

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private NotificationTarget createInviteContactNotificationTarget(ProjectInvite projectInvite) {
        return new UserNotificationTarget(projectInvite.getName(), projectInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForInviteContactEmail(Long projectId, ProjectUserInviteResource inviteResource) {
        Project project = projectRepository.findById(projectId).get();
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findById(leadRole.getOrganisationId()).get();
        String leadOrganisationName = leadOrganisation.getName();
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("projectName", project.getName());
        globalArguments.put("applicationId", inviteResource.getApplicationId());
        globalArguments.put("leadOrganisation", leadOrganisationName);
        globalArguments.put("inviteOrganisationName", inviteResource.getOrganisationName());
        globalArguments.put("competitionName", inviteResource.getCompetitionName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, inviteResource));
        return globalArguments;
    }

    private String getInviteUrl(String baseUrl, ProjectUserInviteResource inviteResource) {
        return String.format("%s/accept-invite/%s", baseUrl, inviteResource.getHash());
    }

    private ServiceResult<Project> validateGOLGenerated(Project project, CommonFailureKeys failKey){
        if (project.getGrantOfferLetter() != null){
            return serviceFailure(failKey);
        }
        return serviceSuccess(project);
    }
}
