package com.worth.ifs.commons;

import com.worth.ifs.Application;
import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.BaseWebIntegrationTest;
import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.user.resource.UserResource;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

/**
 * This is the base class for all integration tests against a configured Spring application.  Subclasses of this base can be
 * of the form of either integration tests with a running server ({@link BaseWebIntegrationTest}) or without
 * (e.g. {@link BaseRepositoryIntegrationTest}).
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("integration-test")
public abstract class BaseIntegrationTest extends BaseTest {

    public static final int USER_COUNT  = 17;
    public static final List<String> ALL_USERS_EMAIL = Arrays.asList("steve.smith@empire.com", "jessica.doe@ludlow.co.uk", "paul.plum@gmail.com", "competitions@innovateuk.gov.uk", "finance@innovateuk.gov.uk", "pete.tom@egg.com", "felix.wilson@gmail.com", "ewan+1@hiveit.co.uk", "ifs_web_user@innovateuk.org", "compadmin@innovateuk.test", "comp_exec1@innovateuk.test", "comp_exec2@innovateuk.test", "comp_technologist1@innovateuk.test", "comp_technologist2@innovateuk.test", "ifsadmin@innovateuk.test");

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