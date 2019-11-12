package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.project.projectdetails.viewmodel.JoinAProjectViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.util.RestLookupCallbacks.find;

/**
 * This class is used as an entry point to accept an invite to a project.
 */
@Controller
@SecuredBySpring(value = "Controller",
        description = "All users with a valid invite hash are able to view and accept the corresponding project invite",
        securedType = AcceptProjectInviteController.class)
@PreAuthorize("permitAll")
public class AcceptProjectInviteController {

    public static final String INVITE_HASH = "project_invite_hash";

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Autowired
    private EncryptedCookieService cookieUtil;

    @Autowired
    private NavigationUtils navigationUtils;

    public static final String ACCEPT_INVITE_MAPPING = "/accept-invite/";
    public static final String ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING = "/registration/accept-invite-user-does-not-yet-exist-show-project";
    public static final String ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING = "/registration/accept-invite-user-exist-show-project";
    public static final String ACCEPT_INVITE_USER_EXIST_CONFIRM_MAPPING = "/registration/accept-invite-user-exist-confirm";

    public static final String ACCEPT_INVITE_USER_EXISTS_BUT_NOT_LOGGED_IN_VIEW = "project/registration/accept-invite-user-exists-but-not-logged-in";
    public static final String ACCEPT_INVITE_SHOW_PROJECT = "project/registration/accept-invite-show-project";
    public static final String ACCEPT_INVITE_FAILURE = "project/registration/accept-invite-failure";


    //===================================
    // Initial landing of the invite link
    //===================================

    @GetMapping(ACCEPT_INVITE_MAPPING + "{hash}")
    public String inviteEntryPage(
            @PathVariable("hash") final String hash,
            HttpServletResponse response,
            Model model,
            UserResource loggedInUser) {

        RestResult<String> result = find(inviteByHash(hash), checkUserExistsByHash(hash)).andOnSuccess((invite, userExists) -> {
            ValidationMessages errors = errorMessages(loggedInUser, invite);
            if (errors.hasErrors()) {
                return populateModelWithErrorsAndReturnErrorView(errors, model);
            }
            cookieUtil.saveToCookie(response, INVITE_HASH, hash);
            if (userExists && loggedInUser == null) {
                return restSuccess(ACCEPT_INVITE_USER_EXISTS_BUT_NOT_LOGGED_IN_VIEW);
            } else if (userExists) {
                return restSuccess("redirect:" + ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING);
            } else {
                return restSuccess("redirect:" + ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING);
            }
        });

        if (result.isFailure()) {
            return ACCEPT_INVITE_FAILURE;
        } else {
            return result.getSuccess();
        }
    }

    //==================================
    // Show the user the confirm project
    //==================================

    @GetMapping(ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING)
    public String acceptInviteUserDoesNotYetExistShowProject(HttpServletRequest request, Model model, UserResource loggedInUser) {
        model.addAttribute("userExists", false);
        return acceptInviteShowProject(request, model, loggedInUser);
    }

    @GetMapping(ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING)
    public String acceptInviteUserDoesExistShowProject(HttpServletRequest request, Model model, UserResource loggedInUser) {
        model.addAttribute("userExists", true);
        return acceptInviteShowProject(request, model, loggedInUser);
    }

    private String acceptInviteShowProject(HttpServletRequest request, Model model, UserResource loggedInUser) {
        String hash = cookieUtil.getCookieValue(request, INVITE_HASH);
        return projectInviteRestService.getInviteByHash(hash)
                .andOnSuccess(invite -> {

                    ValidationMessages errors = errorMessages(loggedInUser, invite);
                    if (errors.hasErrors()) {
                        return populateModelWithErrorsAndReturnErrorView(errors, model);
                    }

                    JoinAProjectViewModel japvm = new JoinAProjectViewModel();
                    japvm.setCompetitionName(invite.getCompetitionName());
                    japvm.setLeadApplicantName(invite.getLeadApplicant());
                    japvm.setOrganisationName(invite.getOrganisationName());
                    japvm.setLeadOrganisationName(invite.getLeadOrganisation());
                    japvm.setProjectName(invite.getProjectName());
                    model.addAttribute("model", japvm);
                    return restSuccess(ACCEPT_INVITE_SHOW_PROJECT);
                }).getSuccess();
    }

    //======================================================
    // Accept a project invite for a user who already exists
    //======================================================

    @GetMapping(ACCEPT_INVITE_USER_EXIST_CONFIRM_MAPPING)
    public String acceptInviteUserDoesExistConfirm(HttpServletRequest request,
                                                   UserResource loggedInUser,
                                                   Model model) {
        String hash = cookieUtil.getCookieValue(request, INVITE_HASH);
        return find(inviteByHash(hash), userByHash(hash)).andOnSuccess((invite, userExists) -> {
                    ValidationMessages errors = errorMessages(loggedInUser, invite);
                    if (errors.hasErrors()) {
                        return populateModelWithErrorsAndReturnErrorView(errors, model);
                    }
                    // Accept the invite - adding the user to the project
                    return projectInviteRestService.acceptInvite(hash, userExists.getId()).andOnSuccessReturn(() ->
                            navigationUtils.getRedirectToLandingPageUrl(request));
                }
        ).getSuccess();
    }


    //======================================================
    // Code to validate fundamental problems with the invite
    //======================================================

    public static RestResult<String> populateModelWithErrorsAndReturnErrorView(ValidationMessages errors, Model model) {
        model.addAttribute("failureMessageKeys", errors.getErrors());
        return restSuccess(ACCEPT_INVITE_FAILURE);
    }

    public static ValidationMessages errorMessages(UserResource loggedInUser, ProjectUserInviteResource invite) {
        ValidationMessages errors = new ValidationMessages();
        if (!invite.getStatus().equals(SENT)) {
            errors.addError(globalError("registration.INVITE_ALREADY_ACCEPTED"));
        } else if (loggedInUser != null && !invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
            errors.addError(globalError("registration.LOGGED_IN_WITH_OTHER_ACCOUNT"));
        }
        return errors;
    }

    private Supplier<RestResult<ProjectUserInviteResource>> inviteByHash(String hash) {
        return () -> projectInviteRestService.getInviteByHash(hash);
    }

    private Supplier<RestResult<Boolean>> checkUserExistsByHash(String hash) {
        return () -> projectInviteRestService.checkExistingUser(hash);
    }

    private Supplier<RestResult<UserResource>> userByHash(String hash) {
        return () -> projectInviteRestService.getUser(hash);
    }

}
