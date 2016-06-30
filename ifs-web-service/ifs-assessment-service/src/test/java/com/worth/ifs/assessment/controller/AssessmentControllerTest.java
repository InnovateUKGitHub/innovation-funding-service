package com.worth.ifs.assessment.controller;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.builder.QuestionResourceBuilder;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentSummaryViewModel;
import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentControllerTest extends BaseControllerMockMVCTest<AssessmentController> {

    private MockMvc mvc;

    private static final Long ASSESSMENT_ID = 1L;

    @Mock
    private AssessmentFeedbackService assessmentFeedbackService;

    @Mock
    private AssessmentService assessmentService;

    @Test
    public void testGetAllQuestionsOfGivenAssessment() throws Exception{
        final String expectedValue = "Blah";
        final Integer expectedScore = 10;
        QuestionResource questionResource = QuestionResourceBuilder.newQuestionResource().build();
        List<QuestionResource> listOfQuestions = new ArrayList<>();
        Long questionId = questionResource.getId();
        listOfQuestions.add(questionResource);
        when(assessmentService.getAllQuestionsById(ASSESSMENT_ID )).thenReturn(listOfQuestions);
        when(assessmentFeedbackService.getAssessmentFeedbackByAssessmentAndQuestion(ASSESSMENT_ID,questionId))
                  .thenReturn(newAssessmentFeedbackResource()
                          .withFeedback(expectedValue)
                          .withScore(expectedScore)
                          .build());

        final MvcResult result = mockMvc.perform(get("/assessment/summary/{assessmentId}",ASSESSMENT_ID))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-application-summary"))
                .andReturn();
        AssessmentSummaryViewModel model = (AssessmentSummaryViewModel)result.getModelAndView().getModel().get("model");
        Assert.assertTrue(model.getListOfAssessmentFeedback().size()==1);

    }

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }
}
