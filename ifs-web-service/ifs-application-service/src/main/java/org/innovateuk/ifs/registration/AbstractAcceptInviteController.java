package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseController;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.invite.service.InviteServiceImpl.INVITE_ALREADY_ACCEPTED;
import static org.innovateuk.ifs.invite.service.InviteServiceImpl.INVITE_HASH;
import static org.innovateuk.ifs.registration.OrganisationCreationController.ORGANISATION_FORM;



public class AbstractAcceptInviteController extends BaseController {

    @Autowired
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    protected CookieUtil cookieUtil;

    private static final String ALREADY_ACCEPTED_VIEW = "redirect:/login";
    protected static final String LOGGED_IN_WITH_ANOTHER_USER_VIEW = "registration/logged-in-with-another-user-failure";

    protected final String getInviteHashCookie(HttpServletRequest request){
        return cookieUtil.getCookieValue(request, InviteServiceImpl.INVITE_HASH);
    }

    protected final Runnable clearDownInviteFlowCookiesFn(HttpServletResponse response) {
        return () -> clearDownInviteFlowCookies(response);
    }

    protected final void clearDownInviteFlowCookies(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_FORM);
        cookieUtil.removeCookie(response, INVITE_HASH);
    }

    protected final void putInviteHashCookie(HttpServletResponse response, String hash) {
        cookieUtil.saveToCookie(response, INVITE_HASH, hash);
    }

    protected final String alreadyAcceptedView(HttpServletResponse response) {
        clearDownInviteFlowCookies(response);
        cookieFlashMessageFilter.setFlashMessage(response, INVITE_ALREADY_ACCEPTED);
        return ALREADY_ACCEPTED_VIEW;
    }

    protected final boolean loggedInAsNonInviteUser(ApplicationInviteResource invite, UserResource loggedInUser) {
        if (loggedInUser == null){
            return false;
        } else if (invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail())){
            return false;
        }
        return true;
    }
}
