package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.error.exception.InvalidURLException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO qqRP can this class go?
 * A service class for common invite methods.
 */
@Service
public class InviteServiceImpl implements InviteService {
    public static final String INVITE_ALREADY_ACCEPTED = "inviteAlreadyAccepted";
    public static final String INVITE_HASH = "invite_hash";
    public static final String ORGANISATION_TYPE = "organisationType";

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Override
    public ApplicationInviteResource getInviteByRequest(HttpServletRequest request, HttpServletResponse response) {
        String hash = cookieUtil.getCookieValue(request, INVITE_HASH);
        return getInviteByHash(hash, response);
    }

    @Override
    public ApplicationInviteResource getInviteByHash(String hash, HttpServletResponse response) {
        RestResult<ApplicationInviteResource> result = inviteRestService.getInviteByHash(hash);
        if(result.isSuccess()) {
            removeCookiesIfInviteNotSent(result.getSuccessObject(), response);
            return result.getSuccessObject();
        } else {
            cookieUtil.removeCookie(response, INVITE_HASH);
            throw new InvalidURLException("Invite url is not valid", null);
        }
    }

    private void removeCookiesIfInviteNotSent(ApplicationInviteResource invite, HttpServletResponse response) {
        if (!InviteStatus.SENT.equals(invite.getStatus())) {
            cookieUtil.removeCookie(response, INVITE_HASH);
            cookieFlashMessageFilter.setFlashMessage(response, INVITE_ALREADY_ACCEPTED);
        }
    }
}
