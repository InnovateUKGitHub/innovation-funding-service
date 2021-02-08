package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.transactional.AssessmentPeriodService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder.newAssessmentPeriodResource;

public class AssessmentPeriodControllerTest extends BaseControllerMockMVCTest<AssessmentPeriodController> {

    @Mock
    private AssessmentPeriodService assessmentPeriodService;

    @Override
    protected AssessmentPeriodController supplyControllerUnderTest() {
        return new AssessmentPeriodController();
    }

    @Test
    public void getAssessmentPeriodByCompetitionIdAndIndex_returnsSuccess() throws Exception {

        long competitionId = 1L;
        int index = 1;

        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource().build();

        when(assessmentPeriodService.getAssessmentPeriodByCompetitionIdAndIndex(competitionId, index))
                .thenReturn(ServiceResult.serviceSuccess(assessmentPeriodResource));

        mockMvc.perform(get("/assessment-period/{competitionId}/get-by-index", competitionId)
                .param("index", String.valueOf(index)))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(assessmentPeriodResource)));
    }

    @Test
    public void getAssessmentPeriodByCompetitionIdAndIndex_returnsFailure() throws Exception {

        long competitionId = 1L;
        int index = 1;

        when(assessmentPeriodService.getAssessmentPeriodByCompetitionIdAndIndex(competitionId, index))
                .thenReturn(ServiceResult.serviceFailure(notFoundError(AssessmentPeriodResource.class, competitionId, index)));

        mockMvc.perform(get("/assessment-period/{competitionId}/get-by-index", competitionId)
                .param("index", String.valueOf(index)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void create_returnsSuccess() throws Exception {

        long competitionId = 1L;
        int index = 1;

        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource()
                .withCompetitionId(competitionId)
                .withIndex(index)
                .build();

        when(assessmentPeriodService.create(competitionId, index))
                .thenReturn(ServiceResult.serviceSuccess(assessmentPeriodResource));

        mockMvc.perform(post("/assessment-period/{competitionId}", competitionId)
                .param("index", String.valueOf(index)))
                .andExpect(status().isCreated())
                .andExpect(content().json(toJson(assessmentPeriodResource)));
    }

    @Test
    public void create_returnsFailure() throws Exception {

        long competitionId = 1L;
        int index = 1;

        when(assessmentPeriodService.create(competitionId, index))
                .thenReturn(ServiceResult.serviceFailure(
                        new Error(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)));

        mockMvc.perform(post("/assessment-period/{competitionId}", competitionId)
                .param("index", String.valueOf(index)))
                .andExpect(status().isInternalServerError());
    }
}
