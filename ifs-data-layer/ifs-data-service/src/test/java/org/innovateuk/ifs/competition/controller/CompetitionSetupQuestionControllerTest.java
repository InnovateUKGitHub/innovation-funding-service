package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupQuestionControllerTest extends BaseControllerMockMVCTest<CompetitionSetupQuestionController> {

    @Override
    protected CompetitionSetupQuestionController supplyControllerUnderTest() {
        return new CompetitionSetupQuestionController();
    }

    @Test
    public void getByQuestionId() throws Exception {
        final Long questionId = 1L;

        when(competitionSetupQuestionServiceMock.getByQuestionId(questionId)).thenReturn(serviceSuccess(newCompetitionSetupQuestionResource().build()));

        mockMvc.perform(get("/competition-setup-question/getById/{questionId}", questionId))
                .andExpect(status().isOk());

        verify(competitionSetupQuestionServiceMock, only()).getByQuestionId(questionId);
    }

    @Test
    public void save() throws Exception {
        final CompetitionSetupQuestionResource question = newCompetitionSetupQuestionResource().build();

        when(competitionSetupQuestionServiceMock.update(question)).thenReturn(serviceSuccess(newCompetitionSetupQuestionResource().withTitle("expected question").build()));

        mockMvc.perform(put("/competition-setup-question/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isOk());

        verify(competitionSetupQuestionServiceMock, only()).update(question);
    }

    @Test
    public void addDefaultToCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(competitionSetupQuestionServiceMock.createByCompetitionId(competitionId)).thenReturn(serviceSuccess(newCompetitionSetupQuestionResource().build()));

        mockMvc.perform(post("/competition-setup-question/addDefaultToCompetition/{competitionId}", competitionId))
                .andExpect(status().isCreated());

        verify(competitionSetupQuestionServiceMock, only()).createByCompetitionId(competitionId);
    }

    @Test
    public void deleteById() throws Exception {
        final Long questionId = 1L;

        when(competitionSetupQuestionServiceMock.delete(questionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/competition-setup-question/deleteById/{questionId}", questionId))
                .andExpect(status().isNoContent());

        verify(competitionSetupQuestionServiceMock, only()).delete(questionId);
    }
}