package org.innovateuk.ifs.project.monitor.transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.mapper.MonitoringOfficerInviteMapper;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.monitor.domain.MonitoringOfficerInvite;
import org.innovateuk.ifs.project.monitor.repository.MonitoringOfficerInviteRepository;
import org.innovateuk.ifs.project.monitor.repository.ProjectMonitoringOfficerRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.monitor.transactional.ProjectMonitoringOfficerServiceImpl.Notifications.MONITORING_OFFICER_INVITE;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service implementation providing operations around monitoring officers.
 */
@Service
public class ProjectMonitoringOfficerServiceImpl extends BaseTransactionalService implements ProjectMonitoringOfficerService {

    private static final Log LOG = LogFactory.getLog(ProjectMonitoringOfficerServiceImpl.class);

    private static final String DEFAULT_INTERNAL_USER_EMAIL_DOMAIN = "innovateuk.ukri.org";
    private static final String WEB_CONTEXT = "/management/monitoring-officer";

    enum Notifications {
        MONITORING_OFFICER_INVITE
    }

    @Autowired
    private MonitoringOfficerInviteRepository monitoringOfficerInviteRepository;

    @Autowired
    private ProjectMonitoringOfficerRepository monitoringOfficerRepository;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private MonitoringOfficerInviteMapper monitoringOfficerInviteMapper;

    @Value("${ifs.system.internal.user.email.domains}")
    private String internalUserEmailDomains;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    @Transactional
    public ServiceResult<Void> inviteMonitoringOfficer(UserResource invitedUser) {

        return validateInvite(invitedUser)
                .andOnSuccess(this::validateUserIsNotInternal)
                .andOnSuccess(this::validateUserInviteNotPending)
                .andOnSuccess(this::addOrInviteUser);
    }

    private ServiceResult<UserResource> validateInvite(UserResource invitedUser) {

        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName())) {
            return serviceFailure(MONITORING_OFFICER_INVITE_INVALID);
        }
        return serviceSuccess(invitedUser);
    }

    private ServiceResult<UserResource> validateUserIsNotInternal(UserResource user) {
        String emailAddress = user.getEmail();
        String domain = StringUtils.substringAfter(emailAddress, "@");
        internalUserEmailDomains = StringUtils.defaultIfBlank(internalUserEmailDomains, DEFAULT_INTERNAL_USER_EMAIL_DOMAIN);
        String[] domains = internalUserEmailDomains.split(",");
        for (String acceptedDomain : domains) {
            if (acceptedDomain.equalsIgnoreCase(domain)) {
                return serviceFailure(MONITORING_OFFICERS_CANNOT_BE_INTERNAL_USERS);
            }
        }

        return serviceSuccess(user);
    }

    private ServiceResult<UserResource> validateUserInviteNotPending(UserResource invitedUser) {
        boolean foundPendingInvite = monitoringOfficerInviteRepository.existsByStatusAndEmail(SENT, invitedUser.getEmail());
        return foundPendingInvite ? serviceFailure(MONITORING_OFFICER_INVITE_TARGET_USER_ALREADY_INVITED) : serviceSuccess(invitedUser);
    }

    private ServiceResult<Void> addOrInviteUser(UserResource invitedUser) {
        Optional<User> user = userRepository.findByEmail(invitedUser.getEmail());

        if (user.isPresent()) {
            if (!user.get().hasRole(Role.MONITORING_OFFICER)) {
                addMonitoringOfficerRoleToUser(user.get());
            }
            return serviceSuccess();
        } else {
            return saveInvite(invitedUser)
                    .andOnSuccess(this::sendMonitoringOfficerInviteNotification);
        }
    }

    private ServiceResult<MonitoringOfficerInvite> saveInvite(UserResource invitedUser) {
        MonitoringOfficerInvite monitoringOfficerInvite = new MonitoringOfficerInvite(
                invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                CREATED);

        return serviceSuccess(monitoringOfficerInviteRepository.save(monitoringOfficerInvite));
    }

    private ServiceResult<Void> sendMonitoringOfficerInviteNotification(MonitoringOfficerInvite monitoringOfficerInvite) {

        Map<String, Object> globalArgs = createGlobalArgsForMonitoringOfficerInvite(monitoringOfficerInvite);

        Notification notification = new Notification(systemNotificationSource,
                singletonList(createMonitoringOfficerInviteNotificationTarget(monitoringOfficerInvite)),
                MONITORING_OFFICER_INVITE, globalArgs);

        ServiceResult<Void> sendResult = notificationService.sendNotificationWithFlush(notification, EMAIL);

        sendResult.handleSuccessOrFailure(
                failure -> handleInviteError(monitoringOfficerInvite, failure),
                success -> handleInviteSuccess(monitoringOfficerInvite)
        );

        return sendResult;
    }

    private Map<String, Object> createGlobalArgsForMonitoringOfficerInvite(MonitoringOfficerInvite monitoringOfficerInvite) {
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, monitoringOfficerInvite));
        return globalArguments;
    }

    private static String getInviteUrl(String baseUrl, MonitoringOfficerInvite monitoringOfficerInvite) {
        return String.format("%s/%s/%s", baseUrl, monitoringOfficerInvite.getHash(), "register");
    }

    private NotificationTarget createMonitoringOfficerInviteNotificationTarget(MonitoringOfficerInvite monitoringOfficerInvite) {
        return new UserNotificationTarget(monitoringOfficerInvite.getName(), monitoringOfficerInvite.getEmail());
    }

    private ServiceResult<Void> handleInviteError(MonitoringOfficerInvite i, ServiceFailure failure) {
        LOG.error(String.format("Invite failed %s, %s (error count: %s)", i.getId(), i.getEmail(), failure.getErrors().size()));
        List<Error> errors = failure.getErrors();
        return serviceFailure(errors);
    }

    private ServiceResult<Void> handleInviteSuccess(MonitoringOfficerInvite monitoringOfficerInvite) {
        monitoringOfficerInviteRepository.save(monitoringOfficerInvite.sendOrResend(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return serviceSuccess();
    }

    @Override
    public ServiceResult<MonitoringOfficerInviteResource> getInviteByHash(String hash) {
        return getByHash(hash).andOnSuccessReturn(monitoringOfficerInviteMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<MonitoringOfficerInviteResource> openInvite(String hash) {
        return getByHash(hash)
                .andOnSuccessReturn(Invite::open)
                .andOnSuccessReturn(monitoringOfficerInviteMapper::mapToResource);
    }

    private ServiceResult<MonitoringOfficerInvite> getByHash(String hash) {
        return find(monitoringOfficerInviteRepository.getByHash(hash), notFoundError(MonitoringOfficerInvite.class, hash));
    }

    private void addMonitoringOfficerRoleToUser(User user) {
        user.addRole(Role.MONITORING_OFFICER);
        userRepository.save(user);
    }
}