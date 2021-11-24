package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessorCompetitionSummaryController;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.assessment.documentation.AssessorCompetitionSummaryResourceDocs.assessorCompetitionSummaryResourceBuilder;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorCompetitionSummaryControllerDocumentation extends BaseControllerMockMVCTest<AssessorCompetitionSummaryController> {

    @Mock
    private AssessorCompetitionSummaryService assessorCompetitionSummaryServiceMock;

    @Override
    protected AssessorCompetitionSummaryController supplyControllerUnderTest() {
        return new AssessorCompetitionSummaryController();
    }

    @Test
    public void getAssessorSummary() throws Exception {
        long assessorId = 1L;
        long competitionId = 2L;

        AssessorCompetitionSummaryResource resource = assessorCompetitionSummaryResourceBuilder.build();

        when(assessorCompetitionSummaryServiceMock.getAssessorSummary(assessorId, competitionId))
                .thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/assessor/{assessorId}/competition/{competitionId}/summary", assessorId, competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }
}
