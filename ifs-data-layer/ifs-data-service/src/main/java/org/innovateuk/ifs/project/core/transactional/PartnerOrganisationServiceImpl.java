package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.PartnerOrganisationMapper;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.PendingPartnerProgressRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.activitylog.resource.ActivityType.ORGANISATION_REMOVED;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNOT_REMOVE_LEAD_ORGANISATION_FROM_PROJECT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.core.transactional.PartnerOrganisationServiceImpl.Notifications.REMOVE_PROJECT_ORGANISATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional(readOnly = true)
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
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        REMOVE_PROJECT_ORGANISATION
    }

    @Override
    public ServiceResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId) {
        return find(partnerOrganisationRepository.findByProjectId(projectId),
                notFoundError(PartnerOrganisation.class)).
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
    public ServiceResult<Void> removePartnerOrganisation(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId()),
                notFoundError(PartnerOrganisation.class)).andOnSuccess(
                projectPartner -> validatePartnerNotLead(projectPartner).andOnSuccessReturnVoid(
                        () -> {
                            removePartnerOrg(projectOrganisationCompositeId.getProjectId(), projectPartner.getOrganisation().getId());
                            sendNotifications(projectPartner.getProject(), projectPartner.getOrganisation());

                        })
        );
    }

    private ServiceResult<Void> validatePartnerNotLead(PartnerOrganisation partnerOrganisation) {
        return partnerOrganisation.isLeadOrganisation() ?
                serviceFailure(CANNOT_REMOVE_LEAD_ORGANISATION_FROM_PROJECT) :
                serviceSuccess();
    }

    private void sendNotifications(Project project, Organisation organisation) {
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

        Notification notification = new Notification(from, singletonList(to), REMOVE_PROJECT_ORGANISATION, notificationArguments);
        notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private String getProjectTeamLink(long projectId) {
        return format(webBaseUrl +"/project-setup/project/%d/team", projectId);
    }

    private void removePartnerOrg(long projectId, long organisationId) {
        projectUserInviteRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        projectPartnerInviteRepository.deleteByProjectIdAndInviteOrganisationOrganisationId(projectId, organisationId);
        projectUserRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        partnerOrganisationRepository.deleteOneByProjectIdAndOrganisationId(projectId, organisationId);
        Optional<PendingPartnerProgress> pendingPartnerProgress = pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(organisationId, projectId);
        if (pendingPartnerProgress.isPresent()) {
            pendingPartnerProgressRepository.deleteById(pendingPartnerProgress.get().getId());
        }
        deleteProjectFinance(projectId, organisationId);
        deleteBankDetails(projectId, organisationId);
        activityLogService.recordActivityByProjectIdAndOrganisationId(projectId, organisationId, ORGANISATION_REMOVED);
    }

    private void deleteBankDetails(long projectId, long organisationId) {
        Optional<BankDetails> bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId);
        if (bankDetails.isPresent()) {
            bankDetailsRepository.delete(bankDetails.get());
        }
    }

    private void deleteProjectFinance(long projectId, long organisationId) {
        find(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisationId),
                notFoundError(ProjectFinance.class)).andOnSuccessReturnVoid(projectFinance -> {
                    deleteThreads(projectFinance.getId());
                    projectFinanceRowRepository.deleteAllByTargetId(projectFinance.getId());
                    projectFinanceRepository.deleteAllByProjectIdAndOrganisationId(projectId, organisationId);
        });
    }

    private void deleteThreads(long projectFinanceId) {
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
