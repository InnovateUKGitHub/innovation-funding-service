package org.innovateuk.ifs.starters.stubdev.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
public class TestComponent {

    @PreAuthorize("hasAuthority('foooo')")
    public void authRoleFail() {
        throw new RuntimeException("ssss");
    }

    @PreAuthorize("isAuthenticated()")
    public void auth() {
        //ok
    }

    @PreAuthorize("hasAuthority('TEST-ROLE')")
    public void authRoleOk() {
        //ok
    }

}
