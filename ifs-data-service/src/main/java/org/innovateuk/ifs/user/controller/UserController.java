package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.transactional.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.resource.UserRelatedURLs.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * This RestController exposes CRUD operations to both the
 * {org.innovateuk.ifs.user.service.UserRestServiceImpl} and other REST-API users
 * to manage {@link User} related data.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Log LOG = LogFactory.getLog(UserController.class);

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private CrmService crmService;


    @GetMapping("/uid/{uid}")
    public RestResult<UserResource> getUserByUid(@PathVariable("uid") final String uid) {
        return baseUserService.getUserResourceByUid(uid).toGetResponse();
    }

    @GetMapping("/id/{id}")
    public RestResult<UserResource> getUserById(@PathVariable("id") final Long id) {
        return baseUserService.getUserById(id).toGetResponse();
    }

    @GetMapping("/findByRole/{userRoleName}")
    public RestResult<List<UserResource>> findByRole(@PathVariable("userRoleName") final String userRoleName) {
        return baseUserService.findByProcessRole(UserRoleType.fromName(userRoleName)).toGetResponse();
    }

    @GetMapping("/findAll/")
    public RestResult<List<UserResource>> findAll() {
        return baseUserService.findAll().toGetResponse();
    }

    @GetMapping("/findByEmail/{email}/")
    public RestResult<UserResource> findByEmail(@PathVariable("email") final String email) {
        return userService.findByEmail(email).toGetResponse();
    }

    @GetMapping("/findAssignableUsers/{applicationId}")
    public RestResult<Set<UserResource>> findAssignableUsers(@PathVariable("applicationId") final Long applicationId) {
        return userService.findAssignableUsers(applicationId).toGetResponse();
    }

    @GetMapping("/findRelatedUsers/{applicationId}")
    public RestResult<Set<UserResource>> findRelatedUsers(@PathVariable("applicationId") final Long applicationId) {
        return userService.findRelatedUsers(applicationId).toGetResponse();
    }

    @GetMapping("/" + URL_SEND_PASSWORD_RESET_NOTIFICATION + "/{emailaddress}/")
    public RestResult<Void> sendPasswordResetNotification(@PathVariable("emailaddress") final String emailAddress) {
        return userService.findByEmail(emailAddress)
                .andOnSuccessReturn(userService::sendPasswordResetNotification)
                .toPutResponse();
    }

    @GetMapping("/" + URL_CHECK_PASSWORD_RESET_HASH + "/{hash}")
    public RestResult<Void> checkPasswordReset(@PathVariable("hash") final String hash) {
        return tokenService.getPasswordResetToken(hash).andOnSuccessReturnVoid().toPutResponse();
    }

    @PostMapping("/" + URL_PASSWORD_RESET + "/{hash}")
    public RestResult<Void> resetPassword(@PathVariable("hash") final String hash, @RequestBody final String password) {
        return userService.changePassword(hash, password)
                .toPutResponse();
    }

    @GetMapping("/" + URL_VERIFY_EMAIL + "/{hash}")
    public RestResult<Void> verifyEmail(@PathVariable("hash") final String hash) {
        final ServiceResult<Token> result = tokenService.getEmailToken(hash);
        LOG.debug(String.format("UserController verifyHash: %s", hash));
        return result.handleSuccessOrFailure(
                failure -> restFailure(failure.getErrors()),
                token -> {
                    registrationService.activateUserAndSendDiversitySurvey(token.getClassPk()).andOnSuccessReturnVoid(v -> {
                        tokenService.handleExtraAttributes(token);
                        tokenService.removeToken(token);
                        crmService.syncCrmContact(token.getClassPk());
                    });
                    return restSuccess();
                });
    }

    @PutMapping("/" + URL_RESEND_EMAIL_VERIFICATION_NOTIFICATION + "/{emailAddress}/")
    public RestResult<Void> resendEmailVerificationNotification(@PathVariable("emailAddress") final String emailAddress) {
        return userService.findInactiveByEmail(emailAddress)
                .andOnSuccessReturn(user -> registrationService.resendUserVerificationEmail(user))
                .toPutResponse();
    }

    @PostMapping("/createLeadApplicantForOrganisation/{organisationId}")
    public RestResult<UserResource> createUser(@PathVariable("organisationId") final Long organisationId, @RequestBody UserResource userResource) {
        return registrationService.createOrganisationUser(organisationId, userResource).andOnSuccessReturn(created ->
                {
                    registrationService.sendUserVerificationEmail(created, empty()).getSuccessObjectOrThrowException();
                    return created;
                }
        ).toPostCreateResponse();
    }

    @PostMapping("/createLeadApplicantForOrganisation/{organisationId}/{competitionId}")
    public RestResult<UserResource> createUser(@PathVariable("organisationId") final Long organisationId, @PathVariable("competitionId") final Long competitionId, @RequestBody UserResource userResource) {
        return registrationService.createOrganisationUser(organisationId, userResource).andOnSuccessReturn(created ->
                {
                    registrationService.sendUserVerificationEmail(created, of(competitionId)).getSuccessObjectOrThrowException();
                    return created;
                }
        ).toPostCreateResponse();
    }

    @PostMapping("/updateDetails")
    public RestResult<Void> updateDetails(@RequestBody UserResource userResource) {
        return userProfileService.updateDetails(userResource).andOnSuccessReturnVoid(() -> crmService.syncCrmContact(userResource.getId())).toPutResponse();
    }

    @GetMapping("/id/{userId}/getProfileSkills")
    public RestResult<ProfileSkillsResource> getProfileSkills(@PathVariable("userId") long userId) {
        return userProfileService.getProfileSkills(userId).toGetResponse();
    }

    @PutMapping("/id/{userId}/updateProfileSkills")
    public RestResult<Void> updateProfileSkills(@PathVariable("userId") long id,
                                                @Valid @RequestBody ProfileSkillsEditResource profileSkills) {
        return userProfileService.updateProfileSkills(id, profileSkills).toPutResponse();
    }

    @GetMapping("/id/{userId}/getProfileAgreement")
    public RestResult<ProfileAgreementResource> getProfileAgreement(@PathVariable("userId") long userId) {
        return userProfileService.getProfileAgreement(userId).toGetResponse();
    }

    @PutMapping("/id/{userId}/updateProfileAgreement")
    public RestResult<Void> updateProfileAgreement(@PathVariable("userId") long userId) {
        return userProfileService.updateProfileAgreement(userId).toPutResponse();
    }

    @GetMapping("/id/{userId}/getUserAffiliations")
    public RestResult<List<AffiliationResource>> getUserAffiliations(@PathVariable("userId") Long userId) {
        return userProfileService.getUserAffiliations(userId).toGetResponse();
    }

    @PutMapping("/id/{userId}/updateUserAffiliations")
    public RestResult<Void> updateUserAffiliations(@PathVariable("userId") Long userId,
                                                   @RequestBody List<AffiliationResource> affiliations) {
        return userProfileService.updateUserAffiliations(userId, affiliations).toPutResponse();
    }

    @GetMapping("/id/{userId}/getUserProfile")
    public RestResult<UserProfileResource> getUserProfile(@PathVariable("userId") Long userId) {
        return userProfileService.getUserProfile(userId).toGetResponse();
    }

    @PutMapping("/id/{userId}/updateUserProfile")
    public RestResult<Void> updateUserProfile(@PathVariable("userId") Long userId,
                                              @RequestBody UserProfileResource profileDetails) {
        return userProfileService.updateUserProfile(userId, profileDetails).toPutResponse();
    }

    @GetMapping("/id/{userId}/profileStatus")
    public RestResult<UserProfileStatusResource> getUserProfileStatus(@PathVariable("userId") Long userId) {
        return userProfileService.getUserProfileStatus(userId).toGetResponse();
    }
}
