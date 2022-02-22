package org.innovateuk.ifs.starters.stubdev.security;

import org.innovateuk.ifs.commons.security.UidSupplier;

import javax.servlet.http.HttpServletRequest;

/**
 * Light touch for overriding the default security implementation.
 *
 * This overrides the default UidSupplier in stub mode and returns the default or user specified uuid.
 *
 * This will get bound the SecurityContext by the existing security setup and needs to match a DB entry.
 */
public class StubUidSupplier implements UidSupplier {

    private String currentUserUuid = "unset";

    @Override
    public String getUid(HttpServletRequest request) {
        return currentUserUuid;
    }

    protected void setUuid(String userUuid) {
        this.currentUserUuid = userUuid;
    }
}
