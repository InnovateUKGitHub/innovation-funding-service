package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;


public class AbstractAcceptInviteController {

    private static final String ALREADY_ACCEPTED_VIEW = "redirect:/login";

    public static final String INVITE_ALREADY_ACCEPTED = "inviteAlreadyAccepted";

    @Autowired
    protected CookieFlashMessageFilter cookieFlashMessageFilter;

    @Autowired
    protected RegistrationCookieService registrationCookieService;

    protected static final String LOGGED_IN_WITH_ANOTHER_USER_VIEW = "registration/logged-in-with-another-user-failure";

    protected final Runnable clearDownInviteFlowCookiesFn(HttpServletResponse response) {
        return () -> clearDownInviteFlowCookies(response);
    }

    protected final void clearDownInviteFlowCookies(HttpServletResponse response) {
        registrationCookieService.deleteOrganisationCreationCookie(response);
        registrationCookieService.deleteOrganisationIdCookie(response);
        registrationCookieService.deleteInviteHashCookie(response);
    }

    protected final String alreadyAcceptedView(HttpServletResponse response) {
        clearDownInviteFlowCookies(response);
        cookieFlashMessageFilter.setFlashMessage(response, INVITE_ALREADY_ACCEPTED);
        return ALREADY_ACCEPTED_VIEW;
    }

    protected final boolean loggedInAsNonInviteUser(ApplicationInviteResource invite, UserResource loggedInUser) {
        if (loggedInUser == null || invite.getEmail().equalsIgnoreCase(loggedInUser.getEmail())){
            return false;
        }
        return true;
    }
}
