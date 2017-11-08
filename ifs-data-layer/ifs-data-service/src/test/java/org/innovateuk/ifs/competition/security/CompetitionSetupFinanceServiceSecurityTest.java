package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupFinanceService;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
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
            fail("Should not have been able to update finance section without a global role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }


    @Test
    public void testGetForCompetitionAllowedIfGlobalCompAdminOrProjectFinanceRole() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getForCompetition(1L), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void testGetForCompetitionAllowedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.getForCompetition(1L);
            fail("Should not have been able to get finance section without a global role");
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

        @Override
        public ServiceResult<CompetitionSetupFinanceResource> getForCompetition(Long competitionId) {
            return null;
        }
    }
}
