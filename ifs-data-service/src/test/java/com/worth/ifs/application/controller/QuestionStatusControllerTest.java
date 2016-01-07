package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.mapper.QuestionStatusMapper;
import com.worth.ifs.application.resource.QuestionStatusResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.QuestionStatusBuilder.newQuestionStatus;
import static com.worth.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionStatusControllerTest extends BaseControllerMockMVCTest<QuestionStatusController> {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    protected SectionController sectionController;

    @Mock
    protected QuestionStatusMapper questionStatusMapper;

    @Override
    protected QuestionStatusController supplyControllerUnderTest() {
        return new QuestionStatusController();
    }

    @Test
    public void getNextQuestionFromOtherSectionTest() throws Exception {
        Application application = newApplication().withCompetition(newCompetition().build()).build();

        QuestionStatus questionStatus = newQuestionStatus().withApplication(application).build();
        QuestionStatusResource questionStatusResource = newQuestionStatusResource().withApplication(application).build();

        List questionStatuses = new ArrayList<>();
        questionStatuses.add(questionStatus);

        List questionStatusResources = new ArrayList<>();
        questionStatusResources.add(questionStatusResource);

        when(questionStatusRepository.findByQuestionIdAndApplicationId(anyLong(), anyLong())).thenReturn(questionStatuses);
        when(questionStatusMapper.mapQuestionStatusToResource(questionStatus)).thenReturn(questionStatusResource);

        mockMvc.perform(get("/questionStatus/findByQuestionAndAplication/1/2"))
            .andExpect(status().isOk())
            .andExpect(content().string(new ObjectMapper().writeValueAsString(questionStatusResources)))
            .andDo(document("questionStatus/findByQuestionAndAplication"));
    }
}
