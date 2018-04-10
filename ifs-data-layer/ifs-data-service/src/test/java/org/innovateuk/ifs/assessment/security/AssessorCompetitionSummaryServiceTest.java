package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryService;
import org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

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
                COMP_ADMIN,
                PROJECT_FINANCE
        );
    }
}
