package com.worth.ifs.user.transactional;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.*;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.token.resource.TokenType;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.repository.CompAdminEmailRepository;
import com.worth.ifs.user.repository.ProjectFinanceEmailRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.resource.UserRoleType.*;
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

    private StandardPasswordEncoder encoder = new StandardPasswordEncoder(HASH_SALT);

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
    private CompAdminEmailRepository compAdminEmailRepository;
    
    @Autowired
    private ProjectFinanceEmailRepository projectFinanceEmailRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordPolicyValidator passwordPolicyValidator;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    public ServiceResult<UserResource> createApplicantUser(Long organisationId, UserResource userResource) {
        return createApplicantUser(organisationId, Optional.empty(), userResource);
    }

    private boolean isUserCompAdmin(final String email) {
        if(StringUtils.hasText(email)) {
            CompAdminEmail existingUserSearch = compAdminEmailRepository.findOneByEmail(email);
            if(existingUserSearch != null) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isUserProjectFinance(String email) {
    	if(StringUtils.hasText(email)) {
            ProjectFinanceEmail existingUserSearch = projectFinanceEmailRepository.findOneByEmail(email);
            if(existingUserSearch != null) {
                return true;
            }
        }
        return false;
	}

    @Override
    public ServiceResult<UserResource> createApplicantUser(Long organisationId, Optional<Long> competitionId, UserResource userResource) {
        String roleName;
        if(isUserCompAdmin(userResource.getEmail())){
            roleName = COMP_ADMIN.getName();
        } else if(isUserProjectFinance(userResource.getEmail())){
            roleName = PROJECT_FINANCE.getName();
        } else {
            roleName = APPLICANT.getName();
        }
        User newUser = assembleUserFromResource(userResource);
        return validateUser(userResource, userResource.getPassword()).andOnSuccess(validUser -> addOrganisationToUser(newUser, organisationId).andOnSuccess(user ->
                addRoleToUser(user, roleName))).andOnSuccess(() ->
                createUserWithUid(newUser, userResource.getPassword(), competitionId));
    }

	private ServiceResult<UserResource> validateUser(UserResource userResource, String password) {
        return passwordPolicyValidator.validatePassword(password, userResource).andOnSuccessReturn(() -> userResource);
    }

    @Override
    public ServiceResult<Void> activateUser(Long userId){
        return getUser(userId).andOnSuccessReturnVoid(u -> {
            idpService.activateUser(u.getUid());
            u.setStatus(UserStatus.ACTIVE);
            userRepository.save(u);
        });
    }

    private ServiceResult<UserResource> createUserWithUid(User user, String password, Optional<Long> competitionId) {

        ServiceResult<String> uidFromIdpResult = idpService.createUserRecordWithUid(user.getEmail(), password);

        return uidFromIdpResult.andOnSuccessReturn(uidFromIdp -> {
            user.setUid(uidFromIdp);
            user.setStatus(UserStatus.INACTIVE);
            User savedUser = userRepository.save(user);
            final UserResource userResource = userMapper.mapToResource(savedUser);
            return userResource;
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
        newUser.setEmail(userResource.getEmail());
        newUser.setTitle(userResource.getTitle());
        newUser.setPhoneNumber(userResource.getPhoneNumber());

        return newUser;
    }

    @Override
    public ServiceResult<Void> sendUserVerificationEmail(final UserResource user, final Optional<Long> competitionId) {
        String verificationLink = getVerificationLink(user, competitionId);


        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new ExternalUserNotificationTarget(user.getName(), user.getEmail());

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("verificationLink", verificationLink);

        Notification notification = new Notification(from, singletonList(to), Notifications.VERIFY_EMAIL_ADDRESS, notificationArguments);
        return notificationService.sendNotification(notification, EMAIL).andOnSuccessReturnVoid();
    }

    private String getVerificationLink(UserResource user, Optional<Long> competitionId) {
        String hash = generateAndSaveVerificationHash(user, competitionId);
        return String.format("%s/registration/verify-email/%s", webBaseUrl, hash);
    }

    private String generateAndSaveVerificationHash(UserResource user, Optional<Long> competitionId) {
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
