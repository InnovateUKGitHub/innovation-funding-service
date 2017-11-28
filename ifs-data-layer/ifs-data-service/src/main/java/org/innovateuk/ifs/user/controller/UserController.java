package org.innovateuk.ifs.user.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.innovateuk.ifs.user.transactional.CrmService;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.resource.UserRelatedURLs.*;
import static org.innovateuk.ifs.user.resource.UserRoleType.externalApplicantRoles;

/**
 * This RestController exposes CRUD operations to both the
 * {org.innovateuk.ifs.user.service.UserRestServiceImpl} and other REST-API users
 * to manage {@link User} related data.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Log LOG = LogFactory.getLog(UserController.class);

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "40";

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private TokenService tokenService;

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

    @GetMapping("/findByRole/{userRole}")
    public RestResult<List<UserResource>> findByRole(@PathVariable("userRole") final UserRoleType userRole) {
        return baseUserService.findByProcessRole(userRole).toGetResponse();
    }

    @GetMapping("/internal/active")
    public RestResult<UserPageResource> findActiveInternalUsers(@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize){
        return userService.findActiveByProcessRoles(UserRoleType.internalRoles(), new PageRequest(pageIndex, pageSize)).toGetResponse();
    }

    @GetMapping("/internal/inactive")
    public RestResult<UserPageResource> findInactiveInternalUsers(@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                  @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize){
        return userService.findInactiveByProcessRoles(UserRoleType.internalRoles(), new PageRequest(pageIndex, pageSize)).toGetResponse();
    }

    @PostMapping("/internal/create/{inviteHash}")
    public RestResult<Void> createInternalUser(@PathVariable("inviteHash") String inviteHash, @Valid @RequestBody InternalUserRegistrationResource internalUserRegistrationResource){
        return registrationService.createInternalUser(inviteHash, internalUserRegistrationResource).toPostCreateResponse();
    }

    @PostMapping("/internal/edit")
    public RestResult<Void> editInternalUser(@Valid @RequestBody EditUserResource editUserResource){

        UserResource userToEdit = getUserToEdit(editUserResource);

        return registrationService.editInternalUser(userToEdit, editUserResource.getUserRoleType()).toPostResponse();
    }

    private UserResource getUserToEdit(EditUserResource editUserResource) {

        UserResource userToEdit = new UserResource();
        userToEdit.setId(editUserResource.getUserId());
        userToEdit.setFirstName(editUserResource.getFirstName());
        userToEdit.setLastName(editUserResource.getLastName());

        return userToEdit;
    }

    @GetMapping("/findAll/")
    public RestResult<List<UserResource>> findAll() {
        return baseUserService.findAll().toGetResponse();
    }

    @GetMapping("/findExternalUsers")
    public RestResult<List<UserOrganisationResource>> findExternalUsers(@RequestParam(value = "searchString") final String searchString,
                                                                        @RequestParam(value = "searchCategory") final SearchCategory searchCategory) {
        return userService.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory).toGetResponse();
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
                    registrationService.activateApplicantAndSendDiversitySurvey(token.getClassPk()).andOnSuccessReturnVoid(v -> {
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
        return userService.updateDetails(userResource).andOnSuccessReturnVoid(() -> crmService.syncCrmContact(userResource.getId())).toPutResponse();
    }


    @GetMapping("/id/{id}/deactivate")
    public RestResult<Void> deactivateUser(@PathVariable("id") final Long id) {
        return registrationService.deactivateUser(id).toGetResponse();
    }

    @GetMapping("/id/{id}/reactivate")
    public RestResult<Void> reactivateUser(@PathVariable("id") final Long id) {
        return registrationService.activateUser(id).toGetResponse();
    }
 }
