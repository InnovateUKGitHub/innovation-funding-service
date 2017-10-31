package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.transactional.QuestionSetupService;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.setup.builder.SetupStatusResourceBuilder.newSetupStatusResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionSetupControllerTest extends BaseControllerMockMVCTest<QuestionSetupController> {

    private static final String BASE_URL = "/question/setup";

    @Mock
    private QuestionSetupService questionSetupService;

    @Override
    protected QuestionSetupController supplyControllerUnderTest() {
        return new QuestionSetupController();
    }

    @Test
    public void testMarkQuestionInSetupAsComplete() throws Exception {
        final Long questionId = 91L;
        final Long competitionId = 24L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;

        when(questionSetupService.markQuestionInSetupAsComplete(questionId, competitionId, parentSection)).thenReturn(serviceSuccess(newSetupStatusResource().build()));

        mockMvc.perform(put(BASE_URL + "/mark-as-complete/{competitionId}/{parentSection}/{questionId}", competitionId, parentSection, questionId))
            .andExpect(status().isOk());

        verify(questionSetupService, only()).markQuestionInSetupAsComplete(questionId, competitionId, parentSection);
    }

    @Test
    public void testMarkQuestionInSetupAsInComplete() throws Exception {
        final Long questionId = 91L;
        final Long competitionId = 24L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;

        when(questionSetupService.markQuestionInSetupAsIncomplete(questionId, competitionId, parentSection)).thenReturn(serviceSuccess(newSetupStatusResource().build()));

        mockMvc.perform(put(BASE_URL + "/mark-as-incomplete/{competitionId}/{parentSection}/{questionId}", competitionId, parentSection, questionId))
                .andExpect(status().isOk());

        verify(questionSetupService, only()).markQuestionInSetupAsIncomplete(questionId, competitionId, parentSection);
    }

    @Test
    public void testGetQuestionStatuses() throws Exception {
        final Long competitionId = 24L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.APPLICATION_FORM;
        final Map<Long, Boolean> resultMap = asMap(1L, Boolean.FALSE);

        when(questionSetupService.getQuestionStatuses(competitionId, parentSection)).thenReturn(serviceSuccess(resultMap));

        mockMvc.perform(get(BASE_URL + "/get-statuses/{competitionId}/{parentSection}", competitionId, parentSection))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(resultMap)));

        verify(questionSetupService, only()).getQuestionStatuses(competitionId, parentSection);
    }
}
