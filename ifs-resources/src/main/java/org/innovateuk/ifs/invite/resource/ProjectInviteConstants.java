package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.ZeroDowntime;

/**
 * A helper class to organise different urls for various project invite actions
 */
@ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
public final class ProjectInviteConstants {
    public static final String CAMEL_PROJECT_INVITE_BASE_URL = "/projectinvite";
    public static final String CAMEL_PROJECT_INVITE_SAVE = "/saveInvite";
    public static final String CAMEL_CHECK_EXISTING_USER_URL = "/checkExistingUser/";
    public static final String CAMEL_GET_USER_BY_HASH_MAPPING = "/getUser/";
    public static final String CAMEL_GET_INVITE_BY_HASH = "/getProjectInviteByHash/";
    public static final String CAMEL_ACCEPT_INVITE = "/acceptInvite/";
    public static final String CAMEL_GET_PROJECT_INVITE_LIST = "/getInvitesByProjectId/";

    public static final String PROJECT_INVITE_BASE_URL = "/project-invite";
    public static final String PROJECT_INVITE_SAVE = "/save-invite";
    public static final String CHECK_EXISTING_USER_URL = "/check-existing-user/";
    public static final String GET_USER_BY_HASH_MAPPING = "/get-user/";
    public static final String GET_INVITE_BY_HASH = "/get-project-invite-by-hash/";
    public static final String ACCEPT_INVITE = "/accept-invite/";
    public static final String GET_PROJECT_INVITE_LIST = "/get-invites-by-project-id/";

    private ProjectInviteConstants() {}
}
