package org.innovateuk.ifs.invite.resource;

/**
 * A helper class to organise different urls for various project invite actions
 */
public final class InviteProjectConstants {
    public static final String PROJECT_INVITE_BASE_URL = "/projectinvite";
    public static final String PROJECT_INVITE_SAVE = "/saveInvite";
    public static final String CHECK_EXISTING_USER_URL = "/checkExistingUser/";
    public static final String GET_USER_BY_HASH_MAPPING = "/getUser/";
    public static final String GET_INVITE_BY_HASH = "/getProjectInviteByHash/";
    public static final String ACCEPT_INVITE = "/acceptInvite/";
    public static final String GET_PROJECT_INVITE_LIST = "/getInvitesByProjectId/";

    private InviteProjectConstants() {}
}
