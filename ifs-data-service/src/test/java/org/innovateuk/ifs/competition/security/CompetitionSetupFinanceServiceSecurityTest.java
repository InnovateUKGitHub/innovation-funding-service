package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupQuestionService;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static freemarker.template.utility.Collections12.singletonList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;
import static org.junit.Assert.fail;

/**
 * Testing the permission rules applied to the secured methods in CompetitionSetupFinanceService.
 */
public class CompetitionSetupFinanceServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSetupFinanceService> {


    @Override
    protected Class<? extends CompetitionSetupFinanceService> getClassUnderTest() {
        return TestCompetitionSetupFinanceService.class;
    }

    @Test
    public void testSaveAllowedIfGlobalCompAdminOrProjectFinanceRole() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.save(newCompetitionSetupFinanceResource().build()), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void testSaveAllowedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.save(newCompetitionSetupFinanceResource().build());
            fail("Should not have been able to save question without a global role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestCompetitionSetupFinanceService implements CompetitionSetupFinanceService {

        @Override
        public ServiceResult<Void> save(CompetitionSetupFinanceResource competitionSetupFinanceResource) {
            return null;
        }
    }
}
