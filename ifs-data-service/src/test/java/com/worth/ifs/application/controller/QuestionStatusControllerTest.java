package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.transactional.QuestionService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionStatusControllerTest extends BaseControllerMockMVCTest<QuestionStatusController> {

    @Mock
    protected QuestionService questionService;

    @Override
    protected QuestionStatusController supplyControllerUnderTest() {
        return new QuestionStatusController();
    }

    @Test
    public void getNextQuestionFromOtherSectionTest() throws Exception {
        ApplicationResource applicationResource = newApplicationResource().withCompetition(newCompetitionResource().build().getId()).build();
        QuestionStatusResource questionStatus = newQuestionStatusResource().withApplication(applicationResource).build();
        List<QuestionStatusResource> questionStatuses = singletonList(questionStatus);

        when(questionService.getQuestionStatusByApplicationIdAndAssigneeId(1L, 2L)).thenReturn(serviceSuccess(questionStatuses));

        mockMvc.perform(get("/questionStatus/findByQuestionAndApplication/1/2"))
            .andExpect(status().isOk())
            .andExpect(content().string(new ObjectMapper().writeValueAsString(questionStatuses)))
            .andDo(document("questionStatus/findByQuestionAndApplication"));
    }
}
