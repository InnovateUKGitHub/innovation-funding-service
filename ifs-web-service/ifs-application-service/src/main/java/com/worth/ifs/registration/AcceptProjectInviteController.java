package com.worth.ifs.registration;

import com.worth.ifs.BaseController;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.registration.service.RegistrationService;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.Supplier;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.invite.constant.InviteStatusConstants.SEND;
import static com.worth.ifs.util.CookieUtil.getCookieValue;
import static com.worth.ifs.util.CookieUtil.saveToCookie;
import static com.worth.ifs.util.RestLookupCallbacks.find;

/**
 * This class is use as an entry point to accept a invite to a project, to a application.
 */
@Controller
public class AcceptProjectInviteController extends BaseController {
    public static final String INVITE_HASH = "invite_hash";
    private static final Log LOG = LogFactory.getLog(AcceptProjectInviteController.class);
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Autowired
    private RegistrationService registrationService;

    private static final String ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING = "registration/project/accept-invite-user-does-not-yet-exist-show-project";
    private static final String ACCEPT_INVITE_MAPPING = "/accept-invite/project/";
    private static final String ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING = "registration/project/accept-invite-user-exist-show-project";
    private static final String ACCEPT_INVITE_USER_EXIST_CONFIRM_MAPPING = "registration/project/accept-invite-user-exist-confirm";


    @RequestMapping(value = ACCEPT_INVITE_MAPPING + "{hash}", method = RequestMethod.GET)
    public String inviteEntryPage(
            @PathVariable("hash") final String hash,
            HttpServletResponse response,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return find(inviteByHash(hash), inviteOrganisationByHash(hash), checkUserExistsByHash(hash)).andOnSuccess((invite, inviteOrganisation, userExists) -> {
            if (invite.getStatus().equals(SEND)) {
                saveToCookie(response, INVITE_HASH, hash);
                if (userExists && loggedInUser == null) {
                    return handleUserExistsButNotLoggedIn();
                } else if (userExists) {
                    return handleUserExistsAndAUserIsLoggedIn(loggedInUser, invite, inviteOrganisation, model);
                } else {
                    return handleUserDoesNotExistYet();
                }
            } else {
                return handleInviteAlreadyAccepted(response);
            }
        }).getSuccessObject();
    }

    private RestResult<String> handleUserExistsButNotLoggedIn() {
        return restSuccess("/registration/project/accept-invite-user-exists-but-not-logged-in.html");
    }

    private RestResult<String> handleUserExistsAndAUserIsLoggedIn(UserResource loggedInUser, InviteResource invite, InviteOrganisationResource inviteOrganisation, Model model) {
        Map<String, String> failureMessages = registrationService.getInvalidInviteMessages(loggedInUser, invite, inviteOrganisation);
        if (!failureMessages.isEmpty()) {
            failureMessages.forEach((messageKey, messageValue) -> model.addAttribute(messageKey, messageValue));
            return restSuccess("registration/project/accept-invite-failure");
        } else {
            return restSuccess("redirect:" + ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING);
        }
    }

    private RestResult<String> handleUserDoesNotExistYet() {
        return restSuccess("redirect:" + ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING);
    }

    private RestResult<String> handleInviteAlreadyAccepted(HttpServletResponse response) {
        cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
        return restSuccess("redirect:/login");
    }


    //===============


    @RequestMapping(value = ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING, method = RequestMethod.GET)
    public String acceptInviteUserDoesNotYetExistShowProject(HttpServletRequest request, Model model) {
        model.addAttribute("userExists", false);
        return acceptInviteShowProject(request, model);
    }

    @RequestMapping(value = ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING, method = RequestMethod.GET)
    public String acceptInviteUserDoesExistShowProject(HttpServletRequest request, Model model) {
        model.addAttribute("userExists", true);
        return acceptInviteShowProject(request, model);
    }

    private String acceptInviteShowProject(HttpServletRequest request, Model model) {
        String hash = getCookieValue(request, INVITE_HASH);
        return find(inviteOrganisationByHash(hash)).andOnSuccess(inviteOrganisationResource -> {
            model.addAttribute("TODO", "TODO view model");
            return restSuccess("/registration/project/accept-invite-show-project");
        }).getSuccessObject();
    }


    //===============


    @RequestMapping(value = ACCEPT_INVITE_USER_EXIST_CONFIRM_MAPPING, method = RequestMethod.GET)
    public String acceptInviteUserDoesExistComfirm(HttpServletRequest request) {
        String hash = getCookieValue(request, INVITE_HASH);
        return find(inviteByHash(hash), inviteOrganisationByHash(hash), checkUserExistsByHash(hash)).andOnSuccess((invite, inviteOrganisation, userExists) -> {
                    if (invite.getStatus().equals(SEND) && userExists) {

                        return restSuccess("TODO");
                    } else {
                        return restSuccess("TODO - fail");
                    }
                }

        ).getSuccessObject();
    }
    //===============

    private Supplier<RestResult<InviteResource>> inviteByHash(String hash) {
        return () -> inviteRestService.getInviteByHash(hash);
    }

    private Supplier<RestResult<InviteOrganisationResource>> inviteOrganisationByHash(String hash) {
        return () -> inviteRestService.getInviteOrganisationByHash(hash);
    }

    private Supplier<RestResult<Boolean>> checkUserExistsByHash(String hash) {
        return () -> inviteRestService.checkExistingUser(hash);
    }

    private Supplier<RestResult<UserResource>> getUserByHash(String hash) {
        return () -> inviteRestService.getUser(hash);
    }

    //===============

}
