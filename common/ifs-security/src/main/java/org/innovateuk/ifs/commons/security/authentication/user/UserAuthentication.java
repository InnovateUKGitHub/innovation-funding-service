package org.innovateuk.ifs.commons.security.authentication.user;

import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Representation of an authenticated user, where its roles, name,etc can be obtained.
 */
public class UserAuthentication implements Authentication {


    private final transient UserResource user;
    private boolean authenticated = true;

    public UserAuthentication(UserResource user) {
        this.user = user;
    }

    @Override
    public String getName() {
        if(user != null) {
            return user.getName();
        } else {
            //Spring boot actuator authorisation failures as audit events (authorizationAuditListener.onAuthorizationFailureEvent()
            //the unit tests e.g. CompetitionServiceSecurityTest.testGetCompetitionById() was failing when supplied with a null user
            // hence the addition of this else statement.
            return null;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (user == null) {
            return new ArrayList<>();
        }

        return user.getRoles().stream()
                .flatMap(role -> role.getAuthorities().stream())
                .map(Authority::toSpringSecurityAuthorityString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Object getPrincipal() {
        return user.getName();
    }

    @Override
    public UserResource getDetails() {
        return user;
    }

    @Override
    public Object getCredentials() {
        return user.getUid();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
