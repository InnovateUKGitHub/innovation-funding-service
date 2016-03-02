package com.worth.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.*;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.util.EntityLookupCallbacks;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.USERS_DUPLICATE_EMAIL_ADDRESS;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;

/**
 * A Service that covers basic operations concerning Users
 */
@Service
public class UserServiceImpl extends BaseTransactionalService implements UserService {
    final JsonNodeFactory factory = JsonNodeFactory.instance;
    private static final CharSequence HASH_SALT = "klj12nm6nsdgfnlk12ctw476kl";
    private static final Log LOG = LogFactory.getLog(UserServiceImpl.class);
    enum Notifications {
        VERIFY_EMAIL_ADDRESS
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ServiceResult<User> getUserByToken(final String token) {
        return find(repository.findByToken(token), notFoundError(User.class, token)).
                andOnSuccess(EntityLookupCallbacks::getOnlyElementOrFail);
    }

    @Override
    public ServiceResult<User> getUserByEmailandPassword(final String email, final String password) {

        return find(repository.findByEmail(email), notFoundError(User.class, email)).
                andOnSuccess(EntityLookupCallbacks::getOnlyElementOrFail).
                andOnSuccess(user -> user.passwordEquals(password) ? serviceSuccess(user) : serviceFailure(notFoundError(User.class)));
    }

    @Override
    public ServiceResult<User> getUserById(final Long id) {
        return super.getUser(id);
    }

    @Override
    public ServiceResult<List<User>> getUserByName(final String name) {
        return find(repository.findByName(name), notFoundError(User.class, name));
    }

    @Override
    public ServiceResult<List<User>> findAll() {
        return serviceSuccess(repository.findAll());
    }

    @Override
    public ServiceResult<List<UserResource>> findByEmail(final String email) {
        List<User> users = repository.findByEmail(email);
        return serviceSuccess(users.stream().map(UserResource::new).collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<Set<User>> findAssignableUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<User> assignables = roles.stream()
                .filter(r -> r.getRole().getName().equals("leadapplicant") || r.getRole().getName().equals("collaborator"))
                .map(ProcessRole::getUser)
                .collect(toSet());

        return serviceSuccess(assignables);
    }

    @Override
    public ServiceResult<Set<User>> findRelatedUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);

        Set<User> related = roles.stream()
                .map(ProcessRole::getUser)
                .collect(toSet());

        return serviceSuccess(related);
    }

    @Override
    public ServiceResult<UserResource> createApplicantUser(final Long organisationId, UserResource userResource) {
        return this.createApplicantUser(organisationId, userResource, Optional.empty());
    }

    @Override
    public ServiceResult<UserResource> createApplicantUser(final Long organisationId, UserResource userResource, Optional<Long> competitionId) {
        LOG.debug(String.format("createApplicantUser %s for organisation: %s ", userResource.getEmail(), organisationId));
        User newUser = assembleUserFromResource(userResource);
        addOrganisationToUser(newUser, organisationId);
        addRoleToUser(newUser, UserRoleType.APPLICANT.getName());
        newUser.setStatus(UserStatus.INACTIVE);

        if (repository.findByEmail(userResource.getEmail()).isEmpty()) {
            UserResource createdUserResource = createUserWithToken(newUser, competitionId);
            return serviceSuccess(createdUserResource);
        } else {
            return serviceFailure(new Error(USERS_DUPLICATE_EMAIL_ADDRESS, userResource.getEmail()));
        }
    }

    public ServiceResult<UserResource> updateUser(UserResource userResource) {
        List<User> existingUser = repository.findByEmail(userResource.getEmail());
        if (existingUser == null || existingUser.size() <= 0) {
            LOG.error("User with email " + userResource.getEmail() + " doesn't exist!");
            return serviceFailure(notFoundError(User.class, userResource.getEmail()));
        }
        User newUser = updateExistingUserFromResource(existingUser.get(0), userResource);
        UserResource updatedUser = new UserResource(saveUser(newUser));
        return serviceSuccess(updatedUser);
    }

    private UserResource createUserWithToken(User user, Optional<Long> competitionId) {
        user = repository.save(user);
        addTokenBasedOnIdToUser(user);
        user = saveUser(user);
        sendUserVerificationEmail(user, competitionId);
        return new UserResource(user);
    }

    private ServiceResult<Notification> sendUserVerificationEmail(User user, Optional<Long> competitionId) {
        String verificationLink = getVerificationLink(user, competitionId);


        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new ExternalUserNotificationTarget(user.getName(), user.getEmail());

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("verificationLink", verificationLink);

        Notification notification = new Notification(from, singletonList(to), Notifications.VERIFY_EMAIL_ADDRESS, notificationArguments);
        ServiceResult<Notification> result = notificationService.sendNotification(notification, EMAIL);
        return result;
    }

    private String getVerificationLink(User user, Optional<Long> competitionId) {
        String hash = generateAndSaveVerificationHash(user, competitionId);
        return String.format("%s/verify-email/%s", webBaseUrl, hash);
    }

    private String generateAndSaveVerificationHash(User user, Optional<Long> competitionId) {
        StandardPasswordEncoder encoder = new StandardPasswordEncoder(HASH_SALT);
        int random = (int) Math.ceil(Math.random() * 1000); // random number from 1 to 1000
        String hash = String.format("%s==%s==%s", user.getId(), user.getEmail(), random);
        hash = encoder.encode(hash);


        ObjectNode extraInfo = factory.objectNode();
        if(competitionId.isPresent()){
            extraInfo.put("competitionId", competitionId.get());
        }
        Token token = new Token(Token.Type.VERIFY_EMAIL_ADDRESS, User.class.getName(), user.getId(), hash, extraInfo);
        tokenRepository.save(token);
        return hash;
    }

    private User updateExistingUserFromResource(User existingUser, UserResource updatedUserResource) {
        existingUser.setPhoneNumber(updatedUserResource.getPhoneNumber());
        existingUser.setTitle(updatedUserResource.getTitle());
        existingUser.setLastName(updatedUserResource.getLastName());
        existingUser.setFirstName(updatedUserResource.getFirstName());
        return existingUser;
    }

    private User saveUser(User user) {
        return repository.save(user);
    }

    private void addRoleToUser(User user, String roleName) {
        List<Role> userRoles = roleRepository.findByName(roleName);
        user.setRoles(userRoles);
    }

    private void addOrganisationToUser(User user, Long organisationId) {
        Organisation userOrganisation = organisationRepository.findOne(organisationId);
        List<Organisation> userOrganisationList = new ArrayList<>();
        userOrganisationList.add(userOrganisation);
        user.setOrganisations(userOrganisationList);
    }

    private User assembleUserFromResource(UserResource userResource) {
        User newUser = new User();
        newUser.setFirstName(userResource.getFirstName());
        newUser.setLastName(userResource.getLastName());
        newUser.setPassword(userResource.getPassword());
        newUser.setEmail(userResource.getEmail());
        newUser.setTitle(userResource.getTitle());
        newUser.setPhoneNumber(userResource.getPhoneNumber());

        String fullName = concatenateFullName(userResource.getFirstName(), userResource.getLastName());
        newUser.setName(fullName);

        return newUser;
    }

    private String concatenateFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    private void addTokenBasedOnIdToUser(User user) {
        String userToken = user.getId() + "abc123";
        user.setToken(userToken);
    }
}
