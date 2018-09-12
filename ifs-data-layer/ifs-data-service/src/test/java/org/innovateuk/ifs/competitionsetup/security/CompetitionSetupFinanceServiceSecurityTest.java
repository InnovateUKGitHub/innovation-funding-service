package org.innovateuk.ifs.competitionsetup.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupFinanceServiceImpl;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.junit.Assert.fail;

/**
 * Testing the permission rules applied to the secured methods in CompetitionSetupFinanceService.
 */
public class CompetitionSetupFinanceServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSetupFinanceService> {


    @Override
    protected Class<? extends CompetitionSetupFinanceService> getClassUnderTest() {
        return CompetitionSetupFinanceServiceImpl.class;
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
}
