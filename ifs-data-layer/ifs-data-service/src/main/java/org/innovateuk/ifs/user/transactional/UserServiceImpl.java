package org.innovateuk.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.authentication.validator.PasswordPolicyValidator;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.invite.repository.UserInviteRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.invite.domain.ProjectPartnerInvite;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.transactional.UserTransactionalService;
import org.innovateuk.ifs.user.cache.UserCacheEvict;
import org.innovateuk.ifs.user.cache.UserUpdate;
import org.innovateuk.ifs.user.command.GrantRoleCommand;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.RoleProfileStatusMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisation;
import org.innovateuk.ifs.userorganisation.mapper.UserOrganisationMapper;
import org.innovateuk.ifs.userorganisation.repository.UserOrganisationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.intersection;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.innovateuk.ifs.user.resource.UserStatus.INACTIVE;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * A Service that covers basic operations concerning Users
 */
@Service
public class UserServiceImpl extends UserTransactionalService implements UserService {

    private final static Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    public enum Notifications {
        VERIFY_EMAIL_ADDRESS,
        RESET_PASSWORD,
        EMAIL_CHANGE_OLD,
        EMAIL_CHANGE_NEW
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Value("${ifs.system.external.user.email.domain}")
    private String externalUserEmailDomain;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private TermsAndConditionsService termsAndConditionsService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IdentityProviderService identityProviderService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordPolicyValidator passwordPolicyValidator;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserOrganisationRepository userOrganisationRepository;

    @Autowired
    private UserOrganisationMapper userOrganisationMapper;

    @Autowired
    private RoleProfileStatusMapper roleProfileStatusMapper;

    @Autowired
    private UserInviteRepository userInviteRepository;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Autowired
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    private Supplier<String> randomHashSupplier = () -> UUID.randomUUID().toString();

    @Override
    public ServiceResult<UserResource> findByEmail(final String email) {
        return find(userRepository.findByEmail(email), notFoundError(User.class, email)).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<UserResource> findInactiveByEmail(String email) {
        return find(userRepository.findByEmailAndStatus(email, INACTIVE), notFoundError(User.class, email, INACTIVE)).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<Set<UserResource>> findAssignableUsers(final long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<UserResource> assignables = roles.stream()
                .filter(r -> "leadapplicant".equals(r.getRole().getName()) || "collaborator".equals(r.getRole().getName()))
                .map(ProcessRole::getUser)
                .map(userMapper::mapToResource)
                .collect(toSet());

        return serviceSuccess(assignables);
    }

    @Override
    public ServiceResult<Set<UserResource>> findRelatedUsers(final long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);

        Set<UserResource> related = roles.stream()
                .map(ProcessRole::getUser)
                .map(userMapper::mapToResource)
                .collect(toSet());

        return serviceSuccess(related);
    }

    @Override
    @Transactional
    public ServiceResult<Void> sendPasswordResetNotification(UserResource user) {
        if (UserStatus.ACTIVE.equals(user.getStatus())) {
            String hash = getAndSavePasswordResetToken(user);

            NotificationSource from = systemNotificationSource;
            NotificationTarget to = new UserNotificationTarget(user.getName(), user.getEmail());

            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("passwordResetLink", getPasswordResetLink(hash));

            Notification notification = new Notification(from, singletonList(to), Notifications.RESET_PASSWORD, notificationArguments);
            return notificationService.sendNotificationWithFlush(notification, EMAIL);
        } else if (userIsExternalNotOnlyAssessor(user) &&
                userNotYetVerified(user)) {
            return registrationService.resendUserVerificationEmail(user);
        } else {
            return serviceFailure(notFoundError(UserResource.class, user.getEmail(), UserStatus.ACTIVE));
        }
    }

    private String getAndSavePasswordResetToken(UserResource user) {
        String hash = getRandomHash();
        Token token = new Token(TokenType.RESET_PASSWORD, User.class.getName(), user.getId(), hash, now(), factory.objectNode());
        tokenRepository.save(token);
        return hash;
    }

    @Override
    @Transactional
    public ServiceResult<Void> changePassword(String hash, String password) {
        return tokenService.getPasswordResetToken(hash).andOnSuccess(token ->
            find(user(token.getClassPk())).andOnSuccess(user -> {

                UserResource userResource = userMapper.mapToResource(user);

                return passwordPolicyValidator.validatePassword(password, userResource).andOnSuccess(() ->
                        identityProviderService.updateUserPassword(userResource.getUid(), password).andOnSuccessReturnVoid(() ->
                            tokenRepository.delete(token))
                );
            })
        );
    }

    private ServiceResult<Void> notifyEmailChange(String oldEmail, String newEmail, User user) {
        NotificationSource from = systemNotificationSource;
        NotificationTarget oldEmailTarget = new UserNotificationTarget(user.getName(), oldEmail);
        NotificationTarget newEmailTarget = new UserNotificationTarget(user.getName(), newEmail);

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("newEmail", newEmail);

        Notification oldNotification = new Notification(from, singletonList(oldEmailTarget), Notifications.EMAIL_CHANGE_OLD, notificationArguments);
        Notification newNotification = new Notification(from, singletonList(newEmailTarget), Notifications.EMAIL_CHANGE_NEW, notificationArguments);
        ServiceResult<Void> oldResult = notificationService.sendNotificationWithFlush(oldNotification, EMAIL);
        ServiceResult<Void> newResult = notificationService.sendNotificationWithFlush(newNotification, EMAIL);
        return aggregate(oldResult, newResult).andOnSuccessReturnVoid();
    }

    private void logEmailChange(String oldEmail, String newEmail){
        LOG.info("Updated email from  " + oldEmail + " to " + newEmail);
    }

    private String getRandomHash() {
        return randomHashSupplier.get();
    }

    private String getPasswordResetLink(String hash) {
        return String.format("%s/login/reset-password/hash/%s", webBaseUrl, hash);
    }

    @Override
    @Transactional
    @UserUpdate
    public ServiceResult<UserResource> updateDetails(UserResource userResource) {
        return find(userRepository.findByEmail(userResource.getEmail()), notFoundError(User.class, userResource.getEmail()))
                .andOnSuccess(user -> updateUser(user, userResource))
                .andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    @UserCacheEvict
    public ServiceResult<Void> evictUserCache(String uid) {
        return serviceSuccess();
    }

    private ServiceResult<User> updateUser(User existingUser, UserResource updatedUserResource) {
        existingUser.setPhoneNumber(updatedUserResource.getPhoneNumber());
        existingUser.setTitle(updatedUserResource.getTitle());
        existingUser.setLastName(updatedUserResource.getLastName());
        existingUser.setFirstName(updatedUserResource.getFirstName());
        existingUser.setAllowMarketingEmails(updatedUserResource.getAllowMarketingEmails());
        return serviceSuccess(userRepository.save(existingUser));
    }

    private boolean userNotYetVerified(UserResource user) {
        return UserStatus.INACTIVE.equals(user.getStatus())
                && tokenRepository.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getCanonicalName(), user.getId()).isPresent();
    }

    private boolean userIsExternalNotOnlyAssessor(UserResource user) {
        return user
                .getRoles()
                .stream()
                .anyMatch(r -> COLLABORATOR == r ||
                             APPLICANT == r ||
                             FINANCE_CONTACT == r ||
                             LEADAPPLICANT == r ||
                             PARTNER == r ||
                             PROJECT_MANAGER == r);
    }

    @Override
    public ServiceResult<ManageUserPageResource> findActive(String filter, Pageable pageable) {
        Page<User> pagedResult = userRepository.findByEmailContainingAndStatus(filter, UserStatus.ACTIVE, pageable);
        return serviceSuccess(new ManageUserPageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), mapUsersToManageUserResource(pagedResult.getContent()), pagedResult.getNumber(), pagedResult.getSize()));
    }

