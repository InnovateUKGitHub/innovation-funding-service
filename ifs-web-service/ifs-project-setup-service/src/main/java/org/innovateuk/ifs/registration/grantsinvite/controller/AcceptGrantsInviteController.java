package org.innovateuk.ifs.registration.grantsinvite.controller;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.registration.grantsinvite.viewmodel.GrantsInviteViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.util.RestLookupCallbacks.find;

/**
 * This class is used as an entry point to accept an invite to a live project.
 */
@Controller
@SecuredBySpring(value = "Controller",
        description = "All users with a valid invite hash are able to view and accept the corresponding project invite",
        securedType = AcceptGrantsInviteController.class)
@PreAuthorize("permitAll")
@RequestMapping("/project/{projectId}/grants/invite")
public class AcceptGrantsInviteController {

    public static final String INVITE_HASH = "grants_invite_hash";
    public static final String ACCEPT_INVITE_FAILURE = "project/registration/accept-invite-failure";

    @Autowired
    private GrantsInviteRestService grantsInviteRestService;

    @Autowired
    private EncryptedCookieService cookieUtil;

    @Autowired
    private NavigationUtils navigationUtils;

    //===================================
    // Initial landing of the invite link
    //===================================

    @GetMapping("/{hash}")
    public String inviteEntryPage(
            @PathVariable final long projectId,
            @PathVariable final String hash,
            HttpServletResponse response,
            Model model,
            UserResource loggedInUser) {

        RestResult<String> result = find(inviteByHash(projectId, hash)).andOnSuccess((invite) -> {
            ValidationMessages errors = validateUserCanAcceptInvite(loggedInUser, invite);
            if (errors.hasErrors()) {
                return populateModelWithErrorsAndReturnErrorView(errors, model);
            }
            cookieUtil.saveToCookie(response, INVITE_HASH, hash);
            model.addAttribute("model", new GrantsInviteViewModel(invite.getApplicationId(),
                    projectId, invite.getProjectName(), invite.getGrantsInviteRole(), invite.userExists(),
                    loggedInUser != null));
            return restSuccess("project/grants-invite/accept-invite");
        });

        if (result.isFailure()) {
            return ACCEPT_INVITE_FAILURE;
        } else {
            return result.getSuccess();
        }
    }

    //======================================================
    // Accept a project invite for a user who already exists
    //======================================================

    @GetMapping("accept-authenticated")
    public String acceptInviteUserDoesExistConfirm(HttpServletRequest request,
                                                   @PathVariable long projectId,
                                                   UserResource loggedInUser,
                                                   Model model) {
        String hash = cookieUtil.getCookieValue(request, INVITE_HASH);
        return find(inviteByHash(projectId, hash)).andOnSuccess((invite) -> {
                    ValidationMessages errors = validateUserCanAcceptInvite(loggedInUser, invite);
                    if (errors.hasErrors()) {
                        return populateModelWithErrorsAndReturnErrorView(errors, model);
                    }
                    // Accept the invite - adding the user to the project
                    return grantsInviteRestService.acceptInvite(projectId, invite.getId()).andOnSuccessReturn(() ->
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

    public static ValidationMessages validateUserCanAcceptInvite(UserResource loggedInUser, SentGrantsInviteResource invite) {
        ValidationMessages errors = new ValidationMessages();
        if (!invite.getStatus().equals(SENT)) {
            errors.addError(globalError("registration.INVITE_ALREADY_ACCEPTED"));
        } else if (loggedInUser != null && !invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
            errors.addError(globalError("registration.LOGGED_IN_WITH_OTHER_ACCOUNT"));
        }
        return errors;
    }

    private Supplier<RestResult<SentGrantsInviteResource>> inviteByHash(long projectId, String hash) {
        return () -> grantsInviteRestService.getInviteByHash(projectId, hash);
    }
}
