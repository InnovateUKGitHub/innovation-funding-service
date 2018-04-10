package org.innovateuk.ifs.question.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.question.QuestionSetupCompetitionController;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionSetupCompetitionControllerTest extends BaseControllerMockMVCTest<QuestionSetupCompetitionController> {

    @Override
    protected QuestionSetupCompetitionController supplyControllerUnderTest() {
        return new QuestionSetupCompetitionController();
    }

    @Test
    public void getByQuestionId() throws Exception {
        final Long questionId = 1L;

        when(questionSetupServiceMock.getByQuestionId(questionId)).thenReturn(serviceSuccess(newCompetitionSetupQuestionResource().build()));

        mockMvc.perform(get("/question/getById/{questionId}", questionId))
                .andExpect(status().isOk());

        verify(questionSetupServiceMock, only()).getByQuestionId(questionId);
    }

    @Test
    public void save() throws Exception {
        final CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource().build();

        when(questionSetupServiceMock.update(question)).thenReturn(serviceSuccess(newCompetitionSetupQuestionResource().withTitle("expected question").build()));

        mockMvc.perform(put("/question/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isOk());

        verify(questionSetupServiceMock, only()).update(question);
    }

    @Test
    public void addDefaultToCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(questionSetupServiceMock.createByCompetitionId(competitionId)).thenReturn(serviceSuccess(newCompetitionSetupQuestionResource().build()));

        mockMvc.perform(post("/question/addDefaultToCompetition/{competitionId}", competitionId))
                .andExpect(status().isCreated());

        verify(questionSetupServiceMock, only()).createByCompetitionId(competitionId);
    }

    @Test
    public void deleteById() throws Exception {
        final Long questionId = 1L;

        when(questionSetupServiceMock.delete(questionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/question/deleteById/{questionId}", questionId))
                .andExpect(status().isNoContent());

        verify(questionSetupServiceMock, only()).delete(questionId);
    }
}