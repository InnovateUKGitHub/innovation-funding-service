package org.innovateuk.ifs.invite.resource;

/**
 * A helper class to organise different urls for various project invite actions
 */
public final class ProjectInviteConstants {

    public static final String PROJECT_INVITE_BASE_URL = "/project-invite";
    public static final String PROJECT_INVITE_SAVE = "/save-invite";
    public static final String CHECK_EXISTING_USER_URL = "/check-existing-user/";
    public static final String GET_USER_BY_HASH_MAPPING = "/get-user/";
    public static final String GET_INVITE_BY_HASH = "/get-project-invite-by-hash/";
    public static final String ACCEPT_INVITE = "/accept-invite/";
    public static final String GET_PROJECT_INVITE_LIST = "/get-invites-by-project-id/";

    private ProjectInviteConstants() {}
}
