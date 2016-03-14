package com.worth.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.*;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.domain.TokenType;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.Collections.singletonList;

/**
 * A service around Registration and general user-creation operations
 */
@Service
public class RegistrationServiceImpl extends BaseTransactionalService implements RegistrationService {


    final JsonNodeFactory factory = JsonNodeFactory.instance;
    private static final CharSequence HASH_SALT = "klj12nm6nsdgfnlk12ctw476kl";

    public enum ServiceFailures {
        UNABLE_TO_CREATE_USER
    }

    enum Notifications {
        VERIFY_EMAIL_ADDRESS
    }

    @Autowired
    private IdentityProviderService idpService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    public ServiceResult<UserResource> createApplicantUser(Long organisationId, UserResource userResource) {
        return createApplicantUser(organisationId, Optional.empty(), userResource);
    }

    @Override
    public ServiceResult<UserResource> createApplicantUser(Long organisationId, Optional<Long> competitionId, UserResource userResource) {

        User newUser = assembleUserFromResource(userResource);

        return addOrganisationToUser(newUser, organisationId).andOnSuccess(user ->
               addRoleToUser(user, UserRoleType.APPLICANT.getName())).andOnSuccess(user ->
               createUserWithUid(newUser, userResource.getPassword(), competitionId)).andOnSuccessReturn(UserResource::new);
    }

    @Override
    public ServiceResult<Void> activateUser(Long userId){
        return getUser(userId).andOnSuccessReturnVoid(u -> {
            u.setStatus(UserStatus.ACTIVE);
            userRepository.save(u);
        });
    }

    private ServiceResult<User> createUserWithUid(User user, String password, Optional<Long> competitionId) {

        ServiceResult<String> uidFromIdpResult = idpService.createUserRecordWithUid(user.getEmail(), password);

        return uidFromIdpResult.andOnSuccessReturn(uidFromIdp -> {
            user.setUid(uidFromIdp);
            user.setStatus(UserStatus.INACTIVE);
            User savedUser = userRepository.save(user);
            sendUserVerificationEmail(savedUser, competitionId);
            return savedUser;
        });
    }

    private ServiceResult<User> addRoleToUser(User user, String roleName) {

        return find(roleRepository.findByName(roleName), notFoundError(Role.class, roleName)).andOnSuccessReturn(roles -> {

            Role applicantRole = getOnlyElement(roles);

            List<Role> newRoles = user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>();

            if (!newRoles.contains(applicantRole)) {
                newRoles.add(applicantRole);
            }

            user.setRoles(newRoles);
            return user;
        });

    }

    private ServiceResult<User> addOrganisationToUser(User user, Long organisationId) {

        return find(organisation(organisationId)).andOnSuccessReturn(userOrganisation -> {

            List<Organisation> userOrganisationList = new ArrayList<>();
            userOrganisationList.add(userOrganisation);
            user.setOrganisations(userOrganisationList);
            return user;
        });
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
        return firstName+" "+lastName;
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
        return String.format("%s/registration/verify-email/%s", webBaseUrl, hash);
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
        Token token = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), user.getId(), hash, extraInfo);
        tokenRepository.save(token);
        return hash;
    }

}
