package com.worth.ifs.application.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.QuestionStatusBuilder.newQuestionStatus;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionStatusControllerTest extends BaseControllerMockMVCTest<QuestionStatusController> {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    protected SectionController sectionController;

    @Override
    protected QuestionStatusController supplyControllerUnderTest() {
        return new QuestionStatusController();
    }

    @Test
    public void getNextQuestionFromOtherSectionTest() throws Exception {
      Application application = newApplication().withCompetition(newCompetition().build()).build();
      Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
      QuestionStatus questionStatus = newQuestionStatus().withApplication(application).build();
      List questionStatuses = new ArrayList<>();
      questionStatuses.add(questionStatus);
      when(questionStatusRepository.findByQuestionIdAndApplicationId(question.getId(), application.getId())).thenReturn(questionStatuses);

      mockMvc.perform(get("/questionStatus/findByQuestionAndAplication/1/2"))
          .andExpect(status().isOk())
          .andDo(document("questionStatus/findByQuestionAndAplication"));
    }
}
