package org.innovateuk.ifs.notifications.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WhiteBlackDomainFilter {

    private static final String WILDCARD = "*";

    @Value("#{'${ifs.emailNotification.whitelist}'.split(',')}")
    protected List<String> whitelist;

    @Value("#{'${ifs.emailNotification.blacklist}'.split(',')}")
    protected List<String> blacklist;

    /**
     * Determine if the email should be sent using the usual white/black logic.
     *
     * Blacklisting blocks by precedence
     * Case insensitive
     * A WILDCARD whitelist allows all
     * Match using string endsWith for partial domains. So foo@subdomain.ukri.org will match ukri.org
     *
     * @param email the email to check
     * @return true if the email passes and should send, false otherwise
     */
    public boolean passesFilterCheck(String email) {
        if (!isValidEmail(email)) {
            return false;
        }
        String emailDomain = email.split("@")[1].toLowerCase();
        for (String item : blacklist) {
            if (emailDomain.endsWith(item.toLowerCase())) {
                return false;
            }
        }
        if (isWilcardWhitelist()) {
            return true;
        }
        for (String item : whitelist) {
            if (emailDomain.endsWith(item.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean isWilcardWhitelist() {
        if (whitelist.size() == 1 && whitelist.get(0).equals(WILDCARD)) {
            return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String[] split = email.split("@");
        if (split == null || split.length != 2 || split[1].isEmpty()) {
            return false;
        }
        return true;
    }

    protected void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    protected void setBlacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }
}
