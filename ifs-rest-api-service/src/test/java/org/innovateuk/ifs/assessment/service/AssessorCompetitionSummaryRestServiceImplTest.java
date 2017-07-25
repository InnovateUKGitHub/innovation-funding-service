package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.junit.Assert.assertEquals;

public class AssessorCompetitionSummaryRestServiceImplTest extends BaseRestServiceUnitTest<AssessorCompetitionSummaryRestServiceImpl> {

    @Override
    protected AssessorCompetitionSummaryRestServiceImpl registerRestServiceUnderTest() {
        return new AssessorCompetitionSummaryRestServiceImpl();
    }

    @Test
    public void getAssessorSummary() throws Exception {
        long assessorId = 1L;
        long competitionId = 2L;

        AssessorCompetitionSummaryResource expected = newAssessorCompetitionSummaryResource()
                .withCompetitionId(competitionId)
                .withCompetitionName("Test Competition")
                .build();

        setupGetWithRestResultExpectations(format("/assessor/%s/competition/%s/summary", assessorId, competitionId), AssessorCompetitionSummaryResource.class, expected);

        AssessorCompetitionSummaryResource actual = service.getAssessorSummary(assessorId, competitionId).getSuccessObjectOrThrowException();

        assertEquals(expected, actual);
    }
}
