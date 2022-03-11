package org.innovateuk.ifs.starters.stubdev.security;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.starters.stubdev.IfsStubDevAutoConfiguration;
import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles(IfsProfileConstants.STUBDEV)
@EnableConfigurationProperties(StubDevConfigurationProperties.class)
@SpringBootTest(classes = {TestComponent.class, IfsStubDevAutoConfiguration.class, StubUserSwitchController.class, StubSecurityConfig.class})
@ExtendWith(SpringExtension.class)
public class AuthComponentTest {

    @Autowired
    public TestComponent testComponent;

    @Test
    void testAuthRoleFail() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThrows(AuthenticationException.class, () -> testComponent.authRoleOk());
        assertThrows(AuthenticationException.class, () -> testComponent.authRoleFail());
        assertThrows(AuthenticationException.class, () -> testComponent.auth());
    }

    @Test
    void testAuth() {
        SecurityContextHolder.getContext().setAuthentication(testUser());
        testComponent.auth();
        testComponent.authRoleOk();
        assertThrows(AccessDeniedException.class, () -> testComponent.authRoleFail());
    }

    private Authentication testUser() {
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return ImmutableList.of(new SimpleGrantedAuthority("TEST-ROLE"));
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return "unset";
            }
        };
    }

}
