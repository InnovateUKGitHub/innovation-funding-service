package org.innovateuk.ifs.competitionsetup.transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionFinance;
import org.innovateuk.ifs.competition.domain.CompetitionFinanceInvite;
import org.innovateuk.ifs.competition.mapper.CompetitionFinanceRepository;
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
    private CompetitionFinanceRepository competitionFinanceRepository;

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
        COMPETITION_FINANCE_INVITE,
        ADD_COMPETITION_FINANCE
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
        List<CompetitionFinance> competitionFinanceUsers = competitionFinanceRepository.findCompetitionFinance(competitionId);
        List<UserResource> users = simpleMap(competitionFinanceUsers, user -> userMapper.mapToResource(user.getUser()));
        return serviceSuccess(users);
    }

    @Override
    public ServiceResult<CompetitionFinanceInviteResource> getInviteByHash(String hash) {
        CompetitionFinanceInvite competitionFinanceInvite = competitionFinanceInviteRepository.getByHash(hash);
        return serviceSuccess(competitionFinanceInviteMapper.mapToResource(competitionFinanceInvite));
    }

    @Override
    @Transactional
    public ServiceResult<Void> addFinanceUser(long competitionId, long userId) {
        return getCompetition(competitionId)
                .andOnSuccessReturnVoid(competition ->
                        find(userRepository.findById(userId),
                                notFoundError(User.class, userId))
                                .andOnSuccess(compFinance -> {
                                    CompetitionFinance competitionFinance = competitionFinanceRepository.save(new CompetitionFinance(competition, compFinance));
                                    return sendAddCompFinanceNotification(competitionFinance, competition);
                                })
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeFinanceUser(long competitionId, long userId) {
        competitionFinanceRepository.deleteCompetitionFinance(competitionId, userId);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<UserResource>> findPendingFinanceUseInvites(long competitionId) {
        List<CompetitionFinanceInvite> pendingCompFinanceInvites = competitionFinanceInviteRepository.findByCompetitionIdAndStatus(competitionId, SENT);

        List<UserResource> pendingCompFinanceInviteUsers = simpleMap(pendingCompFinanceInvites,
                pendingCompFinanceInvite -> convert(pendingCompFinanceInvite));

        return serviceSuccess(pendingCompFinanceInviteUsers);
    }

    private UserResource convert(CompetitionFinanceInvite competitionFinanceInvite) {
        UserResource userResource = new UserResource();
        userResource.setFirstName(competitionFinanceInvite.getName());
        userResource.setEmail(competitionFinanceInvite.getEmail());
        return userResource;
    }

    private ServiceResult<Void> validateInvite(UserResource invitedUser) {

        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName())) {
            return serviceFailure(COMPETITION_FINANCE_INVITE_INVALID);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateUserInviteNotPending(long competitionId, UserResource invitedUser) {
        boolean foundPendingInvite = competitionFinanceInviteRepository.existsByCompetitionIdAndStatusAndEmail(competitionId, SENT, invitedUser.getEmail());
        return foundPendingInvite ? serviceFailure(COMPETITION_FINANCE_INVITE_TARGET_USER_ALREADY_INVITED) : serviceSuccess();
    }

    private ServiceResult<Void> validateUserNotAlreadyCompFinanceOnCompetition(long competitionId, String email) {
        boolean isUserCompFinanceOnCompetition = competitionFinanceRepository.existsByCompetitionIdAndCompetitionFinanceEmail(competitionId, email);
        return isUserCompFinanceOnCompetition ? serviceFailure(COMPETITION_FINANCE_HAS_ACCEPTED_INVITE) : serviceSuccess();
    }

    private ServiceResult<Void> addOrInviteUser(Competition competition, UserResource invitedUser) {
        Optional<User> user = userRepository.findByEmail(invitedUser.getEmail());

        if (user.isPresent()) {
            if (!user.get().hasRole(Role.COMPETITION_FINANCE)) {
                addCompetitionFinanceRoleToUser(user.get());
            }
            return addFinanceUser(competition.getId(), user.get().getId());
        } else {
            return saveInvite(invitedUser, competition)
                    .andOnSuccess(competitionFinanceInvite -> sendCompFinanceInviteNotification(competitionFinanceInvite, competition));
        }
    }

    private ServiceResult<Void> sendCompFinanceInviteNotification(CompetitionFinanceInvite competitionFinanceInvite, Competition competition) {

        Map<String, Object> globalArgs = createGlobalArgsForCompetitionFinanceInvite(competitionFinanceInvite, competition);

        Notification notification = new Notification(systemNotificationSource,
                singletonList(createCompetitionFinanceInviteNotificationTarget(competitionFinanceInvite)),
                Notifications.COMPETITION_FINANCE_INVITE, globalArgs);

        ServiceResult<Void> compFinanceInviteEmailSendResult = notificationService.sendNotificationWithFlush(notification, EMAIL);

        compFinanceInviteEmailSendResult.handleSuccessOrFailure(
                failure -> handleInviteError(competitionFinanceInvite, failure),
                success -> handleInviteSuccess(competitionFinanceInvite)
        );

        return compFinanceInviteEmailSendResult;
    }

    private NotificationTarget createCompetitionFinanceInviteNotificationTarget(CompetitionFinanceInvite competitionFinanceInvite) {
        return new UserNotificationTarget(competitionFinanceInvite.getName(), competitionFinanceInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForCompetitionFinanceInvite(CompetitionFinanceInvite competitionFinanceInvite, Competition competition) {
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("competitionName", competition.getName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, competitionFinanceInvite));
        return globalArguments;
    }

    private String getInviteUrl(String baseUrl, CompetitionFinanceInvite competitionFinanceInvite) {
        return String.format("%s/%s/%s", baseUrl, competitionFinanceInvite.getHash(), "register");
    }

    private ServiceResult<Void> handleInviteSuccess(CompetitionFinanceInvite competitionFinanceInvite) {
        competitionFinanceInviteRepository.save(competitionFinanceInvite.sendOrResend(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return serviceSuccess();
    }


    private ServiceResult<Void> handleInviteError(CompetitionFinanceInvite i, ServiceFailure failure) {
        LOG.error(String.format("Invite failed %s, %s, %s (error count: %s)", i.getId(), i.getEmail(), i.getTarget().getName(), failure.getErrors().size()));
        List<Error> errors = failure.getErrors();
        return serviceFailure(errors);
    }

    private ServiceResult<CompetitionFinanceInvite> saveInvite(UserResource invitedUser, Competition competition) {

        CompetitionFinanceInvite competitionFinanceInvite = new CompetitionFinanceInvite(competition,
                invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                CREATED);

        CompetitionFinanceInvite savedInvite = competitionFinanceInviteRepository.save(competitionFinanceInvite);

        return serviceSuccess(savedInvite);
    }

    private void addCompetitionFinanceRoleToUser(User user) {
        user.addRole(Role.COMPETITION_FINANCE);
        userRepository.save(user);
        userService.evictUserCache(user.getUid());
    }

    private ServiceResult<Void> sendAddCompFinanceNotification(CompetitionFinance competitionFinance, Competition competition) {

        Map<String, Object> globalArgs = createGlobalArgsForAddCompFinance(competition);

        Notification notification = new Notification(systemNotificationSource,
                singletonList(createAddCompFinanceNotificationTarget(competitionFinance)),
                Notifications.ADD_COMPETITION_FINANCE, globalArgs);

        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    private NotificationTarget createAddCompFinanceNotificationTarget(CompetitionFinance competitionFinance) {
        return new UserNotificationTarget(competitionFinance.getUser().getName(), competitionFinance.getUser().getEmail());
    }

    private Map<String, Object> createGlobalArgsForAddCompFinance(Competition competition) {
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("competitionName", competition.getName());
        globalArguments.put("dashboardUrl", webBaseUrl + "/management/dashboard/live");
        return globalArguments;
    }
}
