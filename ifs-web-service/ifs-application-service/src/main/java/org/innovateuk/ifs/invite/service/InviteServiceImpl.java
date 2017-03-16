package org.innovateuk.ifs.invite.service;

import org.springframework.stereotype.Service;

/**
 * A service class for common invite methods.
 */
@Service
public class InviteServiceImpl implements InviteService {
    public static final String INVITE_ALREADY_ACCEPTED = "inviteAlreadyAccepted";
    public static final String INVITE_HASH = "invite_hash";
    public static final String ORGANISATION_TYPE = "organisationType";
}
