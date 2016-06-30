package com.worth.ifs.assessment.controller;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
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
        List<AssessmentFeedbackResource> listOfAssessmentFeedback = newAssessmentFeedbackResource().build(2);

        when(assessmentFeedbackService.getAllAssessmentFeedback(ASSESSMENT_ID))
                  .thenReturn(listOfAssessmentFeedback);

        final MvcResult result = mockMvc.perform(get("/assessment/summary/{assessmentId}",ASSESSMENT_ID))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-application-summary"))
                .andReturn();
        List<AssessmentSummaryViewModel> model = (List<AssessmentSummaryViewModel>)result.getModelAndView().getModel().get("model");
        Assert.assertTrue(model.size()==2);
    }

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }
}
