package org.innovateuk.ifs.notifications.service;

import java.util.List;

public class WhiteBlackDomainFilter {

    /**
     * Given black and white email domains, determine if the email should be sent using the usual white/black logic.
     *
     * Blacklisting blocks by precedence
     * Case insensitive
     * An empty whitelist allows all
     * Match using string endsWith for partial domains. So foo@foo.ukri.org will match ukri.org
     *
     * @param whitelist white domains
     * @param blacklist black domains
     * @param email the email to check
     * @return true if the email passes and should send, false otherwise
     */
    public static boolean passesFilterCheck(List<String> whitelist, List<String> blacklist, String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String[] split = email.split("@");
        if (split == null || split.length != 2 || split[1].isEmpty()) {
            return false;
        }
        String emailDomain = split[1].toLowerCase();
        for (String item : blacklist) {
            if (emailDomain.endsWith(item.toLowerCase())) {
                return false;
            }
        }
        if (whitelist.isEmpty()) {
            return true;
        }
        for (String item : whitelist) {
            if (emailDomain.endsWith(item.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}
