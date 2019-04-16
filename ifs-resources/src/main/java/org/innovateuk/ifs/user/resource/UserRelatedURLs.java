package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.commons.ZeroDowntime;

/**
 * Holder of URL strings for user with users
 */
@ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
public final class UserRelatedURLs {
    public static final String CAMEL_URL_CHECK_PASSWORD_RESET_HASH = "checkPasswordResetHash";
    public static final String CAMEL_URL_PASSWORD_RESET = "passwordReset";
    public static final String CAMEL_URL_SEND_PASSWORD_RESET_NOTIFICATION = "sendPasswordResetNotification";
    public static final String CAMEL_URL_VERIFY_EMAIL = "verifyEmail";
    public static final String CAMEL_URL_RESEND_EMAIL_VERIFICATION_NOTIFICATION = "resendEmailVerificationNotification";

    public static final String URL_CHECK_PASSWORD_RESET_HASH = "check-password-reset-hash";
    public static final String URL_PASSWORD_RESET = "password-reset";
    public static final String URL_SEND_PASSWORD_RESET_NOTIFICATION = "send-password-reset-notification";
    public static final String URL_VERIFY_EMAIL = "verify-email";
    public static final String URL_RESEND_EMAIL_VERIFICATION_NOTIFICATION = "resend-email-verification-notification";

    private UserRelatedURLs() {}
}
