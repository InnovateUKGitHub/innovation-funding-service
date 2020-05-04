package org.innovateuk.ifs.competitionsetup.transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.ExternalFinance;
import org.innovateuk.ifs.competition.domain.ExternalFinanceInvite;
import org.innovateuk.ifs.competition.mapper.ExternalFinanceRepository;
import org.innovateuk.ifs.competition.repository.CompetitionFinanceInviteRepository;
import org.innovateuk.ifs.invite.mapper.CompetitionFinanceInviteMapper;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
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
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionSetupFinanceUserServiceImpl extends BaseTransactionalService implements CompetitionSetupFinanceUserService {

    private static final Log LOG = LogFactory.getLog(CompetitionSetupFinanceUserServiceImpl.class);

    @Autowired
    private ExternalFinanceRepository externalFinanceRepository;

    @Autowired
    private CompetitionFinanceInviteRepository competitionFinanceInviteRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private CompetitionFinanceInviteMapper competitionFinanceInviteMapper;

    @Autowired
    private UserMapper userMapper;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    private static final String WEB_CONTEXT = "/management/finance-user";

    enum Notifications {
        EXTERNAL_FINANCE_INVITE,
        ADD_EXTERNAL_FINANCE
    }

    @Override
    @Transactional
    public ServiceResult<Void> inviteFinanceUser(UserResource invitedUser, long competitionId) {
        return validateInvite(invitedUser)
                .andOnSuccess(() -> validateUserInviteNotPending(competitionId, invitedUser))
                .andOnSuccess(() -> validateUserNotAlreadyCompFinanceOnCompetition(competitionId, invitedUser.getEmail()))
                .andOnSuccess(() -> getCompetition(competitionId))
                .andOnSuccess(competition -> addOrInviteUser(competition, invitedUser)
                );
    }

    @Override
    public ServiceResult<List<UserResource>> findFinanceUser(long competitionId) {
        List<ExternalFinance> externalFinanceUsers = externalFinanceRepository.findCompetitionFinance(competitionId);
        List<UserResource> users = simpleMap(externalFinanceUsers, user -> userMapper.mapToResource(user.getUser()));
        return serviceSuccess(users);
    }

    @Override
    public ServiceResult<CompetitionFinanceInviteResource> getInviteByHash(String hash) {
        ExternalFinanceInvite externalFinanceInvite = competitionFinanceInviteRepository.getByHash(hash);
        return serviceSuccess(competitionFinanceInviteMapper.mapToResource(externalFinanceInvite));
    }

    @Override
    @Transactional
    public ServiceResult<Void> addFinanceUser(long competitionId, long userId) {
        return getCompetition(competitionId)
                .andOnSuccessReturnVoid(competition ->
                        find(userRepository.findById(userId),
                                notFoundError(User.class, userId))
                                .andOnSuccess(compFinance -> {
                                    ExternalFinance externalFinance = externalFinanceRepository.save(new ExternalFinance(competition, compFinance));
                                    return sendAddCompFinanceNotification(externalFinance, competition);
                                })
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeFinanceUser(long competitionId, long userId) {
        externalFinanceRepository.deleteCompetitionFinance(competitionId, userId);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<UserResource>> findPendingFinanceUseInvites(long competitionId) {
        List<ExternalFinanceInvite> pendingCompFinanceInvites = competitionFinanceInviteRepository.findByCompetitionIdAndStatus(competitionId, SENT);

        List<UserResource> pendingCompFinanceInviteUsers = simpleMap(pendingCompFinanceInvites,
                pendingCompFinanceInvite -> convert(pendingCompFinanceInvite));

        return serviceSuccess(pendingCompFinanceInviteUsers);
    }

    private UserResource convert(ExternalFinanceInvite externalFinanceInvite) {
        UserResource userResource = new UserResource();
        userResource.setFirstName(externalFinanceInvite.getName());
        userResource.setEmail(externalFinanceInvite.getEmail());
        return userResource;
    }

    private ServiceResult<Void> validateInvite(UserResource invitedUser) {

        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName())) {
            return serviceFailure(EXTERNAL_FINANCE_INVITE_INVALID);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateUserInviteNotPending(long competitionId, UserResource invitedUser) {
        boolean foundPendingInvite = competitionFinanceInviteRepository.existsByCompetitionIdAndStatusAndEmail(competitionId, SENT, invitedUser.getEmail());
        return foundPendingInvite ? serviceFailure(EXTERNAL_FINANCE_INVITE_TARGET_USER_ALREADY_INVITED) : serviceSuccess();
    }

    private ServiceResult<Void> validateUserNotAlreadyCompFinanceOnCompetition(long competitionId, String email) {
        boolean isUserCompFinanceOnCompetition = externalFinanceRepository.existsByCompetitionIdAndCompetitionFinanceEmail(competitionId, email);
        return isUserCompFinanceOnCompetition ? serviceFailure(EXTERNAL_FINANCE_HAS_ACCEPTED_INVITE) : serviceSuccess();
    }

    private ServiceResult<Void> addOrInviteUser(Competition competition, UserResource invitedUser) {
        Optional<User> user = userRepository.findByEmail(invitedUser.getEmail());

        if (user.isPresent()) {
            if (!user.get().hasRole(Role.EXTERNAL_FINANCE)) {
                addCompetitionFinanceRoleToUser(user.get());
            }
            return addFinanceUser(competition.getId(), user.get().getId());
        } else {
            return saveInvite(invitedUser, competition)
                    .andOnSuccess(competitionFinanceInvite -> sendCompFinanceInviteNotification(competitionFinanceInvite, competition));
        }
    }

    private ServiceResult<Void> sendCompFinanceInviteNotification(ExternalFinanceInvite externalFinanceInvite, Competition competition) {

        Map<String, Object> globalArgs = createGlobalArgsForCompetitionFinanceInvite(externalFinanceInvite, competition);

        Notification notification = new Notification(systemNotificationSource,
                singletonList(createCompetitionFinanceInviteNotificationTarget(externalFinanceInvite)),
                Notifications.EXTERNAL_FINANCE_INVITE, globalArgs);

        ServiceResult<Void> compFinanceInviteEmailSendResult = notificationService.sendNotificationWithFlush(notification, EMAIL);

        compFinanceInviteEmailSendResult.handleSuccessOrFailure(
                failure -> handleInviteError(externalFinanceInvite, failure),
                success -> handleInviteSuccess(externalFinanceInvite)
        );

        return compFinanceInviteEmailSendResult;
    }

    private NotificationTarget createCompetitionFinanceInviteNotificationTarget(ExternalFinanceInvite externalFinanceInvite) {
        return new UserNotificationTarget(externalFinanceInvite.getName(), externalFinanceInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForCompetitionFinanceInvite(ExternalFinanceInvite externalFinanceInvite, Competition competition) {
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("competitionName", competition.getName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, externalFinanceInvite));
        return globalArguments;
    }

    private String getInviteUrl(String baseUrl, ExternalFinanceInvite externalFinanceInvite) {
        return String.format("%s/%s/%s", baseUrl, externalFinanceInvite.getHash(), "register");
    }

    private ServiceResult<Void> handleInviteSuccess(ExternalFinanceInvite externalFinanceInvite) {
        competitionFinanceInviteRepository.save(externalFinanceInvite.sendOrResend(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return serviceSuccess();
    }


    private ServiceResult<Void> handleInviteError(ExternalFinanceInvite i, ServiceFailure failure) {
        LOG.error(String.format("Invite failed %s, %s, %s (error count: %s)", i.getId(), i.getEmail(), i.getTarget().getName(), failure.getErrors().size()));
        List<Error> errors = failure.getErrors();
        return serviceFailure(errors);
    }

    private ServiceResult<ExternalFinanceInvite> saveInvite(UserResource invitedUser, Competition competition) {

        ExternalFinanceInvite externalFinanceInvite = new ExternalFinanceInvite(competition,
                invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                CREATED);

        ExternalFinanceInvite savedInvite = competitionFinanceInviteRepository.save(externalFinanceInvite);

        return serviceSuccess(savedInvite);
    }

    private void addCompetitionFinanceRoleToUser(User user) {
        user.addRole(Role.EXTERNAL_FINANCE);
        userRepository.save(user);
        userService.evictUserCache(user.getUid());
    }

    private ServiceResult<Void> sendAddCompFinanceNotification(ExternalFinance externalFinance, Competition competition) {

        Map<String, Object> globalArgs = createGlobalArgsForAddCompFinance(competition);

        Notification notification = new Notification(systemNotificationSource,
                singletonList(createAddCompFinanceNotificationTarget(externalFinance)),
                Notifications.ADD_EXTERNAL_FINANCE, globalArgs);

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private NotificationTarget createAddCompFinanceNotificationTarget(ExternalFinance externalFinance) {
        return new UserNotificationTarget(externalFinance.getUser().getName(), externalFinance.getUser().getEmail());
    }

    private Map<String, Object> createGlobalArgsForAddCompFinance(Competition competition) {
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("competitionName", competition.getName());
        globalArguments.put("dashboardUrl", webBaseUrl + "/management/dashboard/live");
        return globalArguments;
    }
}
