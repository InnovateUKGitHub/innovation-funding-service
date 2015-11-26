package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.domain.UserRoleType.APPLICANT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Testing the security annotations on the ApplicationService
 */
public class ApplicationServiceImplSecurityTest extends BaseServiceSecurityTest<ApplicationService> {

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_allowedIfGlobalApplicationRole() {

        setLoggedInUser(newUser().withRolesGlobal(newRole().withType(APPLICANT).build()).build());
        Application newApplication = service.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
        assertEquals("An application", newApplication.getName());
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotLoggedIn() {

        setLoggedInUser(null);
        try {
            service.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
            fail("Should not have been able to create an Application without first logging in");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNoGlobalRolesAtAll() {

        try {
            service.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
            fail("Should not have been able to create an Application without the global Applicant role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotCorrectGlobalRoles() {

        List<UserRoleType> nonApplicantRoles = asList(UserRoleType.values()).stream().filter(type -> type != APPLICANT).collect(toList());

        nonApplicantRoles.forEach(role -> {

            setLoggedInUser(newUser().withRolesGlobal(newRole().withType(role).build()).build());

            try {
                service.createApplicationByApplicationNameForUserIdAndCompetitionId("An application", 123L, 456L);
                fail("Should not have been able to create an Application without the global Applicant role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Override
    protected Class<? extends ApplicationService> getServiceClass() {
        return TestApplicationService.class;
    }

    private static class TestApplicationService implements ApplicationService {

        @Override
        public Application createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName, Long competitionId, Long userId) {
            return newApplication().with(name(applicationName)).build();
        }
    }
}
