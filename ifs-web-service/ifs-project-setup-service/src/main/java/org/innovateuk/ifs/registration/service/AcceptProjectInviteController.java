package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.project.projectdetails.viewmodel.JoinAProjectViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.RedirectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.util.RestLookupCallbacks.find;

/**
 * This class is use as an entry point to accept a invite to a project, to a application.
 */
@Controller
@PreAuthorize("permitAll")
public class AcceptProjectInviteController {

    public static final String INVITE_HASH = "project_invite_hash";

    @Autowired
    private ProjectInviteRestService projectInviteRestService;
    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private CookieUtil cookieUtil;

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
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return find(inviteByHash(hash), checkUserExistsByHash(hash)).andOnSuccess((invite, userExists) -> {
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
        }).getSuccessObject();
    }

    //==================================
    // Show the user the confirm project
    //==================================

    @GetMapping(ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING)
    public String acceptInviteUserDoesNotYetExistShowProject(HttpServletRequest request, Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        model.addAttribute("userExists", false);
        return acceptInviteShowProject(request, model, loggedInUser);
    }

    @GetMapping(ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING)
    public String acceptInviteUserDoesExistShowProject(HttpServletRequest request, Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser) {
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
                }).getSuccessObjectOrThrowException();
    }

    //======================================================
    // Accept a project invite for a user who already exists
    //======================================================

    @GetMapping(ACCEPT_INVITE_USER_EXIST_CONFIRM_MAPPING)
    public String acceptInviteUserDoesExistConfirm(HttpServletRequest request,
                                                   @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                                   Model model) {
        String hash = cookieUtil.getCookieValue(request, INVITE_HASH);
        return find(inviteByHash(hash), userByHash(hash)).andOnSuccess((invite, userExists) -> {
                    ValidationMessages errors = errorMessages(loggedInUser, invite);
                    if (errors.hasErrors()) {
                        return populateModelWithErrorsAndReturnErrorView(errors, model);
                    }
                    // Accept the invite - adding the user to the project
                    return projectInviteRestService.acceptInvite(hash, userExists.getId()).andOnSuccessReturn(() ->
                            RedirectUtils.redirectToApplicationService(request, "/applicant/dashboard"));

                }
        ).getSuccessObject();
    }


    //======================================================
    // Code to validate fundamental problems with the invite
    //======================================================

    public static RestResult<String> populateModelWithErrorsAndReturnErrorView(ValidationMessages errors, Model model) {
        model.addAttribute("failureMessageKeys", errors.getErrors());
        return restSuccess(ACCEPT_INVITE_FAILURE);
    }

    public static ValidationMessages errorMessages(UserResource loggedInUser, InviteProjectResource invite) {
        ValidationMessages errors = new ValidationMessages();
        if (!invite.getStatus().equals(SENT)) {
            errors.addError(globalError("registration.INVITE_ALREADY_ACCEPTED"));
        } else if (loggedInUser != null && !invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
            errors.addError(globalError("registration.LOGGED_IN_WITH_OTHER_ACCOUNT"));
        }
        return errors;
    }

    private Supplier<RestResult<InviteProjectResource>> inviteByHash(String hash) {
        return () -> projectInviteRestService.getInviteByHash(hash);
    }

    private Supplier<RestResult<Boolean>> checkUserExistsByHash(String hash) {
        return () -> projectInviteRestService.checkExistingUser(hash);
    }

    private Supplier<RestResult<UserResource>> userByHash(String hash) {
        return () -> projectInviteRestService.getUser(hash);
    }

}