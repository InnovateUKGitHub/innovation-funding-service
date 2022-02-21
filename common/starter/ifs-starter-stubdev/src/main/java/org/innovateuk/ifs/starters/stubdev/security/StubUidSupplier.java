package org.innovateuk.ifs.starters.stubdev.security;

import org.innovateuk.ifs.commons.security.UidSupplier;

import javax.servlet.http.HttpServletRequest;

public class StubUidSupplier implements UidSupplier {

    @Override
    public String getUid(HttpServletRequest request) {
        return "79320212-3272-4f86-bf60-cb406c27c0f8";
    }
}