    @Override
    public ServiceResult<ManageUserPageResource> findActiveExternal(String filter, Pageable pageable) {
        return findExternalUserPageResource(filter, pageable, ACTIVE);
    }

    @Override
    public ServiceResult<ManageUserPageResource> findInactive(String filter, Pageable pageable) {
        Page<User> pagedResult = userRepository.findByEmailContainingAndStatus(filter, UserStatus.INACTIVE, pageable);
        return serviceSuccess(new ManageUserPageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), mapUsersToManageUserResource(pagedResult.getContent()), pagedResult.getNumber(), pagedResult.getSize()));
    }

    @Override
    public ServiceResult<ManageUserPageResource> findInactiveExternal(String filter, Pageable pageable) {
        return findExternalUserPageResource(filter, pageable, INACTIVE);
    }

    private ServiceResult<ManageUserPageResource> findExternalUserPageResource(String filter, Pageable pageable, UserStatus userStatus) {
        Page<User> pagedResult = userRepository.findByEmailContainingAndStatusAndRolesIn(
                filter,
                userStatus,
                externalRoles()
                        .stream()
                        .map(r -> Role.getByName(r.getName()))
                        .collect(toSet()),
                pageable
        );
        List<ManageUserResource> userResources = pagedResult.getContent()
                .stream()
                .map(this::mapUserToManageUserResource)
                .collect(toList());

        return serviceSuccess(new ManageUserPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                userResources,
                pagedResult.getNumber(),
                pagedResult.getSize())
        );
    }

    private List<ManageUserResource> mapUsersToManageUserResource(List<User> users) {
        return users.stream()
                .map(this::mapUserToManageUserResource)
                .collect(toList());
    }

    private ManageUserResource mapUserToManageUserResource(User user) {
        return new ManageUserResource(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles().stream().collect(Collectors.toList()),
                user.getCreatedOn(),
                user.getCreatedBy().getName(),
                mapRoleProfileStatusesToResource(user.getRoleProfileStatuses()));
    }

    private Set<RoleProfileStatusResource> mapRoleProfileStatusesToResource(Set<RoleProfileStatus> roleProfileStatuses) {
        return roleProfileStatuses.stream()
                .map(roleProfileStatusMapper::mapToResource)
                .collect(toSet());
    }

    @Override
    public ServiceResult<List<UserOrganisationResource>> findByProcessRolesAndSearchCriteria(Set<Role> roleTypes, String searchString, SearchCategory searchCategory) {

        return validateSearchString(searchString).andOnSuccess(() -> {
            String searchStringExpr = "%" + StringUtils.trim(searchString) + "%";
            Set<UserOrganisation> userOrganisations;
            switch (searchCategory) {
                case NAME:
                    userOrganisations = userOrganisationRepository.findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(searchStringExpr, searchStringExpr, roleTypes);
                    break;

                case ORGANISATION_NAME:
                    userOrganisations = userOrganisationRepository.findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(searchStringExpr, roleTypes);
                    break;

                case EMAIL:
                default:
                    userOrganisations = userOrganisationRepository.findByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(searchStringExpr, roleTypes);
                    break;
            }
            return serviceSuccess(simpleMap(userOrganisations, userOrganisationMapper::mapToResource));
        });
    }

    @Override
    @Transactional
    @UserUpdate
    public ServiceResult<UserResource> agreeNewTermsAndConditions(long userId) {
        return termsAndConditionsService.getLatestSiteTermsAndConditions().andOnSuccess(latest ->
                getUser(userId).andOnSuccessReturn(user -> {
                    user.getTermsAndConditionsIds().add(latest.getId());
                    return userRepository.save(user);
                }))
                .andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    @Transactional
    @UserUpdate
    public ServiceResult<UserResource> grantRole(GrantRoleCommand grantRoleCommand) {
        return getUser(grantRoleCommand.getUserId())
                .andOnSuccess(user -> validateRoleDoesNotExist(user, grantRoleCommand.getTargetRole()))
                .andOnSuccess(user -> validateEmail(user, grantRoleCommand.getTargetRole()))
                .andOnSuccessReturn(user -> {
                    user.getRoles().add(grantRoleCommand.getTargetRole());
                    return user;
                }).andOnSuccessReturn(userMapper::mapToResource);
    }

    private ServiceResult<User> validateEmail(User user, Role role) {

        if (role.isKTA()) {
            String domain = StringUtils.substringAfter(user.getEmail(), "@");

            if (!externalUserEmailDomain.equalsIgnoreCase(domain)) {
                return serviceFailure(USER_ADD_ROLE_INVALID_EMAIL);
            }
        }

        return serviceSuccess(user);
    }

    private ServiceResult<User> validateRoleDoesNotExist(User user, Role role) {
        if(user.getRoles().contains(role)) {
            return serviceFailure(USER_ADD_ROLE_ROLE_ALREADY_EXISTS);
        }
        return serviceSuccess(user);
    }

    @Override
    @Transactional
    @UserUpdate
    public ServiceResult<UserResource> updateEmail(long userId, String email) {
        return find(userRepository.findById(userId), notFoundError(User.class, userId))
                .andOnSuccess(user -> validateEmailDoesntAlreadyExist(user, email))
                .andOnSuccess(user -> validateEmailForApplicationTeam(user, email))
                .andOnSuccess(user -> validateEmailForProjectTeam(user, email))
                .andOnSuccess(user -> updateUserEmail(user, email))
                .andOnSuccessReturn(userMapper::mapToResource);
    }

    private ServiceResult<User> validateEmailForApplicationTeam(User user, String email) {

        List<Long> userApplicationIds = processRoleRepository.findByUser(user).stream()
                .map(ProcessRole::getApplicationId)
                .collect(toList());

        List<Long> newEmailInviteApplicationIds = applicationInviteRepository.findByEmail(email).stream()
                .filter(invite -> !OPENED.equals(invite.getStatus()))
                .map(Invite::getTarget)
                .map(Application::getId)
                .collect(toList());

        List<Long> invalidApplications = new ArrayList(intersection(newEmailInviteApplicationIds, userApplicationIds));

        if (!invalidApplications.isEmpty()) {
            return serviceFailure(new Error(USER_EMAIL_UPDATE_EMAIL_EXISTS_ON_APPLICATION, invalidApplications.get(0)) );
        }

        return serviceSuccess(user);
    }

    private ServiceResult<User> validateEmailForProjectTeam(User user, String email) {

        List<Long> userProjectIds = projectUserRepository.findByUserId(user.getId()).stream()
                .map(ProjectUser::getProject)
                .map(Project::getId)
                .collect(toList());

        List<ProjectUserInvite> projectUserInvites = projectUserInviteRepository.findByEmail(email);
        List<ProjectPartnerInvite> projectPartnerInvites = projectPartnerInviteRepository.findByEmail(email);

        List<Long> newEmailInviteProjectIds = projectUserInvites.stream()
                .filter(invite -> !OPENED.equals(invite.getStatus()))
                .map(Invite::getTarget)
                .map(Project::getId)
                .collect(toList());

        List<Long> newEmailInvitePartnerProjectIds = projectPartnerInvites.stream()
                .filter(invite -> !OPENED.equals(invite.getStatus()))
                .map(Invite::getTarget)
                .map(Project::getId)
                .collect(toList());

        List<Long> invalidProjects = new ArrayList(intersection(userProjectIds, combineLists(newEmailInvitePartnerProjectIds, newEmailInviteProjectIds)));

        if (!invalidProjects.isEmpty()) {

          Long applicationId = projectRepository.findById(invalidProjects.get(0)).get().getApplication().getId();

            return serviceFailure(new Error(USER_EMAIL_UPDATE_EMAIL_EXISTS_ON_APPLICATION, applicationId) );
        }

        return serviceSuccess(user);
    }

    private ServiceResult<User> validateEmailDoesntAlreadyExist(User user, String email) {
        Optional<User> existingUserWithSameEmail = userRepository.findByEmail(email);
        if (existingUserWithSameEmail.isPresent()) {
            return serviceFailure(USER_EMAIL_UPDATE_EMAIL_ALREADY_EXISTS);
        }
        return serviceSuccess(user);
    }

    private ServiceResult<Void> validateSearchString(String searchString) {

        searchString = StringUtils.trim(searchString);

        if (StringUtils.isEmpty(searchString) || StringUtils.length(searchString) < 3) {
            return serviceFailure(new Error(USER_SEARCH_INVALID_INPUT_LENGTH, singletonList(3)) );
        } else {
            return serviceSuccess();
        }
    }

    private ServiceResult<User> updateUserEmail(User existingUser, String emailToUpdate) {
        userInviteRepository.findByEmail(existingUser.getEmail()).forEach(invite -> invite.setEmail(emailToUpdate));
        String oldEmail = existingUser.getEmail();
        existingUser.setEmail(emailToUpdate);
        User user = userRepository.save(existingUser);
        return identityProviderService.updateUserEmail(existingUser.getUid(), emailToUpdate)
                .andOnSuccessReturnVoid(() -> logEmailChange(existingUser.getEmail(), emailToUpdate))
                .andOnSuccess(() -> notifyEmailChange(oldEmail, emailToUpdate, user))
                .andOnSuccessReturn(() -> user);
    }
}