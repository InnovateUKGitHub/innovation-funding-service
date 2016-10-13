package com.worth.ifs.user.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.transactional.TokenService;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.*;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.UserProfileService;
import com.worth.ifs.user.transactional.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.user.service.UserRestServiceImpl} and other REST-API users
 * to manage {@link User} related data.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Log LOG = LogFactory.getLog(UserController.class);

    public static final String URL_CHECK_PASSWORD_RESET_HASH = "checkPasswordResetHash";
    public static final String URL_PASSWORD_RESET = "passwordReset";
    public static final String URL_SEND_PASSWORD_RESET_NOTIFICATION = "sendPasswordResetNotification";
    public static final String URL_VERIFY_EMAIL = "verifyEmail";
    public static final String URL_RESEND_EMAIL_VERIFICATION_NOTIFICATION = "resendEmailVerificationNotification";

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserProfileService userProfileService;

    @RequestMapping("/uid/{uid}")
    public RestResult<UserResource> getUserByUid(@PathVariable("uid") final String uid) {
        return userService.getUserResourceByUid(uid).toGetResponse();
    }

    @RequestMapping("/id/{id}")
    public RestResult<UserResource> getUserById(@PathVariable("id") final Long id) {
        return userService.getUserById(id).toGetResponse();
    }

    @RequestMapping("/findByRole/{userRoleName}")
    public RestResult<List<UserResource>> findByRole(@PathVariable("userRoleName") final String userRoleName) {
        return userService.findByProcessRole(UserRoleType.fromName(userRoleName)).toGetResponse();
    }

    @RequestMapping("/findAll/")
    public RestResult<List<UserResource>> findAll() {
        return userService.findAll().toGetResponse();
    }

    @RequestMapping("/findByEmail/{email}/")
    public RestResult<UserResource> findByEmail(@PathVariable("email") final String email) {
        return userService.findByEmail(email).toGetResponse();
    }

    @RequestMapping("/findAssignableUsers/{applicationId}")
    public RestResult<Set<UserResource>> findAssignableUsers(@PathVariable("applicationId") final Long applicationId) {
        return userService.findAssignableUsers(applicationId).toGetResponse();
    }

    @RequestMapping("/findRelatedUsers/{applicationId}")
    public RestResult<Set<UserResource>> findRelatedUsers(@PathVariable("applicationId") final Long applicationId) {
        return userService.findRelatedUsers(applicationId).toGetResponse();
    }

    @RequestMapping("/" + URL_SEND_PASSWORD_RESET_NOTIFICATION + "/{emailaddress}/")
    public RestResult<Void> sendPasswordResetNotification(@PathVariable("emailaddress") final String emailAddress) {
        return userService.findByEmail(emailAddress)
                .andOnSuccessReturn(userService::sendPasswordResetNotification)
                .toPutResponse();
    }

    @RequestMapping("/" + URL_CHECK_PASSWORD_RESET_HASH + "/{hash}")
    public RestResult<Void> checkPasswordReset(@PathVariable("hash") final String hash) {
        return tokenService.getPasswordResetToken(hash).andOnSuccessReturnVoid().toPutResponse();
    }

    @RequestMapping(value = "/" + URL_PASSWORD_RESET + "/{hash}", method = POST)
    public RestResult<Void> resetPassword(@PathVariable("hash") final String hash, @RequestBody final String password) {
        return userService.changePassword(hash, password)
                .toPutResponse();
    }

    @RequestMapping("/" + URL_VERIFY_EMAIL + "/{hash}")
    public RestResult<Void> verifyEmail(@PathVariable("hash") final String hash) {
        final ServiceResult<Token> result = tokenService.getEmailToken(hash);
        LOG.debug(String.format("UserController verifyHash: %s", hash));
        return result.handleSuccessOrFailure(
                failure -> restFailure(failure.getErrors()),
                token -> {
                    registrationService.activateUser(token.getClassPk()).andOnSuccessReturnVoid(v -> {
                        tokenService.handleExtraAttributes(token);
                        tokenService.removeToken(token);
                    });
                    return RestResult.restSuccess();
                });
    }

    @RequestMapping(value = "/" + URL_RESEND_EMAIL_VERIFICATION_NOTIFICATION + "/{emailAddress}/", method = PUT)
    public RestResult<Void> resendEmailVerificationNotification(@PathVariable("emailAddress") final String emailAddress) {
        return userService.findInactiveByEmail(emailAddress)
                .andOnSuccessReturn(user -> registrationService.resendUserVerificationEmail(user))
                .toPutResponse();
    }

    @RequestMapping(value = "/createLeadApplicantForOrganisation/{organisationId}", method = POST)
    public RestResult<UserResource> createUser(@PathVariable("organisationId") final Long organisationId, @RequestBody UserResource userResource) {
        return registrationService.createOrganisationUser(organisationId, userResource).andOnSuccessReturn(created ->
                {
                    registrationService.sendUserVerificationEmail(created, empty()).getSuccessObjectOrThrowException();
                    return created;
                }
        ).toPostCreateResponse();
    }

    @RequestMapping(value = "/createLeadApplicantForOrganisation/{organisationId}/{competitionId}", method = POST)
    public RestResult<UserResource> createUser(@PathVariable("organisationId") final Long organisationId, @PathVariable("competitionId") final Long competitionId, @RequestBody UserResource userResource) {
        return registrationService.createOrganisationUser(organisationId, userResource).andOnSuccessReturn(created ->
                {
                    registrationService.sendUserVerificationEmail(created, of(competitionId)).getSuccessObjectOrThrowException();
                    return created;
                }
        ).toPostCreateResponse();
    }

    @RequestMapping(value = "/updateDetails", method = POST)
    public RestResult<Void> updateDetails(@RequestBody UserResource userResource) {
        return userProfileService.updateDetails(userResource).toPutResponse();
    }

    @RequestMapping(value = "/id/{id}/getProfileSkills", method = GET)
    public RestResult<ProfileSkillsResource> getProfileSkills(@PathVariable("id") Long id) {
        return userProfileService.getProfileSkills(id).toGetResponse();
    }

    @RequestMapping(value = "/id/{id}/updateProfileSkills", method = PUT)
    public RestResult<Void> updateProfileSkills(@PathVariable("id") Long id,
                                          @RequestBody ProfileSkillsResource profileSkills) {
        return userProfileService.updateProfileSkills(id, profileSkills).toPutResponse();
    }

    @RequestMapping(value = "/id/{userId}/getUserAffiliations", method = GET)
    public RestResult<List<AffiliationResource>> getUserAffiliations(@PathVariable("userId") Long userId) {
        return userProfileService.getUserAffiliations(userId).toGetResponse();
    }

    @RequestMapping(value = "/id/{userId}/updateUserAffiliations", method = PUT)
    public RestResult<Void> updateUserAffiliations(@PathVariable("userId") Long userId,
                                                   @RequestBody List<AffiliationResource> affiliations) {
        return userProfileService.updateUserAffiliations(userId, affiliations).toPutResponse();
    }
}
