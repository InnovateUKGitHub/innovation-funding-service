package org.innovateuk.ifs.user.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.command.GrantRoleCommand;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.resource.UserRelatedURLs.*;

/**
 * This RestController exposes CRUD operations to both the
 * {org.innovateuk.ifs.user.service.UserRestServiceImpl} and other REST-API users
 * to manage {@link User} related data.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    public static final Sort DEFAULT_USER_SORT = new Sort(
            new Sort.Order(Sort.Direction.ASC, "firstName"),
            new Sort.Order(Sort.Direction.ASC, "lastName")
    );
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
    public RestResult<UserResource> getUserByUid(@PathVariable String uid) {
        return baseUserService.getUserResourceByUid(uid).toGetResponse();
    }

    @GetMapping("/id/{id}")
    public RestResult<UserResource> getUserById(@PathVariable long id) {
        return baseUserService.getUserById(id).toGetResponse();
    }

    @GetMapping("/find-by-role/{userRole}")
    public RestResult<List<UserResource>> findByRole(@PathVariable Role userRole) {
        return baseUserService.findByProcessRole(userRole).toGetResponse();
    }

    @GetMapping("/active")
    public RestResult<ManageUserPageResource> findActiveUsers(@RequestParam(required = false) String filter,
                                                        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                        @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return userService.findActive(filter, PageRequest.of(pageIndex, pageSize, DEFAULT_USER_SORT)).toGetResponse();
    }

    @GetMapping("/inactive")
    public RestResult<ManageUserPageResource> findInactiveUsers(@RequestParam(required = false)  String filter,
                                                          @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                          @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize){
        return userService.findInactive(filter, PageRequest.of(pageIndex, pageSize, DEFAULT_USER_SORT)).toGetResponse();
    }

    @GetMapping("/external/active")
    public RestResult<ManageUserPageResource> findActiveExternalUsers(@RequestParam(required = false)  String filter,
                                                                @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return userService.findActiveExternal(filter, PageRequest.of(pageIndex, pageSize, DEFAULT_USER_SORT)).toGetResponse();
    }

    @GetMapping("/external/inactive")
    public RestResult<ManageUserPageResource> findInactiveExternalUsers(@RequestParam(required = false)  String filter,
                                                                  @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                  @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize){
        return userService.findInactiveExternal(filter, PageRequest.of(pageIndex, pageSize, DEFAULT_USER_SORT)).toGetResponse();
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

    @GetMapping("/find-all/")
    public RestResult<List<UserResource>> findAll() {
        return baseUserService.findAll().toGetResponse();
    }

    @GetMapping("/find-external-users")
    public RestResult<List<UserOrganisationResource>> findExternalUsers(@RequestParam String searchString,
                                                                        @RequestParam SearchCategory searchCategory) {
        return userService.findByProcessRolesAndSearchCriteria(Role.externalApplicantRoles(), searchString, searchCategory).toGetResponse();
    }

    @GetMapping("/find-by-email/{email}/")
    public RestResult<UserResource> findByEmail(@PathVariable String email) {
        return userService.findByEmail(email).toGetResponse();
    }

    @GetMapping("/find-assignable-users/{applicationId}")
    public RestResult<Set<UserResource>> findAssignableUsers(@PathVariable long applicationId) {
        return userService.findAssignableUsers(applicationId).toGetResponse();
    }

    @GetMapping("/find-related-users/{applicationId}")
    public RestResult<Set<UserResource>> findRelatedUsers(@PathVariable long applicationId) {
        return userService.findRelatedUsers(applicationId).toGetResponse();
    }

    @GetMapping("/" + URL_SEND_PASSWORD_RESET_NOTIFICATION + "/{emailAddress}/")
    public RestResult<Void> sendPasswordResetNotification(@PathVariable String emailAddress) {
        return userService.findByEmail(emailAddress)
                .andOnSuccessReturn(userService::sendPasswordResetNotification)
                .toPutResponse();
    }

    @GetMapping("/" + URL_CHECK_PASSWORD_RESET_HASH + "/{hash}")
    public RestResult<Void> checkPasswordReset(@PathVariable String hash) {
        return tokenService.getPasswordResetToken(hash).andOnSuccessReturnVoid().toPutResponse();
    }

    @PostMapping("/" + URL_PASSWORD_RESET + "/{hash}")
    public RestResult<Void> resetPassword(@PathVariable String hash, @RequestBody final String password) {
        return userService.changePassword(hash, password)
                .toPutResponse();
    }

    @GetMapping("/" + URL_VERIFY_EMAIL + "/{hash}")
    public RestResult<Void> verifyEmail(@PathVariable String hash) {
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
    public RestResult<Void> resendEmailVerificationNotification(@PathVariable String emailAddress) {
        return userService.findInactiveByEmail(emailAddress)
                .andOnSuccessReturn(user -> registrationService.resendUserVerificationEmail(user))
                .toPutResponse();
    }

    @PostMapping("/create-lead-applicant-for-organisation/{organisationId}")
    public RestResult<UserResource> createUser(@PathVariable long organisationId, @RequestBody UserResource userResource) {
        return registrationService.createUser(userResource).toPostCreateResponse();
    }

    @PostMapping("/create-lead-applicant-for-organisation/{organisationId}/{competitionId}")
    public RestResult<UserResource> createUser(@PathVariable long organisationId, @PathVariable long competitionId, @RequestBody UserResource userResource) {
        return registrationService.createUserWithCompetitionContext(competitionId, organisationId, userResource).toPostCreateResponse();
    }

    @PostMapping("/id/{userId}/agree-new-site-terms-and-conditions")
    public RestResult<Void> agreeNewSiteTermsAndConditions(@PathVariable long userId) {
        return userService.agreeNewTermsAndConditions(userId).toPostResponse();
    }

    @PostMapping("/update-details")
    public RestResult<Void> updateDetails(@RequestBody UserResource userResource) {
        return userService.updateDetails(userResource).andOnSuccessReturnVoid(() -> crmService.syncCrmContact(userResource.getId())).toPutResponse();
    }

    @PutMapping("{id}/update-email/{email:.+}")
    public RestResult<Void> updateEmail(@PathVariable long id, @PathVariable String email) {
        return userService.updateEmail(id, email).toPutResponse();
    }

    @PostMapping("/id/{id}/deactivate")
    public RestResult<Void> deactivateUser(@PathVariable long id) {
        return registrationService.deactivateUser(id).toPostResponse();
    }

    @PostMapping("/id/{id}/reactivate")
    public RestResult<Void> reactivateUser(@PathVariable long id) {
        return registrationService.activateUser(id).toPostResponse();
    }

    @PostMapping("{id}/grant/{role}")
    public RestResult<Void> grantRole(@PathVariable long id, @PathVariable Role role) {
        return userService.grantRole(new GrantRoleCommand(id, role)).toPostResponse();
    }
}