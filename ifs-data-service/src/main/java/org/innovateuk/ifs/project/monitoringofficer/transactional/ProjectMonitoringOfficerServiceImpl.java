package org.innovateuk.ifs.project.monitoringofficer.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.monitoringofficer.mapper.MonitoringOfficerMapper;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.transactional.EmailService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE;
import static org.innovateuk.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElementOrEmpty;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectMonitoringOfficerServiceImpl extends AbstractProjectServiceImpl implements ProjectMonitoringOfficerService {

    @Autowired
    private MonitoringOfficerMapper monitoringOfficerMapper;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Autowired
    private  EmailService projectEmailService;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        MONITORING_OFFICER_ASSIGNED,
        MONITORING_OFFICER_ASSIGNED_PROJECT_MANAGER
    }

    @Override
    public ServiceResult<MonitoringOfficerResource> getMonitoringOfficer(Long projectId) {
        return getExistingMonitoringOfficerForProject(projectId).andOnSuccessReturn(monitoringOfficerMapper::mapToResource);
    }

    private ServiceResult<MonitoringOfficer> getExistingMonitoringOfficerForProject(Long projectId) {
        return find(monitoringOfficerRepository.findOneByProjectId(projectId), notFoundError(MonitoringOfficer.class, projectId));
    }

    @Override
    public ServiceResult<SaveMonitoringOfficerResult> saveMonitoringOfficer(final Long projectId, final MonitoringOfficerResource monitoringOfficerResource) {

        return validateMonitoringOfficer(projectId, monitoringOfficerResource).
                andOnSuccess(() -> validateInMonitoringOfficerAssignableState(projectId)).
                andOnSuccess(() -> saveMonitoringOfficer(monitoringOfficerResource));
    }

    private ServiceResult<Void> validateMonitoringOfficer(final Long projectId, final MonitoringOfficerResource monitoringOfficerResource) {

        if (!projectId.equals(monitoringOfficerResource.getProject())) {
            return serviceFailure(PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE);
        } else {
            return serviceSuccess();
        }
    }

    private ServiceResult<Void> validateInMonitoringOfficerAssignableState(final Long projectId) {

        return getProject(projectId).andOnSuccess(project -> {
            if (!projectDetailsWorkflowHandler.isSubmitted(project)) {
                return serviceFailure(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED);
            } else {
                return serviceSuccess();
            }
        });
    }

    private ServiceResult<SaveMonitoringOfficerResult> saveMonitoringOfficer(final MonitoringOfficerResource monitoringOfficerResource) {

        return getExistingMonitoringOfficerForProject(monitoringOfficerResource.getProject()).handleSuccessOrFailure(
                noMonitoringOfficer -> saveNewMonitoringOfficer(monitoringOfficerResource),
                existingMonitoringOfficer -> updateExistingMonitoringOfficer(existingMonitoringOfficer, monitoringOfficerResource)
        );
    }

    private ServiceResult<SaveMonitoringOfficerResult> saveNewMonitoringOfficer(MonitoringOfficerResource monitoringOfficerResource) {
        SaveMonitoringOfficerResult result = new SaveMonitoringOfficerResult();
        MonitoringOfficer monitoringOfficer = monitoringOfficerMapper.mapToDomain(monitoringOfficerResource);
        monitoringOfficerRepository.save(monitoringOfficer);
        return serviceSuccess(result);
    }

    private ServiceResult<SaveMonitoringOfficerResult> updateExistingMonitoringOfficer(MonitoringOfficer existingMonitoringOfficer, MonitoringOfficerResource updateDetails) {
        SaveMonitoringOfficerResult result = new SaveMonitoringOfficerResult();

        if (isMonitoringOfficerDetailsChanged(existingMonitoringOfficer, updateDetails)) {
            existingMonitoringOfficer.setFirstName(updateDetails.getFirstName());
            existingMonitoringOfficer.setLastName(updateDetails.getLastName());
            existingMonitoringOfficer.setEmail(updateDetails.getEmail());
            existingMonitoringOfficer.setPhoneNumber(updateDetails.getPhoneNumber());
        } else {
            result.setMonitoringOfficerSaved(false);
        }

        return serviceSuccess(result);
    }

    private boolean isMonitoringOfficerDetailsChanged(MonitoringOfficer existingMonitoringOfficer, MonitoringOfficerResource updateDetails) {
        return !existingMonitoringOfficer.getFirstName().equals(updateDetails.getFirstName()) ||
                !existingMonitoringOfficer.getLastName().equals(updateDetails.getLastName()) ||
                !existingMonitoringOfficer.getEmail().equals(updateDetails.getEmail()) ||
                !existingMonitoringOfficer.getPhoneNumber().equals(updateDetails.getPhoneNumber());
    }

    @Override
    public ServiceResult<Void> notifyStakeholdersOfMonitoringOfficerChange(MonitoringOfficerResource monitoringOfficer) {

        Project project = projectRepository.findOne(monitoringOfficer.getProject());
        User projectManager = getExistingProjectManager(project).get().getUser();

        NotificationTarget moTarget = createMonitoringOfficerNotificationTarget(monitoringOfficer);
        NotificationTarget pmTarget = createProjectManagerNotificationTarget(projectManager);

        ServiceResult<Void> moAssignedEmailSendResult = projectEmailService.sendEmail(singletonList(moTarget),
                createGlobalArgsForMonitoringOfficerAssignedEmail(monitoringOfficer, project, projectManager),
                ProjectMonitoringOfficerServiceImpl.Notifications.MONITORING_OFFICER_ASSIGNED);
        ServiceResult<Void> pmAssignedEmailSendResult = projectEmailService.sendEmail(singletonList(pmTarget),
                createGlobalArgsForMonitoringOfficerAssignedEmail(monitoringOfficer, project, projectManager),
                ProjectMonitoringOfficerServiceImpl.Notifications.MONITORING_OFFICER_ASSIGNED_PROJECT_MANAGER);

        return processAnyFailuresOrSucceed(asList(moAssignedEmailSendResult, pmAssignedEmailSendResult));
    }

    private Optional<ProjectUser> getExistingProjectManager(Project project) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        List<ProjectUser> projectManagers = simpleFilter(projectUsers, pu -> pu.getRole().isProjectManager());
        return getOnlyElementOrEmpty(projectManagers);
    }

    private NotificationTarget createMonitoringOfficerNotificationTarget(MonitoringOfficerResource monitoringOfficer) {

        String fullName = getMonitoringOfficerFullName(monitoringOfficer);

        return new ExternalUserNotificationTarget(fullName, monitoringOfficer.getEmail());

    }

    private String getMonitoringOfficerFullName(MonitoringOfficerResource monitoringOfficer) {
        // At this stage, validation has already been done to ensure that first name and last name are not empty
        return monitoringOfficer.getFirstName() + " " + monitoringOfficer.getLastName();
    }

    private NotificationTarget createProjectManagerNotificationTarget(final User projectManager) {
        String fullName = getProjectManagerFullName(projectManager);

        return new ExternalUserNotificationTarget(fullName, projectManager.getEmail());
    }

    private String getProjectManagerFullName(User projectManager) {
        // At this stage, validation has already been done to ensure that first name and last name are not empty
        return projectManager.getFirstName() + " " + projectManager.getLastName();
    }

    private Map<String, Object> createGlobalArgsForMonitoringOfficerAssignedEmail(MonitoringOfficerResource monitoringOfficer, Project project, User projectManager) {
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("dashboardUrl", webBaseUrl);
        globalArguments.put("projectName", project.getName());
        ProcessRole leadRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadRole.getOrganisationId());
        globalArguments.put("leadOrganisation", leadOrganisation.getName());
        globalArguments.put("projectManagerName", getProjectManagerFullName(projectManager));
        globalArguments.put("projectManagerEmail", projectManager.getEmail());
        globalArguments.put("monitoringOfficerName", getMonitoringOfficerFullName(monitoringOfficer));
        globalArguments.put("monitoringOfficerTelephone", monitoringOfficer.getPhoneNumber());
        globalArguments.put("monitoringOfficerEmail", monitoringOfficer.getEmail());
        return globalArguments;

    }
}
