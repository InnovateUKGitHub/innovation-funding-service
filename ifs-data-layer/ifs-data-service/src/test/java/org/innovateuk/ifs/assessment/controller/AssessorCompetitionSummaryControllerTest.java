package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorCompetitionSummaryControllerTest extends BaseControllerMockMVCTest<AssessorCompetitionSummaryController> {

    @Mock
    AssessorCompetitionSummaryService assessorCompetitionSummaryServiceMock;

    @Override
    protected AssessorCompetitionSummaryController supplyControllerUnderTest() {
        return new AssessorCompetitionSummaryController();
    }

    @Test
    public void getAssessorSummary() throws Exception {
        long assessorId = 1L;
        long competitionId = 2L;

        AssessorCompetitionSummaryResource assessorCompetitionSummaryResource = newAssessorCompetitionSummaryResource()
                .withCompetitionId(competitionId)
                .withCompetitionName("Test Competition")
                .build();

        when(assessorCompetitionSummaryServiceMock.getAssessorSummary(assessorId, competitionId))
                .thenReturn(serviceSuccess(assessorCompetitionSummaryResource));

        mockMvc.perform(get("/assessor/{assessorId}/competition/{competitionId}/summary", assessorId, competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(assessorCompetitionSummaryResource)));

        verify(assessorCompetitionSummaryServiceMock).getAssessorSummary(assessorId, competitionId);
    }
}
