package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.builder.ApplicationResourceBuilder;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.builder.AssessmentResourceBuilder;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentRestService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentSummaryViewModel;
import com.worth.ifs.competition.builder.CompetitionResourceBuilder;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.builder.ProcessRoleResourceBuilder;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentControllerTest extends BaseControllerMockMVCTest<AssessmentController> {

    private MockMvc mvc;

    private static final Long ASSESSMENT_ID = 1L;

    @Mock
    private AssessmentFeedbackService assessmentFeedbackService;

    @Mock
    private AssessmentService assessmentService;
    @Mock
    private AssessmentRestService assessmentRestService;

    @Test
    public void testGetSummary() throws Exception {
        List<AssessmentFeedbackResource> listOfAssessmentFeedback = newAssessmentFeedbackResource().build(2);
        List<QuestionResource> listOfQuestion = newQuestionResource().build(2);
        listOfQuestion.get(0).setQuestionNumber("1");
        listOfQuestion.get(0).setSection(2L);
        listOfQuestion.get(1).setQuestionNumber("2");
        listOfQuestion.get(1).setSection(2L);
        listOfAssessmentFeedback.get(0).setQuestion(listOfQuestion.get(0).getId());
        listOfAssessmentFeedback.get(1).setQuestion(listOfQuestion.get(1).getId());


        final AssessmentResource assessmentResource = AssessmentResourceBuilder.newAssessmentResource().build();
        final ProcessRoleResource processRoleResource = ProcessRoleResourceBuilder.newProcessRoleResource().build();
        final ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource().build();
        final CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource().build();


        when(assessmentFeedbackService.getAllAssessmentFeedback(ASSESSMENT_ID))
                .thenReturn(listOfAssessmentFeedback);
        when(assessmentService.getAllQuestionsById(ASSESSMENT_ID)).thenReturn(listOfQuestion);

        when(assessmentService.getById(ASSESSMENT_ID)).thenReturn(assessmentResource);
        when(processRoleService.getById(assessmentResource.getProcessRole())).thenReturn(settable(processRoleResource));
        when(applicationService.getById(processRoleResource.getApplication())).thenReturn(applicationResource);
        when(competitionService.getById(applicationResource.getCompetition())).thenReturn(competitionResource);

        final MvcResult result = mockMvc.perform(get("/assessment/{assessmentId}/summary", ASSESSMENT_ID))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-application-summary"))
                .andReturn();
        AssessmentSummaryViewModel model = (AssessmentSummaryViewModel) result.getModelAndView().getModel().get("model");

        Assert.assertTrue(model.getListOfQuestionWithFeedback().size() == 2);
        Assert.assertTrue(model.getApplicationResource().equals(applicationResource));
        Assert.assertTrue(model.getCompetitionResource().equals(competitionResource));
        Assert.assertTrue(model.getMaxScore() == 10);
        Assert.assertTrue(model.getAssessmentId() == ASSESSMENT_ID);
    }

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }
}
