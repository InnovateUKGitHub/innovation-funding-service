package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Service interface for common invite methods.
 */
public interface InviteService {

    ApplicationInviteResource getInviteByRequest(HttpServletRequest request, HttpServletResponse response);
    ApplicationInviteResource getInviteByHash(String hash, HttpServletResponse response);

}
