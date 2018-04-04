package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryServiceImpl;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
