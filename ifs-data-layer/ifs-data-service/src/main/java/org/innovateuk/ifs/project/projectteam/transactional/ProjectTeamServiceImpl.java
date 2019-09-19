package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
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
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.financechecks.repository.ViabilityProcessRepository;
import org.innovateuk.ifs.project.grantofferletter.domain.GOLProcess;
import org.innovateuk.ifs.project.grantofferletter.repository.GrantOfferLetterProcessRepository;
import org.innovateuk.ifs.project.projectdetails.domain.ProjectDetailsProcess;
import org.innovateuk.ifs.project.projectdetails.repository.ProjectDetailsProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileProcessRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secure service for Project Team processing work
 */
@Service
public class ProjectTeamServiceImpl extends AbstractProjectServiceImpl implements ProjectTeamService {

    private static final String WEB_CONTEXT = "/project-setup";

    @Autowired
    private ProjectUserRepository projectUserRepository;

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

    @Autowired
    private EligibilityProcessRepository eligibilityProcessRepository;

    @Autowired
    private GrantOfferLetterProcessRepository grantOfferLetterProcessRepository;

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Autowired
    private SpendProfileProcessRepository spendProfileProcessRepository;

    @Autowired
    private ViabilityProcessRepository viabilityProcessRepository;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_PROJECT_MEMBER
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeInvite(long inviteId, long projectId) {
        return getInvite(inviteId)
                .andOnSuccess(invite -> getProject(projectId)
                    .andOnSuccess(project -> validateInvite(invite, project)
                        .andOnSuccess(() -> deleteInvite(invite)
                        )
                    )
                );
    }

    private ServiceResult<ProjectUserInvite> getInvite(long inviteId) {
        return find(projectUserInviteRepository.findById(inviteId),
                    notFoundError(ProjectUserInvite.class, inviteId));
    }

