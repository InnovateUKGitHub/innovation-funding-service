package org.innovateuk.ifs.question.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.question.transactional.QuestionSetupCompetitionService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionSetupCompetitionControllerTest extends BaseControllerMockMVCTest<QuestionSetupCompetitionController> {

    @Mock
    protected QuestionSetupCompetitionService questionSetupCompetitionServiceMock;

    @Override
    protected QuestionSetupCompetitionController supplyControllerUnderTest() {
        return new QuestionSetupCompetitionController();
    }

    @Test
    public void getByQuestionId() throws Exception {
        final Long questionId = 1L;

        when(questionSetupCompetitionServiceMock.getByQuestionId(questionId)).thenReturn(serviceSuccess(newCompetitionSetupQuestionResource().build()));

        mockMvc.perform(get("/question-setup/getById/{questionId}", questionId))
                .andExpect(status().isOk());

        verify(questionSetupCompetitionServiceMock, only()).getByQuestionId(questionId);
    }

    @Test
    public void save() throws Exception {
        final CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource().build();

        when(questionSetupCompetitionServiceMock.update(question)).thenReturn(serviceSuccess(newCompetitionSetupQuestionResource().withTitle("expected question").build()));

        mockMvc.perform(put("/question-setup/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isOk());

        verify(questionSetupCompetitionServiceMock, only()).update(question);
    }

    @Test
    public void addDefaultToCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(questionSetupCompetitionServiceMock.createByCompetitionId(competitionId)).thenReturn(serviceSuccess(newCompetitionSetupQuestionResource().build()));

        mockMvc.perform(post("/question-setup/addDefaultToCompetition/{competitionId}", competitionId))
                .andExpect(status().isCreated());

        verify(questionSetupCompetitionServiceMock, only()).createByCompetitionId(competitionId);
    }

    @Test
    public void deleteById() throws Exception {
        final Long questionId = 1L;

        when(questionSetupCompetitionServiceMock.delete(questionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/question-setup/deleteById/{questionId}", questionId))
                .andExpect(status().isNoContent());

        verify(questionSetupCompetitionServiceMock, only()).delete(questionId);
    }
}