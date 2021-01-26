package org.innovateuk.ifs.project.projectteam.transactional;

import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
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
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.financechecks.repository.ViabilityProcessRepository;
import org.innovateuk.ifs.project.grantofferletter.domain.GOLProcess;
import org.innovateuk.ifs.project.grantofferletter.repository.GrantOfferLetterProcessRepository;
import org.innovateuk.ifs.project.invite.transactional.ProjectInviteValidator;
import org.innovateuk.ifs.project.projectdetails.domain.ProjectDetailsProcess;
import org.innovateuk.ifs.project.projectdetails.repository.ProjectDetailsProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileProcessRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectInviteValidator projectInviteValidator;

    private LocalValidatorFactoryBean validator;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_PROJECT_MEMBER
    }

    public ProjectTeamServiceImpl() {
        validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
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
        if (!invite.getTarget().equals(project)) {
            return serviceFailure(PROJECT_INVITE_NOT_FOR_CORRECT_PROJECT);
        }
        if (!invite.getStatus().equals(SENT)) {
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

    private ServiceResult<List<ProjectUserInviteResource>> getInvitesByProject(Long projectId) {
        if (projectId == null) {
            return serviceFailure(new Error(PROJECT_INVITE_INVALID_PROJECT_ID, NOT_FOUND));
        }
        List<ProjectUserInvite> invites = projectUserInviteRepository.findByProjectId(projectId);
        List<ProjectUserInviteResource> inviteResources = invites.stream().map(this::mapInviteToInviteResource).collect(Collectors.toList());
        return serviceSuccess(inviteResources);
    }

    private ProjectUserInviteResource mapInviteToInviteResource(ProjectUserInvite invite) {
        ProjectUserInviteResource inviteResource = projectUserInviteMapper.mapToResource(invite);
        Organisation organisation = organisationRepository.findById(inviteResource.getLeadOrganisationId()).get();
        inviteResource.setLeadOrganisation(organisation.getName());
        ProjectResource project = projectService.getProjectById(inviteResource.getProject()).getSuccess();
        inviteResource.setApplicationId(project.getApplication());
        return inviteResource;
    }

    @Override
    @Transactional
    public ServiceResult<Void> inviteTeamMember(long projectId, ProjectUserInviteResource inviteResource) {
        return getProject(projectId)
                .andOnSuccess(project -> validateGOLGenerated(project, PROJECT_SETUP_PROJECT_MANAGER_CANNOT_BE_UPDATED_IF_GOL_GENERATED))
                .andOnSuccess(() -> saveProjectInvite(inviteResource))
                .andOnSuccess(() -> inviteContact(projectId, inviteResource, Notifications.INVITE_PROJECT_MEMBER));
    }

    private ServiceResult<Void> inviteContact(long projectId, ProjectUserInviteResource requestedInvite, Notifications kindOfNotification) {
        Optional<ProjectUserInviteResource> inviteResource = getSavedInvite(projectId, requestedInvite);

        if (!inviteResource.isPresent()) {
            return serviceFailure(new Error(PROJECT_INVITE_NOT_FOUND, NOT_FOUND));
        }
        ProjectUserInvite invite = projectUserInviteMapper.mapToDomain(inviteResource.get());
        invite.send(loggedInUserSupplier.get(), ZonedDateTime.now());
        projectUserInviteRepository.save(invite);

        Notification notification = new Notification(systemNotificationSource,
                createInviteContactNotificationTarget(invite),
                kindOfNotification,
                createGlobalArgsForInviteContactEmail(projectId, inviteResource.get()));

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private ServiceResult<Void> saveProjectInvite(ProjectUserInviteResource projectUserInviteResource) {
        if (projectUserInviteResource.getId() == null) {
            return projectInviteValidator.validate(projectUserInviteResource).andOnSuccess(() -> {
                ProjectUserInvite projectInvite = projectUserInviteMapper.mapToDomain(projectUserInviteResource);
                Errors errors = new BeanPropertyBindingResult(projectInvite, projectInvite.getClass().getName());
                validator.validate(projectInvite, errors);
                if (errors.hasErrors()) {
                    errors.getFieldErrors();
                    return serviceFailure(badRequestError(errors.toString()));
                } else {
                    projectInvite.setHash(generateInviteHash());
                    projectUserInviteRepository.save(projectInvite);
                    return serviceSuccess();
                }
            });
        }
        return serviceSuccess();
    }

    private Optional<ProjectUserInviteResource> getSavedInvite(long projectId, ProjectUserInviteResource invite) {
        return simpleFindFirst(getInvitesByProject(projectId).getSuccess(),
                i -> i.getEmail().equals(invite.getEmail()));
    }


    private ServiceResult<Void> validateTargetUserIsValid(ProjectUserInviteResource invite) {

        String targetEmail = invite.getEmail();
        Optional<User> existingUser = userRepository.findByEmail(targetEmail);

        return existingUser.map(user ->
                validateUserIsNotAlreadyOnProject(invite, user)).
                orElse(serviceSuccess());
    }


    private ServiceResult<Void> validateUserIsNotAlreadyOnProject(ProjectUserInviteResource invite, User user) {

        List<ProjectUser> existingUserEntryForProject = projectUserRepository.findByProjectIdAndUserIdAndRole(invite.getProject(), user.getId(), PROJECT_PARTNER);

        return existingUserEntryForProject.isEmpty() ? serviceSuccess() :
                serviceFailure(PROJECT_SETUP_INVITE_TARGET_USER_ALREADY_EXISTS_ON_PROJECT);
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

    private ServiceResult<Project> validateGOLGenerated(Project project, CommonFailureKeys failKey) {
        if (project.getGrantOfferLetter() != null) {
            return serviceFailure(failKey);
        }
        return serviceSuccess(project);
    }
}