    private ServiceResult<Void> validateInvite(ProjectUserInvite invite, Project project) {
        if(!invite.getTarget().equals(project)) {
            return serviceFailure(PROJECT_INVITE_NOT_FOR_CORRECT_PROJECT);
        }
        if(!invite.getStatus().equals(InviteStatus.SENT)) {
            return serviceFailure(PROJECT_INVITE_ALREADY_OPENED);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> deleteInvite(ProjectUserInvite invite) {
        projectUserInviteRepository.delete(invite);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeUser(ProjectUserCompositeId composite) {
        return getProject(composite.getProjectId()).andOnSuccess(
                project -> validateUserNotPm(project, composite.getUserId()).andOnSuccess(
                        () -> validateUserNotFc(project, composite.getUserId()).andOnSuccess(
                                () -> validateUserNotRemovingThemselves(composite.getUserId()).andOnSuccess(
                                        () -> migrateProcessesFromUserIfNecessary(composite.getUserId(), project).andOnSuccess(
                                                migratedProject -> removeUserFromProject(composite.getUserId(), migratedProject)))
                )));
    }

    private ServiceResult<Void> validateUserNotPm(Project project, long userId) {
        return userHasRoleOnProject(PROJECT_MANAGER, userId, project) ?
                serviceFailure(CANNOT_REMOVE_PROJECT_MANAGER_FROM_PROJECT) :
                serviceSuccess();
    }

    private ServiceResult<Void> validateUserNotFc(Project project, long userId) {
        return userHasRoleOnProject(PROJECT_FINANCE_CONTACT, userId, project) ?
                serviceFailure(CANNOT_REMOVE_FINANCE_CONTACT_FROM_PROJECT) :
                serviceSuccess();
    }

    private ServiceResult<Void> validateUserNotRemovingThemselves(long userId) {
        return getCurrentlyLoggedInUser().andOnSuccess(
                user -> user.getId().equals(userId) ?
                        serviceFailure(CANNOT_REMOVE_YOURSELF_FROM_PROJECT) :
                        serviceSuccess());
    }

    private boolean userHasRoleOnProject(ProjectParticipantRole role, long userId, Project project) {
        return simpleAnyMatch(project.getProjectUsersWithRole(role),
                              pu -> pu.getUser().getId().equals(userId));
    }

    private ServiceResult<Void> removeUserFromProject(long userId, Project project) {
        // This will cause a delete with orphanRemoval.
        boolean removed = project.getProjectUsers().removeIf(pu -> pu.getUser().getId().equals(userId));
        if (removed) {
            return serviceSuccess();
        }
        return serviceFailure(CANNOT_REMOVE_YOURSELF_FROM_PROJECT);
    }

    private ServiceResult<Project> migrateProcessesFromUserIfNecessary(long userToDeleteId, Project project) {
        long projectId = project.getId();
        ProjectProcess projectProcess = project.getProjectProcess();

        if (!migrationNecessary(userToDeleteId, projectProcess)) {
            return serviceSuccess(project);
        }

        long leadOrgId = projectProcess.getParticipant().getOrganisation().getId();
        ProjectUser migrationTarget = chooseMigrationTarget(project, leadOrgId, userToDeleteId);
        projectProcess.setParticipant(migrationTarget);

        List<ProjectDetailsProcess> projectDetailsProcesses = projectDetailsProcessRepository.findByTargetId(projectId);
        projectDetailsProcesses.forEach(process -> process.setParticipant(migrationTarget));

        List<EligibilityProcess> eligibilityProcesses = eligibilityProcessRepository.findByTargetId(projectId);
        eligibilityProcesses.forEach(process -> process.setParticipant(migrationTarget));

        List<ViabilityProcess> viabilityProcesses = viabilityProcessRepository.findByTargetId(projectId);
        viabilityProcesses.forEach(process -> process.setParticipant(migrationTarget));

        List<SpendProfileProcess> spendProfileProcesses = spendProfileProcessRepository.findByTargetId(projectId);
        spendProfileProcesses.forEach(process -> process.setParticipant(migrationTarget));

        List<GOLProcess> golProcesses = grantOfferLetterProcessRepository.findByTargetId(projectId);
        golProcesses.forEach(process -> process.setParticipant(migrationTarget));

        return serviceSuccess(project);
    }

    private boolean migrationNecessary(long userToDeleteId, ProjectProcess process) {
        ProjectUser processUser = process.getParticipant();
        return processUser.getUser().getId().equals(userToDeleteId);
    }

    private ProjectUser chooseMigrationTarget(Project project, long leadOrgId, long userToDeleteId) {
        return getProjectManager(project).orElse(chooseRandomMemberOfLeadOrg(project, leadOrgId, userToDeleteId));
    }

    private ProjectUser chooseRandomMemberOfLeadOrg(Project project, long leadOrgId, long userToDeleteId) {
        return project.getProjectUsers().stream()
                .filter(pu -> !pu.getUser().getId().equals(userToDeleteId))
                .filter(pu -> pu.getOrganisation().getId().equals(leadOrgId))
                .findAny().get();
    }

    @Override
    @Transactional
    public ServiceResult<Void> inviteTeamMember(long projectId, ProjectUserInviteResource inviteResource) {
        return getProject(projectId)
                .andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED))
                .andOnSuccess(() -> inviteContact(projectId, inviteResource, Notifications.INVITE_PROJECT_MEMBER));
    }

    private ServiceResult<Void> inviteContact(long projectId, ProjectUserInviteResource projectResource, Notifications kindOfNotification) {

        ProjectUserInvite projectInvite = projectUserInviteMapper.mapToDomain(projectResource);
        projectInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now());
        projectUserInviteRepository.save(projectInvite);

        Notification notification = new Notification(systemNotificationSource, createInviteContactNotificationTarget(projectInvite), kindOfNotification, createGlobalArgsForInviteContactEmail(projectId, projectResource));

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private NotificationTarget createInviteContactNotificationTarget(ProjectInvite projectInvite) {
        return new UserNotificationTarget(projectInvite.getName(), projectInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForInviteContactEmail(long projectId, ProjectUserInviteResource inviteResource) {
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
        if (project.getGrantOfferLetter() != null) {
            return serviceFailure(failKey);
        }
        return serviceSuccess(project);
    }
}
