package org.innovateuk.ifs.starters.stubdev.security;

import org.innovateuk.ifs.commons.security.UidSupplier;

import javax.servlet.http.HttpServletRequest;

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
