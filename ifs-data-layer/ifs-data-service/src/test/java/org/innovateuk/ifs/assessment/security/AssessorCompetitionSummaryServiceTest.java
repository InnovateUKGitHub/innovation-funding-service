package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryService;
import org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryServiceImpl;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

public class AssessorCompetitionSummaryServiceTest extends BaseServiceSecurityTest<AssessorCompetitionSummaryService> {

    @Override
    protected Class<? extends AssessorCompetitionSummaryService> getClassUnderTest() {
        return AssessorCompetitionSummaryServiceImpl.class;
    }

    @Test
    public void getAllAssessorFormInputResponsesByAssessmentAndQuestion() {
        long assessorId = 1L;
        long competitionId = 3L;

        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getAssessorSummary(assessorId, competitionId),
                UserRoleType.COMP_ADMIN,
                UserRoleType.PROJECT_FINANCE
        );
    }
}
