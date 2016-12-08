package com.worth.ifs.util;

import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.filter.CookieFlashMessageFilter;

import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class InviteUtil {
    public static final String INVITE_HASH = "invite_hash";
    public static final String ORGANISATION_TYPE = "organisationType";

    public static String handleAcceptedInvite(CookieFlashMessageFilter cookieFlashMessageFilter, HttpServletResponse response, CookieUtil cookieUtil) {
        cookieUtil.removeCookie(response, INVITE_HASH);
        cookieFlashMessageFilter.setFlashMessage(response, "inviteAlreadyAccepted");
        return "redirect:/login";
    }

    public static void handleInvalidInvite(HttpServletResponse response, CookieUtil cookieUtil) throws InvalidURLException {
        cookieUtil.removeCookie(response, INVITE_HASH);
        throw new InvalidURLException("Invite url is not valid", null);
    }
}
