package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessorCompetitionSummaryServiceTest extends BaseServiceSecurityTest<AssessorCompetitionSummaryService> {

    @Override
    protected Class<? extends AssessorCompetitionSummaryService> getClassUnderTest() {
        return TestAssessorCompetitionSummaryService.class;
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

    static class TestAssessorCompetitionSummaryService implements AssessorCompetitionSummaryService {

        @Override
        public ServiceResult<AssessorCompetitionSummaryResource> getAssessorSummary(long assessorId, long competitionId) {
            return null;
        }
    }
}
