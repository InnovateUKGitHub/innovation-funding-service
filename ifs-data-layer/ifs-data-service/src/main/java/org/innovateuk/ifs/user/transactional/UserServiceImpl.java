package org.innovateuk.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.transactional.UserTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.EthnicityMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.UserStatus.INACTIVE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * A Service that covers basic operations concerning Users
 */
@Service
public class UserServiceImpl extends UserTransactionalService implements UserService {
    private final JsonNodeFactory factory = JsonNodeFactory.instance;

    public enum Notifications {
        VERIFY_EMAIL_ADDRESS,
        RESET_PASSWORD
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

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
    private EthnicityMapper ethnicityMapper;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    public ServiceResult<UserResource> findByEmail(final String email) {
        return find(userRepository.findByEmail(email), notFoundError(User.class, email)).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<UserResource> findInactiveByEmail(String email) {
        return find(userRepository.findByEmailAndStatus(email, INACTIVE), notFoundError(User.class, email, INACTIVE)).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<Set<UserResource>> findAssignableUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<UserResource> assignables = roles.stream()
                .filter(r -> "leadapplicant".equals(r.getRole().getName()) || "collaborator".equals(r.getRole().getName()))
                .map(ProcessRole::getUser)
                .map(userMapper::mapToResource)
                .collect(toSet());

        return serviceSuccess(assignables);
    }

    @Override
    public ServiceResult<Set<UserResource>> findRelatedUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);

        Set<UserResource> related = roles.stream()
                .map(ProcessRole::getUser)
                .map(userMapper::mapToResource)
                .collect(toSet());

        return serviceSuccess(related);
    }

    @Override
    public ServiceResult<Void> sendPasswordResetNotification(UserResource user) {
        if (UserStatus.ACTIVE.equals(user.getStatus())) {
            String hash = getAndSavePasswordResetToken(user);

            NotificationSource from = systemNotificationSource;
            NotificationTarget to = new ExternalUserNotificationTarget(user.getName(), user.getEmail());

            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("passwordResetLink", getPasswordResetLink(hash));

            Notification notification = new Notification(from, singletonList(to), Notifications.RESET_PASSWORD, notificationArguments);
            return notificationService.sendNotification(notification, EMAIL);
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
    public ServiceResult<Void> changePassword(String hash, String password) {
        return tokenService.getPasswordResetToken(hash).andOnSuccess(token ->
            find(user(token.getClassPk())).andOnSuccess(user -> {

                UserResource userResource = userMapper.mapToResource(user);

                return passwordPolicyValidator.validatePassword(password, userResource).andOnSuccessReturnVoid(() ->
                        identityProviderService.updateUserPassword(userResource.getUid(), password).andOnSuccess(() ->
                            tokenRepository.delete(token))
                );
            })
        );
    }

    private String getRandomHash() {
        return UUID.randomUUID().toString();
    }

    private String getPasswordResetLink(String hash) {
        return String.format("%s/login/reset-password/hash/%s", webBaseUrl, hash);
    }

    @Override
    public ServiceResult<Void> updateDetails(UserResource userResource) {
        return find(userRepository.findByEmail(userResource.getEmail()), notFoundError(User.class, userResource.getEmail()))
                .andOnSuccess( user -> updateUser(user, userResource));
    }

    private ServiceResult<Void> updateUser(User existingUser, UserResource updatedUserResource) {
        existingUser.setPhoneNumber(updatedUserResource.getPhoneNumber());
        existingUser.setTitle(updatedUserResource.getTitle());
        existingUser.setLastName(updatedUserResource.getLastName());
        existingUser.setFirstName(updatedUserResource.getFirstName());
        existingUser.setGender(updatedUserResource.getGender());
        existingUser.setDisability(updatedUserResource.getDisability());
        existingUser.setEthnicity(ethnicityMapper.mapIdToDomain(updatedUserResource.getEthnicity()));
        existingUser.setAllowMarketingEmails(updatedUserResource.getAllowMarketingEmails());
        return serviceSuccess(userRepository.save(existingUser)).andOnSuccessReturnVoid();
    }

    private boolean userNotYetVerified(UserResource user) {
        return UserStatus.INACTIVE.equals(user.getStatus())
                && tokenRepository.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getCanonicalName(), user.getId()).isPresent();
    }

    private boolean userIsExternalNotOnlyAssessor(UserResource user) {
        return user
                .getRoles()
                .stream()
                .anyMatch(r -> UserRoleType.COLLABORATOR.getName().equals(r.getName()) ||
                             UserRoleType.APPLICANT.getName().equals(r.getName()) ||
                             UserRoleType.FINANCE_CONTACT.getName().equals(r.getName()) ||
                             UserRoleType.LEADAPPLICANT.getName().equals(r.getName()) ||
                             UserRoleType.PARTNER.getName().equals(r.getName()) ||
                             UserRoleType.PROJECT_MANAGER.getName().equals(r.getName()));
    }

    @Override
    public ServiceResult<UserPageResource> findActiveByProcessRoles(Set<UserRoleType> roleTypes, Pageable pageable) {
        Page<User> pagedResult = userRepository.findDistinctByStatusAndRolesNameIn(UserStatus.ACTIVE, roleTypes.stream().map(UserRoleType::getName).collect(Collectors.toSet()), pageable);
        List<UserResource> userResources = simpleMap(pagedResult.getContent(), user -> userMapper.mapToResource(user));
        return serviceSuccess(new UserPageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), sortByName(userResources), pagedResult.getNumber(), pagedResult.getSize()));
    }

    @Override
    public ServiceResult<UserPageResource> findInactiveByProcessRoles(Set<UserRoleType> roleTypes, Pageable pageable) {
        Page<User> pagedResult = userRepository.findDistinctByStatusAndRolesNameIn(UserStatus.INACTIVE, roleTypes.stream().map(UserRoleType::getName).collect(Collectors.toSet()), pageable);
        List<UserResource> userResources = simpleMap(pagedResult.getContent(), user -> userMapper.mapToResource(user));
        return serviceSuccess(new UserPageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), sortByName(userResources), pagedResult.getNumber(), pagedResult.getSize()));
    }

    @Override
    public ServiceResult<List<UserOrganisationResource>> findAllByProcessRoles(Set<UserRoleType> roleTypes) {
        /*List<User> users = userRepository.findByRolesNameInOrderByEmailAsc(roleTypes.stream().map(UserRoleType::getName).collect(Collectors.toSet()));
        List<UserOrganisationResource> userResources = simpleMap(users, user -> {
            List<Organisation> organisations = organisationRepository.findByUsersId(user.getId());
            return new UserOrganisationResource(user.getName(), organisations.get(0).getName(), organisations.get(0).getId(), user.getEmail(), user.getStatus().getDisplayName());
        });
        return serviceSuccess(userResources);*/

        return serviceSuccess(userRepository.findAllExternalUserOrganisations());

    }

    private List<UserResource> sortByName(List<UserResource> userResources) {
        return userResources.stream().sorted(Comparator.comparing(userResource -> userResource.getName().toUpperCase())).collect(Collectors.toList());
    }
}
