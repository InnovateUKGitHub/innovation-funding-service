package org.innovateuk.ifs.grants.transactional;

import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grants.domain.GrantsFinanceContactInvite;
import org.innovateuk.ifs.grants.domain.GrantsInvite;
import org.innovateuk.ifs.grants.domain.GrantsMonitoringOfficerInvite;
import org.innovateuk.ifs.grants.domain.GrantsProjectManagerInvite;
import org.innovateuk.ifs.grants.repository.GrantsFinanceContactInviteRepository;
import org.innovateuk.ifs.grants.repository.GrantsInviteRepository;
import org.innovateuk.ifs.grants.repository.GrantsMonitoringOfficerInviteRepository;
import org.innovateuk.ifs.grants.repository.GrantsProjectManagerInviteRepository;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.activitylog.resource.ActivityType.*;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole.GRANTS_PROJECT_MANAGER;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class GrantsInviteServiceImpl extends BaseTransactionalService implements GrantsInviteService {

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private GrantsInviteRepository grantsInviteRepository;

    @Autowired
    private GrantsFinanceContactInviteRepository grantsFinanceContactInviteRepository;

    @Autowired
    private GrantsMonitoringOfficerInviteRepository grantsMonitoringOfficerInviteRepository;

    @Autowired
    private GrantsProjectManagerInviteRepository grantsProjectManagerInviteRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_GRANTS_PROJECT_MANAGER,
        INVITE_GRANTS_MONITORING_OFFICER,
        INVITE_GRANTS_PROJECT_FINANCE_CONTACT,
    }

    @Override
    public ServiceResult<Void> sendInvite(long projectId, GrantsInviteResource invite) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class, projectId)).andOnSuccess(project -> {
            InviteOrganisation inviteOrganisation = new InviteOrganisation();
            inviteOrganisation.setOrganisationName(invite.getOrganisationName());
            inviteOrganisation = inviteOrganisationRepository.save(inviteOrganisation);

            GrantsInvite grantsInvite = getInviteType(invite);
            grantsInvite.setInviteOrganisation(inviteOrganisation);
            grantsInvite.setEmail(invite.getEmail());
            grantsInvite.setName(invite.getUserName());
            grantsInvite.setHash(generateInviteHash());
            grantsInvite.setTarget(project);
            getInviteRepository(invite).save(grantsInvite);
            return sendInviteNotification(grantsInvite)
                    .andOnSuccessReturnVoid((sentInvite) -> sentInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
        });
    }

    private InviteRepository getInviteRepository(GrantsInviteResource invite) {
        switch (invite.getGrantsInviteRole()) {
            case GRANTS_PROJECT_MANAGER:
                return grantsProjectManagerInviteRepository;
            case GRANTS_PROJECT_FINANCE_CONTACT:
                return grantsFinanceContactInviteRepository;
            case GRANTS_MONITORING_OFFICER:
                return grantsMonitoringOfficerInviteRepository;
        }
        throw new IFSRuntimeException("Unknown invite role: " + invite.getGrantsInviteRole().name());
    }

    private GrantsInvite getInviteType(GrantsInviteResource invite) {
        switch (invite.getGrantsInviteRole()) {
            case GRANTS_PROJECT_MANAGER:
                return new GrantsProjectManagerInvite();
            case GRANTS_PROJECT_FINANCE_CONTACT:
                return new GrantsFinanceContactInvite();
            case GRANTS_MONITORING_OFFICER:
                return new GrantsMonitoringOfficerInvite();
        }
        throw new IFSRuntimeException("Unknown invite role: " + invite.getGrantsInviteRole().name());
    }

    private ServiceResult<GrantsInvite> sendInviteNotification(GrantsInvite grantsInvite) {
        return find(grantsInvite.getTarget().getLeadOrganisation(), notFoundError(Organisation.class)).andOnSuccess(leadOrganisation -> {

            Notification notification = getNotification(grantsInvite);

            activityLogService.recordActivityByApplicationId(grantsInvite.getProject().getApplication().getId(), getActivityType(grantsInvite.getClass()));

            return notificationService.sendNotificationWithFlush(notification, EMAIL)
                    .andOnSuccessReturn(() -> grantsInvite);
        });
    }

    private Notification getNotification(GrantsInvite grantsInvite) {

        Map<String, Object> notificationArguments = new HashMap<>();
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget(grantsInvite.getName(), grantsInvite.getEmail());

        return new Notification(from, singletonList(to), getEmailTemplate(grantsInvite.getClass()), notificationArguments);
    }

    private Notifications getEmailTemplate(Class<? extends GrantsInvite> clazz) {
        if (GrantsProjectManagerInvite.class.equals(clazz)) {
            return Notifications.INVITE_GRANTS_PROJECT_MANAGER;
        } else if (GrantsMonitoringOfficerInvite.class.equals(clazz)) {
            return Notifications.INVITE_GRANTS_MONITORING_OFFICER;
        } else if (GrantsFinanceContactInvite.class.equals(clazz)) {
            return Notifications.INVITE_GRANTS_PROJECT_FINANCE_CONTACT;
        }
        throw new IFSRuntimeException("No matching email template");
    }

    @Override
    public ServiceResult<Void> resendInvite(long inviteId) {
        return find(grantsInviteRepository.findById(inviteId), notFoundError(GrantsInvite.class, inviteId))
                .andOnSuccess(this::sendInviteNotification)
                .andOnSuccessReturnVoid((sentInvite) -> sentInvite.resend(loggedInUserSupplier.get(), ZonedDateTime.now()));
    }

    @Override
    public ServiceResult<SentGrantsInviteResource> getInviteByHash(String hash) {
        return find(grantsInviteRepository.getByHash(hash), notFoundError(GrantsInvite.class, hash))
                .andOnSuccessReturn(this::mapToSentResource);
    }

    private SentGrantsInviteResource mapToSentResource(GrantsInvite grantsInvite) {
        return new SentGrantsInviteResource(
                grantsInvite.getInviteOrganisation().getOrganisationName(),
                grantsInvite.getName(),
                grantsInvite.getEmail(),
                getGrantsInviteRole(grantsInvite.getClass()),
                grantsInvite.getId(),
                grantsInvite.getProject().getApplication().getId(),
                grantsInvite.getProject().getName(),
                ofNullable(grantsInvite.getUser()).map(User::getId).orElse(null),
                grantsInvite.getStatus(),
                grantsInvite.getSentOn());
    }

    @Override
    public ServiceResult<Void> acceptInvite(long inviteId, long organisationId) {
        return find(grantsInviteRepository.findById(inviteId), notFoundError(GrantsInvite.class, inviteId))
                .andOnSuccess(invite ->
                        find(organisation(organisationId))
                                .andOnSuccess((organisation) -> {
                                    Project project = invite.getProject();
                                    invite.getInviteOrganisation().setOrganisation(organisation);
                                    projectUserRepository.save(new ProjectUser(invite.getUser(), project, getProjectParticipantRole(invite.getClass()), organisation));
                                    invite.open();
                                    Role roleToAdd = getRole(invite.getClass());
                                    if (invite.getUser().hasRole(roleToAdd)) {
                                        invite.getUser().addRole(roleToAdd);
                                    }
                                    if (!invite.getUser().hasRole(LIVE_PROJECTS_USER)) {
                                        invite.getUser().addRole(LIVE_PROJECTS_USER);
                                    }
                                    return serviceSuccess();
                                }));
    }

    private ActivityType getActivityType(Class<? extends GrantsInvite> clazz) {
        if (GrantsProjectManagerInvite.class.equals(clazz)) {
            return GRANTS_PROJECT_MANAGER_INVITED;
        } else if (GrantsFinanceContactInvite.class.equals(clazz)) {
            return GRANTS_FINANCE_CONTACT_INVITED;
        } else if (GrantsMonitoringOfficerInvite.class.equals(clazz)) {
            return GRANTS_MONITORING_OFFICER_INVITED;
        } else {
            throw new IFSRuntimeException("Unknown invite: " + clazz.getName());
        }
    }

    private Role getRole(Class<? extends GrantsInvite> clazz) {
        if (GrantsProjectManagerInvite.class.equals(clazz)) {
            return APPLICANT;
        } else if (GrantsFinanceContactInvite.class.equals(clazz)) {
            return APPLICANT;
        } else if (GrantsMonitoringOfficerInvite.class.equals(clazz)) {
            return MONITORING_OFFICER;
        } else {
            throw new IFSRuntimeException("Unknown invite: " + clazz.getName());
        }
    }

    private ProjectParticipantRole getProjectParticipantRole(Class<? extends GrantsInvite> clazz) {
        if (GrantsProjectManagerInvite.class.equals(clazz)) {
            return ProjectParticipantRole.GRANTS_PROJECT_MANAGER;
        } else if (GrantsFinanceContactInvite.class.equals(clazz)) {
            return ProjectParticipantRole.GRANTS_PROJECT_FINANCE_CONTACT;
        } else if (GrantsMonitoringOfficerInvite.class.equals(clazz)) {
            return ProjectParticipantRole.GRANTS_MONITORING_OFFICER;
        } else {
            throw new IFSRuntimeException("Unknown invite: " + clazz.getName());
        }
    }

    private GrantsInviteRole getGrantsInviteRole(Class<? extends GrantsInvite> clazz) {
        if (GrantsProjectManagerInvite.class.equals(clazz)) {
            return GRANTS_PROJECT_MANAGER;
        } else if (GrantsFinanceContactInvite.class.equals(clazz)) {
            return GrantsInviteRole.GRANTS_PROJECT_FINANCE_CONTACT;
        } else if (GrantsMonitoringOfficerInvite.class.equals(clazz)) {
            return GrantsInviteRole.GRANTS_MONITORING_OFFICER;
        } else {
            throw new IFSRuntimeException("Unknown invite: " + clazz.getName());
        }
    }
}
