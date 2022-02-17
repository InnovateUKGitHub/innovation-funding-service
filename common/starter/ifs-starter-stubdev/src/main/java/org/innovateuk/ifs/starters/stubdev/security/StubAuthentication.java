package org.innovateuk.ifs.starters.stubdev.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Stub implementation of an Authentication which itself is javax Principal
 */
public class StubAuthentication implements Authentication {

    private final List<GrantedAuthority> grantedAuthorities;
    private final String name;
    private final Long uid;

    public StubAuthentication(Long uid, String name, List<GrantedAuthority> grantedAuthorities) {
        this.uid = uid;
        this.name = name;
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return name + "->" + grantedAuthorities.toString();
    }

    @Override
    public Object getPrincipal() {
        return name;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // no-op
    }

    @Override
    public String getName() {
        return name;
    }
}
