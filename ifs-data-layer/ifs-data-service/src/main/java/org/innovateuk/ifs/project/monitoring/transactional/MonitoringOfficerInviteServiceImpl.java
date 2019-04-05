package org.innovateuk.ifs.project.monitoring.transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.mapper.MonitoringOfficerInviteMapper;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.invite.transactional.InviteService;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficerInvite;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerInviteRepository;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerInviteServiceImpl.Notifications.MONITORING_OFFICER_NEW_PROJECT_NOTIFICATION;
import static org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerInviteServiceImpl.Notifications.MONITORING_OFFICER_REGISTRATION_INVITE;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;

/**
 * Transactional and secured service implementation providing operations around inviting monitoring officers.
 */
@Service
public class MonitoringOfficerInviteServiceImpl extends InviteService<MonitoringOfficerInvite> implements MonitoringOfficerInviteService {

    private static final Log LOG = LogFactory.getLog(MonitoringOfficerInviteServiceImpl.class);

    private static final String DEFAULT_INTERNAL_USER_EMAIL_DOMAIN = "innovateuk.ukri.org";
    private static final String WEB_CONTEXT = "/management/monitoring-officer";

    enum Notifications {
        MONITORING_OFFICER_REGISTRATION_INVITE,
        MONITORING_OFFICER_NEW_PROJECT_NOTIFICATION
    }

    @Autowired
    private MonitoringOfficerInviteRepository monitoringOfficerInviteRepository;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private MonitoringOfficerInviteMapper monitoringOfficerInviteMapper;

    @Autowired
    private RegistrationService registrationService;

    @Value("${ifs.system.internal.user.email.domains}")
    private String internalUserEmailDomains;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    @Transactional
    public ServiceResult<Void> inviteMonitoringOfficer(User invitedUser, Project project) {

        return validateInvite(invitedUser)
                .andOnSuccess(this::validateUserExists)
                .andOnSuccess(this::validateUserIsNotInternal)
                .andOnSuccess(user -> sendEmailToRegisteredOrUnregistered(user, project));
    }

    private ServiceResult<User> validateInvite(User invitedUser) {

        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName())) {
            return serviceFailure(MONITORING_OFFICER_INVITE_INVALID);
        }
        return serviceSuccess(invitedUser);
    }

    private ServiceResult<User> validateUserExists(User invitedUser) {
        // for monitoring officers, the invite is only sent after the user has been created
        // and then assigned to a project
        boolean foundUser = userRepository.existsById(invitedUser.getId());
        return foundUser?
                serviceSuccess(invitedUser) :
                serviceFailure(MONITORING_OFFICER_INVITE_INVALID);
    }

    private ServiceResult<User> validateUserIsNotInternal(User user) {
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

    private ServiceResult<Void> sendEmailToRegisteredOrUnregistered(User invitedUser, Project project) {
        User user = userRepository.findById(invitedUser.getId()).get();
        return UserStatus.PENDING == user.getStatus() ?
                saveInvite(invitedUser).andOnSuccess(invite -> sendInviteToUnregistered(invite, project)) :
                sendEmailToRegistered(invitedUser, project);
    }


    private ServiceResult<MonitoringOfficerInvite> saveInvite(User invitedUser) {
        MonitoringOfficerInvite monitoringOfficerInvite = new MonitoringOfficerInvite(
                invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                CREATED);

        return serviceSuccess(monitoringOfficerInviteRepository.save(monitoringOfficerInvite));
    }

    private ServiceResult<Void> sendEmailToRegistered(User user, Project project) {

        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("monitoringOfficer", user);
        globalArgs.put("project", project);
        globalArgs.put("competition", project.getApplication().getCompetition());

        return sendNotification(globalArgs,
                                MONITORING_OFFICER_NEW_PROJECT_NOTIFICATION,
                                new UserNotificationTarget(user.getName(), user.getEmail()));
    }

    private ServiceResult<Void> sendInviteToUnregistered(MonitoringOfficerInvite invite,
                                                         Project project) {

        Map<String, Object> globalArgs = new HashMap<>();
        globalArgs.put("monitoringOfficerInvite", invite);
        globalArgs.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, invite));
        globalArgs.put("project", project);
        globalArgs.put("competition", project.getApplication().getCompetition());

        return sendNotification(globalArgs,
                                MONITORING_OFFICER_REGISTRATION_INVITE,
                                new UserNotificationTarget(invite.getName(), invite.getEmail()))
                .andOnSuccess(() -> monitoringOfficerInviteRepository.save(
                        invite.sendOrResend(loggedInUserSupplier.get(), ZonedDateTime.now()))
                );
    }

    private ServiceResult<Void> sendNotification(Map<String, Object> args, Notifications notificationType, NotificationTarget target) {

        Notification notification = new Notification(systemNotificationSource,
                                                     singletonList(target),
                                                     notificationType,
                                                     args);

        ServiceResult<Void> sendResult = notificationService.sendNotificationWithFlush(notification, EMAIL);
        return sendResult.handleSuccessOrFailure(
                failure -> serviceFailure(sendResult.getErrors()),
                success -> serviceSuccess()
        );

    }

    private static String getInviteUrl(String baseUrl, MonitoringOfficerInvite monitoringOfficerInvite) {
        return String.format("%s/%s/%s", baseUrl, monitoringOfficerInvite.getHash(), "register");
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

    @Override
    @Transactional
    public ServiceResult<User> activateUserByHash(String inviteHash,
                                                  MonitoringOfficerRegistrationResource resource) {
        return getByHash(inviteHash)
                .andOnSuccessReturn(Invite::open)
                .andOnSuccess(invite -> updateUserWithRegistrationDetails(invite, resource))
                .andOnSuccess(user -> registrationService.activatePendingUser(user, resource.getPassword()));
    }

    private ServiceResult<User> updateUserWithRegistrationDetails(Invite invite, MonitoringOfficerRegistrationResource resource) {
        User user = invite.getUser();
        user.setFirstName(resource.getFirstName());
        user.setLastName(resource.getLastName());
        user.setPhoneNumber(resource.getPhoneNumber());
        return serviceSuccess(user);
    }

    @Override
    public ServiceResult<Boolean> checkUserExistsForInvite(String hash) {
        return super.checkUserExistsForInvite(hash);
    }

    @Override
    @Transactional
    public ServiceResult<Void> addMonitoringOfficerRole(String hash) {
        return getByHash(hash).andOnSuccess(this::applyMonitoringOfficerRoleFromInvite);
    }

    @Override
    protected Class<MonitoringOfficerInvite> getInviteClass() {
        return MonitoringOfficerInvite.class;
    }

    @Override
    protected InviteRepository<MonitoringOfficerInvite> getInviteRepository() {
        return monitoringOfficerInviteRepository;
    }

    private  ServiceResult<Void> applyMonitoringOfficerRoleFromInvite(MonitoringOfficerInvite invite) {
        invite.getUser().addRole(MONITORING_OFFICER);
        userRepository.save(invite.getUser());
        return serviceSuccess();
    }
}