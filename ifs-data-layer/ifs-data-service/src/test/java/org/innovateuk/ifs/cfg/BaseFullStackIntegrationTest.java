package org.innovateuk.ifs.cfg;

import org.innovateuk.ifs.Application;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.config.audit.AuditConfig;
import org.innovateuk.ifs.config.repository.RefreshableCrudRepositoryImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

/**
 * A transactional spring boot test that enables full software stack testing (web to db).
 *
 * This will provide extremely good value test assurance.
 *
 * Extending this will -:
 *      Set up the database and jpa with required settings
 *      Enable jpa repositories and entities
 *      Audit setup
 *      Detailed sql logging
 *      A default spring security user (can be modified)
 *      Applies by virtue of @ActiveProfiles("integration-test") the application-integration-test.properties which
 *              has properties for setting up and enabling flyway among other things.
 *
 * Use case: You do not have a gui or want to avoid the extra ongoing cost of selenium/robot tests
 *
 * Usage: Aim to minimise mocking within the current module. External modules should be mocked.
 *
 * @ContextConfiguration(classes = {
 *     Foo.class,
 * })
 * public class FooTest extends BaseFullStackIntegrationTest
 *
 */
@Transactional
@SpringBootTest
@Import({AuditConfig.class})
@RunWith(SpringRunner.class)
@ActiveProfiles({"integration-test"})
@EntityScan(basePackageClasses = Application.class)
@EnableJpaRepositories(basePackageClasses = Application.class, repositoryBaseClass = RefreshableCrudRepositoryImpl.class)
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
public abstract class BaseFullStackIntegrationTest {

    /**
     * Apply the default logged in test user.
     */
    @PostConstruct
    public void initThreadContext() {
        // Start with a default case of an IFS_ADMINISTRATOR
        setLoggedInUser(Role.IFS_ADMINISTRATOR);
    }

    /**
     * Tests that require specific users can call this method to apply a given role.
     * @param role the required role.
     * @return the newly logged in user.
     */
    public final UserResource setLoggedInUser(Role role) {
        UserResource user = newUserResource().withRoleGlobal(role).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        return getLoggedInUser();
    }

    /**
     * Gets the logged in user.
     * @return the current logged in user.
     */
    public final UserResource getLoggedInUser() {
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getDetails();
    }
}