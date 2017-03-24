package org.innovateuk.ifs.commons;

import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.test.BaseTest;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This is the base class for all integration tests against a configured Spring application.  This superclass also
 * provides a running server against which tests can be run, or can simply be used as a non-web-based integration
 * platform with a full stack available for autowiring.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration-test")
public abstract class BaseIntegrationTest extends BaseTest {

    @LocalServerPort
    protected int port;

    /**
     * Set a user on the Spring Security ThreadLocals
     *
     * @param user
     */
    public static UserResource setLoggedInUser(UserResource user) {
        UserResource currentUser = getLoggedInUser();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        return currentUser;
    }

    /**
     * Get the user on the Spring Security ThreadLocals
     */
    public static UserResource getLoggedInUser() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null) {
            return null;
        }

        if (!(context.getAuthentication() instanceof UserAuthentication)) {
            return null;
        }

        UserAuthentication authentication = (UserAuthentication) context.getAuthentication();

        if (authentication == null) {
            return null;
        }

        return authentication.getDetails();
    }

}
