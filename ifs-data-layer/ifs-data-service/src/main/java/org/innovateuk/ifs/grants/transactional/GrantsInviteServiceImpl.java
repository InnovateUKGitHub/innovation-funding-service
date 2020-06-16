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
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
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

    @Autowired
    private RegistrationService registrationService;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    enum Notifications {
        INVITE_GRANTS_USER,
    }

    @Override
    public ServiceResult<List<SentGrantsInviteResource>> getByProjectId(long projectId) {
        return serviceSuccess(grantsInviteRepository.findByProjectId(projectId).stream()
                .map(this::mapToSentResource)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public ServiceResult<Void> sendInvite(long projectId, GrantsInviteResource invite) {
        return find(project(projectId), organisationIfOnResource(invite)).andOnSuccess((project, organisation) -> {
                GrantsInvite grantsInvite = getInviteType(invite);
                grantsInvite.setOrganisation(organisation);
                grantsInvite.setEmail(invite.getEmail());
                grantsInvite.setName(invite.getUserName());
                grantsInvite.setHash(generateInviteHash());
                grantsInvite.setTarget(project);
                getInviteRepository(invite).save(grantsInvite);
                return sendInviteNotification(grantsInvite)
                        .andOnSuccessReturnVoid((sentInvite) -> sentInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
            });
    }

    private Supplier<ServiceResult<Organisation>> organisationIfOnResource(GrantsInviteResource invite) {
        if (invite.getOrganisationId() != null) {
            return organisation(invite.getOrganisationId());
        }
        return () -> serviceSuccess(null);
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
        notificationArguments.put("applicationId", grantsInvite.getProject().getApplication().getId());
        notificationArguments.put("projectName", grantsInvite.getProject().getName());
        notificationArguments.put("role", getGrantsInviteRole(grantsInvite.getClass()).getDisplayName());
        notificationArguments.put("inviteUrl", String.format("%s/project-setup/project/%d/grants/invite/%s", webBaseUrl, grantsInvite.getProject().getId(), grantsInvite.getHash()));
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget(grantsInvite.getName(), grantsInvite.getEmail());

        return new Notification(from, singletonList(to), Notifications.INVITE_GRANTS_USER, notificationArguments);
    }


    @Override
    @Transactional
    public ServiceResult<Void> resendInvite(long inviteId) {
        return find(grantsInviteRepository.findById(inviteId), notFoundError(GrantsInvite.class, inviteId))
                .andOnSuccess(invite -> {
                    invite.setHash(generateInviteHash());
                    return sendInviteNotification(invite);
                })
                .andOnSuccessReturnVoid((sentInvite) -> sentInvite.resend(loggedInUserSupplier.get(), ZonedDateTime.now()));
    }

    @Override
    public ServiceResult<SentGrantsInviteResource> getInviteByHash(String hash) {
        return find(grantsInviteRepository.getByHash(hash), notFoundError(GrantsInvite.class, hash))
                .andOnSuccessReturn(this::mapToSentResource);
    }

    private SentGrantsInviteResource mapToSentResource(GrantsInvite grantsInvite) {
        return new SentGrantsInviteResource(
                ofNullable(grantsInvite.getOrganisation()).map(Organisation::getId).orElse(null),
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
    @Transactional
    public ServiceResult<Void> acceptInvite(long inviteId) {
        return find(grantsInviteRepository.findById(inviteId), notFoundError(GrantsInvite.class, inviteId))
                .andOnSuccess(invite -> {
                    Project project = invite.getProject();
                    projectUserRepository.save(new ProjectUser(invite.getUser(), project, getProjectParticipantRole(invite.getClass()), invite.getOrganisation()));
                    invite.open();
                    Role roleToAdd = getRole(invite.getClass());
                    if (invite.getUser().hasRole(roleToAdd)) {
                        invite.getUser().addRole(roleToAdd);
                    }
                    if (!invite.getUser().hasRole(LIVE_PROJECTS_USER)) {
                        invite.getUser().addRole(LIVE_PROJECTS_USER);
                    }
                    return serviceSuccess();
                });
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
