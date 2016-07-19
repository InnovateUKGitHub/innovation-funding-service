package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.model.AssessmentApplicationSummaryModelPopulator;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.service.AssessorFormInputResponseService;
import com.worth.ifs.assessment.viewmodel.AssessmentApplicationSummaryQuestionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentApplicationSummaryViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentApplicationSummaryControllerTest extends BaseControllerMockMVCTest<AssessmentApplicationSummaryController> {

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Spy
    @InjectMocks
    private AssessmentApplicationSummaryModelPopulator assessmentApplicationSummaryModelPopulator;

    @Override
    protected AssessmentApplicationSummaryController supplyControllerUnderTest() {
        return new AssessmentApplicationSummaryController();
    }

    @Test
    public void testGetSummary() throws Exception {
        Long competitionId = 1L;
        Long processRoleId = 2L;
        Long applicationId = 3L;
        Long assessmentId = 4L;

        when(assessmentService.getById(assessmentId)).thenReturn(newAssessmentResource()
                .withProcessRole(processRoleId)
                .build());

        when(processRoleService.getById(processRoleId)).thenReturn(settable(newProcessRoleResource()
                .withApplication(applicationId)
                .build()));

        ApplicationResource expectedApplication = newApplicationResource()
                .withCompetition(competitionId)
                .build();
        when(applicationService.getById(applicationId)).thenReturn(expectedApplication);

        CompetitionResource expectedCompetition = newCompetitionResource()
                .withAssessmentStartDate(now().minusDays(2))
                .withAssessmentEndDate(now().plusDays(4))
                .build();
        when(competitionService.getById(competitionId)).thenReturn(expectedCompetition);

        List<AssessorFormInputResponseResource> assessorResponses = newAssessorFormInputResponseResource()
                .withQuestion(1L, 2L)
                .build(2);

        List<QuestionResource> questions = newQuestionResource()
                .withId(1L, 2L)
                .withSection(2L, 2L)
                .withQuestionNumber("1", "2")
                .withShortName("Business opportunity", "Potential market")
                .build(2);

        when(assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId))
                .thenReturn(assessorResponses);
        when(assessmentService.getAllQuestionsById(assessmentId)).thenReturn(questions);

        MvcResult result = mockMvc.perform(get("/{assessmentId}/summary", assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-application-summary"))
                .andReturn();

        AssessmentApplicationSummaryViewModel model = (AssessmentApplicationSummaryViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentId, model.getAssessmentId());
        assertEquals(50, model.getDaysLeftPercentage());
        assertEquals(3, model.getDaysLeft());
        assertEquals(expectedCompetition, model.getCompetition());
        assertEquals(expectedApplication, model.getApplication());
        assertEquals(2, model.getQuestions().size());
        assertEquals(Integer.valueOf(21), model.getTotalScoreGiven());
        assertEquals(Integer.valueOf(100), model.getTotalScorePossible());
        assertEquals(Integer.valueOf(21), model.getTotalScorePercentage());

        AssessmentApplicationSummaryQuestionViewModel summaryQuestion1 = model.getQuestions().get(0);
        assertEquals(Long.valueOf(1L), summaryQuestion1.getQuestionId());
        assertEquals("Business opportunity", summaryQuestion1.getDisplayLabel());
        assertEquals("Q1", summaryQuestion1.getDisplayLabelShort());
        assertEquals(Integer.valueOf(10), summaryQuestion1.getScorePossible());
        assertEquals(asMap(
                "SCORE", "3",
                "FEEDBACK", "Blah",
                "SCOPE", "Yes"), summaryQuestion1.getValues());
        assertFalse(summaryQuestion1.isComplete());

        AssessmentApplicationSummaryQuestionViewModel summaryQuestion2 = model.getQuestions().get(1);
        assertEquals(Long.valueOf(2L), summaryQuestion2.getQuestionId());
        assertEquals("Potential market", summaryQuestion2.getDisplayLabel());
        assertEquals("Q2", summaryQuestion2.getDisplayLabelShort());
        assertEquals(Integer.valueOf(10), summaryQuestion2.getScorePossible());
        assertEquals(asMap(
                "SCORE", "3",
                "FEEDBACK", "Blah",
                "SCOPE", "Yes"), summaryQuestion2.getValues());
        assertFalse(summaryQuestion2.isComplete());
    }
}
