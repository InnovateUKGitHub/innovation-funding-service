package org.innovateuk.ifs.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.crm.transactional.SILMessageRecordingService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.sil.SIlPayloadKeyType;
import org.innovateuk.ifs.sil.SIlPayloadType;
import org.innovateuk.ifs.sil.crm.resource.SilEDIStatus;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_INVALID_ARGUMENT;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.resource.UserCreationResource.UserCreationResourceBuilder.anUserCreationResource;
import static org.innovateuk.ifs.user.resource.UserRelatedURLs.*;

/**
 * This RestController exposes CRUD operations to both the
 * {org.innovateuk.ifs.user.service.UserRestServiceImpl} and other REST-API users
 * to manage {@link User} related data.
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    public static final Sort DEFAULT_USER_SORT = Sort.by(
            new Sort.Order(Sort.Direction.ASC, "firstName"),
            new Sort.Order(Sort.Direction.ASC, "lastName")
    );

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "40";

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CrmService crmService;

    @Autowired
    SILMessageRecordingService silMessagingService;

    @GetMapping("/uid/{uid}")
    public RestResult<UserResource> getUserByUid(@PathVariable String uid) {
        return baseUserService.getUserResourceByUid(uid).toGetResponse();
    }

    @GetMapping("/id/{id}")
    public RestResult<UserResource> getUserById(@PathVariable long id) {
        return baseUserService.getUserById(id).toGetResponse();
    }

    @PostMapping
    public RestResult<UserResource> createUser(@RequestBody UserCreationResource userCreationResource) {
        return registrationService.createUser(userCreationResource).toPostCreateResponse();
    }

    @PostMapping("/user-profile-status/{userId}")
    public RestResult<Void> createUserProfileStatus(@PathVariable long userId) {
        return registrationService.createUserProfileStatus(userId).toPostCreateResponse();
    }

    @GetMapping("/find-by-role/{userRole}")
    public RestResult<List<UserResource>> findByRole(@PathVariable Role userRole) {
        return baseUserService.findByProcessRole(userRole).toGetResponse();
    }

    @GetMapping("/find-by-role-and-status/{userRole}/status/{userStatus}")
    public RestResult<List<UserResource>> findByRoleAndUserStatus(@PathVariable Role userRole, @PathVariable UserStatus userStatus) {
        return baseUserService.findByProcessRoleAndUserStatus(userRole, userStatus).toGetResponse();
    }

    @GetMapping("/active")
    public RestResult<ManageUserPageResource> findActiveUsers(@RequestParam(required = false) String filter,
                                                              @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                              @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return userService.findActive(filter, PageRequest.of(pageIndex, pageSize, DEFAULT_USER_SORT)).toGetResponse();
    }

    @GetMapping("/inactive")
    public RestResult<ManageUserPageResource> findInactiveUsers(@RequestParam(required = false) String filter,
                                                                @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return userService.findInactive(filter, PageRequest.of(pageIndex, pageSize, DEFAULT_USER_SORT)).toGetResponse();
    }

    @GetMapping("/external/active")
    public RestResult<ManageUserPageResource> findActiveExternalUsers(@RequestParam(required = false) String filter,
                                                                      @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                      @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return userService.findActiveExternal(filter, PageRequest.of(pageIndex, pageSize, DEFAULT_USER_SORT)).toGetResponse();
    }

    @GetMapping("/external/inactive")
    public RestResult<ManageUserPageResource> findInactiveExternalUsers(@RequestParam(required = false) String filter,
                                                                        @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                        @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return userService.findInactiveExternal(filter, PageRequest.of(pageIndex, pageSize, DEFAULT_USER_SORT)).toGetResponse();
    }

    @PostMapping("/internal/create/{inviteHash}")
    public RestResult<Void> createInternalUser(@PathVariable("inviteHash") String inviteHash, @Valid @RequestBody InternalUserRegistrationResource internalUserRegistrationResource) {
        return registrationService.createUser(anUserCreationResource()
                        .withFirstName(internalUserRegistrationResource.getFirstName())
                        .withLastName(internalUserRegistrationResource.getLastName())
                        .withPassword(internalUserRegistrationResource.getPassword())
                        .withInviteHash(inviteHash)
                        .build())
                .andOnSuccessReturnVoid()
                .toPostCreateResponse();
    }

    @PostMapping("/internal/edit")
    public RestResult<Void> editInternalUser(@Valid @RequestBody EditUserResource editUserResource) {

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
        return userService.findByProcessRolesAndSearchCriteria(EnumSet.of(Role.APPLICANT), searchString, searchCategory).toGetResponse();
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


        log.debug(String.format("UserController verifyHash: %s", hash));
        return result.handleSuccessOrFailure(
                failure -> restFailure(failure.getErrors()),
                token -> {
                    JsonNode extraInfo = token.getExtraInfo().get("inviteId");
                    Long inviteId = Optional.ofNullable(extraInfo).isPresent() ? extraInfo.asLong() : null;

                    registrationService.activateApplicantAndSendDiversitySurvey(token.getClassPk(), inviteId).andOnSuccessReturnVoid(v -> {
                        tokenService.handleApplicationExtraAttributes(token)
                                .andOnSuccess(applicationResource -> crmService.syncCrmContact(token.getClassPk(), applicationResource.getCompetition(), applicationResource.getId()))
                                .andOnFailure(() -> tokenService.handleProjectExtraAttributes(token)
                                        .andOnSuccess(projectResource -> crmService.syncCrmContact(token.getClassPk(), projectResource.getId()))
                                        .andOnFailure(() -> crmService.syncCrmContact(token.getClassPk()))
                                );
                        tokenService.removeToken(token);
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
    public RestResult<Void> grantRole(@PathVariable long id,
                                      @PathVariable Role role) {
        return userService.grantRole(new GrantRoleCommand(id, role)).toPostResponse();
    }

    @PreAuthorize("permitAll()")
    @PatchMapping(value = "/v1/edi")
    public RestResult<Void> updateUser(
            @Valid @RequestBody SilEDIStatus surveyStatus,
            BindingResult bindingResult, HttpServletRequest request) throws JsonProcessingException {


        if (bindingResult.hasErrors()) {
            log.error(String.format("edi-status-update error: incorrect json: %s ", surveyStatus));
            return RestResult.restFailure(new Error(GENERAL_INVALID_ARGUMENT,
                    bindingResult
                            .getAllErrors()
                            .stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.toList())));
        }

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        String surveyStatusJson = objectMapper.writer().writeValueAsString(surveyStatus);
        silMessagingService.recordSilMessage(SIlPayloadType.USER_UPDATE, SIlPayloadKeyType.USER_ID, user==null?null:user.getUid(),
                surveyStatusJson, null);


        if (user == null) {
            log.error("edi-status-update error: user not found ");
            return RestResult.restFailure(HttpStatus.UNAUTHORIZED);
        } else {
            log.info(String.format("edi-status-update: user id=%d, name=%s ", user.getId(), user.getName()));
            user.setEdiStatus(surveyStatus.getEdiStatus());
            user.setEdiReviewDate(surveyStatus.getEdiReviewDate());
            return userService
                    .updateDetails(user)
                    .andOnSuccessReturnVoid()
                    .toPutResponseNoContentResponse();

        }
    }
}