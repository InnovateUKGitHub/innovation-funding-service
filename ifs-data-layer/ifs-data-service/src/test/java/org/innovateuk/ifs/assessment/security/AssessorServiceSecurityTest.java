package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.assessment.transactional.AssessorServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class AssessorServiceSecurityTest extends BaseServiceSecurityTest<AssessorService> {

    @Override
    protected Class<? extends AssessorService> getClassUnderTest() {
        return AssessorServiceImpl.class;
    }

    @Test
    public void notifyAssessorsByCompetition() {
        long competitionId = 1L;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.notifyAssessorsByCompetition(competitionId), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void hasApplicationsAssigned() {
        long userId = 1L;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.hasApplicationsAssigned(userId), COMP_ADMIN, PROJECT_FINANCE);
    }
}
