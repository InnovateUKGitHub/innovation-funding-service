package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.PartnerOrganisationMapper;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.PendingPartnerProgressRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNOT_REMOVE_LEAD_ORGANISATION_FROM_PROJECT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.MONITORING_OFFICER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.core.transactional.PartnerOrganisationServiceImpl.Notifications.REMOVE_PROJECT_ORGANISATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class PartnerOrganisationServiceImpl implements PartnerOrganisationService {

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private PartnerOrganisationMapper partnerOrganisationMapper;

    @Autowired
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Autowired
    private PendingPartnerProgressRepository pendingPartnerProgressRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private PartnerChangeService partnerChangeService;

    enum Notifications {
        REMOVE_PROJECT_ORGANISATION
    }

    @Override
    public ServiceResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId) {
        return find(partnerOrganisationRepository.findByProjectId(projectId),
                notFoundError(PartnerOrganisation.class, id)).
                andOnSuccessReturn(lst -> simpleMap(lst, partnerOrganisationMapper::mapToResource));
    }

    @Override
    public ServiceResult<PartnerOrganisationResource> getPartnerOrganisation(Long projectId, Long organisationId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId),
                notFoundError(PartnerOrganisation.class)).
                andOnSuccessReturn(partnerOrganisationMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> removePartnerOrganisation(long projectId, long organisationId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId),
                notFoundError(PartnerOrganisation.class, id)).andOnSuccessReturnVoid(
                projectPartner -> validatePartnerNotLead(projectPartner).andOnSuccess(
                        () -> {
                            removePartnerOrg(projectId, projectPartner.getOrganisation().getId());
                            sendNotifications(projectId, projectPartner.getOrganisation());
                            partnerChangeService.updateProjectRemovedPartner(projectId, organisationId);
                        })
        );
    }

    private ServiceResult<Void> validatePartnerNotLead(PartnerOrganisation partnerOrganisation) {
        return partnerOrganisation.isLeadOrganisation() ?
                serviceFailure(CANNOT_REMOVE_LEAD_ORGANISATION_FROM_PROJECT) :
                serviceSuccess();
    }

    private void sendNotifications(long projectId, Organisation organisation) {
        sendNotificationToProjectTeam(projectId, organisation);
        sendNotificationToMonitoringOfficer(projectId, organisation);
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

    private String getProjectTeamLink(long projectId) {
        return format("/project-setup/project/%d/team", projectId);
    }

    private void removePartnerOrg(long projectId, long organisationId) {
        projectUserInviteRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        projectUserRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        partnerOrganisationRepository.deleteOneByProjectIdAndOrganisationId(projectId, organisationId);
        Optional<PendingPartnerProgress> pendingPartnerProgress = pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(organisationId, projectId);
        if (pendingPartnerProgress.isPresent()) {
            pendingPartnerProgressRepository.deleteById(pendingPartnerProgress.get().getId());
        }
        deleteProjectFinance(projectId, organisationId);
    }

    private void deleteProjectFinance(long projectId, long organisationId) {
        find(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId),
                notFoundError(ProjectFinance.class)).andOnSuccessReturnVoid(projectFinance -> {
                    deleteThreads(projectFinance.getId(), projectFinance.getProject().getApplication().getId(), organisationId);
                    projectFinanceRowRepository.deleteAllByTargetId(projectFinance.getId());
                    projectFinanceRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        });
    }

    private void deleteThreads(long projectFinanceId, long applicationId, long organisationId) {
        activityLogRepository.deleteAllByApplicationIdAndOrganisationId(applicationId, organisationId);
        noteRepository.deleteAllByClassPk(projectFinanceId);
        queryRepository.deleteAllByClassPk(projectFinanceId);
    }

    private NotificationTarget createProjectNotificationTarget(User user) {
        String fullName = getProjectManagerFullName(user);
        return new UserNotificationTarget(fullName, user.getEmail());
    }

    private String getProjectManagerFullName(User projectManager) {
        return projectManager.getFirstName() + " " + projectManager.getLastName();
    }
}
