package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.period.controller.AssessmentPeriodController;
import org.innovateuk.ifs.assessment.period.transactional.AssessmentPeriodService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder.newAssessmentPeriodResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentPeriodControllerTest extends BaseControllerMockMVCTest<AssessmentPeriodController> {

    @Mock
    private AssessmentPeriodService assessmentPeriodService;

    @Override
    protected AssessmentPeriodController supplyControllerUnderTest() {
        return new AssessmentPeriodController();
    }

    @Test
    public void getAssessmentPeriodByCompetitionId_returnsSuccess() throws Exception {

        long competitionId = 1L;

        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource().build();

        when(assessmentPeriodService.getAssessmentPeriodByCompetitionId(competitionId))
                .thenReturn(ServiceResult.serviceSuccess(Collections.singletonList(assessmentPeriodResource)));

        mockMvc.perform(get("/assessment-period?competitionId={competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(Collections.singletonList(assessmentPeriodResource))));
    }

    @Test
    public void getAssessmentPeriodByCompetitionId_returnsFailure() throws Exception {

        long competitionId = 1L;
        int index = 1;

        when(assessmentPeriodService.getAssessmentPeriodByCompetitionId(competitionId))
                .thenReturn(ServiceResult.serviceFailure(notFoundError(AssessmentPeriodResource.class, competitionId, index)));

        mockMvc.perform(get("/assessment-period?competitionId={competitionId}", competitionId))
                .andExpect(status().isNotFound());
    }

}
