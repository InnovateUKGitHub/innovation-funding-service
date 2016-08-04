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

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.invite.constant.InviteStatusConstants.SEND;
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

    private RestResult<String> handleUserExistsButNotLoggedIn() {
        return restSuccess("redirect:/registration/project/accept-invite-not-logged-in");
    }

    @RequestMapping(value = "/accept-invite/project/{hash}", method = RequestMethod.GET)
    public String inviteEntryPage(
            @PathVariable("hash") final String hash,
            HttpServletResponse response,
            HttpServletRequest request,
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return find(() -> inviteRestService.getInviteByHash(hash),
                () -> inviteRestService.getInviteOrganisationByHash(hash),
                () -> inviteRestService.checkExistingUser(hash)).andOnSuccess((invite, inviteOrganisation, userExists) -> {
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

    private RestResult<String> handleUserExistsAndAUserIsLoggedIn(UserResource loggedInUser, InviteResource invite, InviteOrganisationResource inviteOrganisation, Model model) {
        Map<String, String> failureMessages = registrationService.getInvalidInviteMessages(loggedInUser, invite, inviteOrganisation);
        if (!failureMessages.isEmpty()) {
            failureMessages.forEach((messageKey, messageValue) -> model.addAttribute(messageKey, messageValue));
            return restSuccess("registration/project/accept-invite-failure");
        } else {
            return restSuccess("redirect:/project/accept-invite-authenticated/confirm-invited-organisation");
        }
    }

    private RestResult<String> handleUserDoesNotExistYet() {
        return restSuccess("registration/project/accept-invite");
    }

    private RestResult<String> handleInviteAlreadyAccepted(HttpServletResponse response) {
        cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
        return restSuccess("redirect:/login");
    }
}
