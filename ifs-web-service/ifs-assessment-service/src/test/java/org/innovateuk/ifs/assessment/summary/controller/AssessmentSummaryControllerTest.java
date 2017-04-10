package org.innovateuk.ifs.assessment.summary.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.assessment.summary.form.AssessmentSummaryForm;
import org.innovateuk.ifs.assessment.summary.populator.AssessmentSummaryModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.summary.controller.AssessmentSummaryController;
import org.innovateuk.ifs.assessment.summary.viewmodel.AssessmentSummaryQuestionViewModel;
import org.innovateuk.ifs.assessment.summary.viewmodel.AssessmentSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentSummaryControllerTest extends BaseControllerMockMVCTest<AssessmentSummaryController> {
    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Spy
    @InjectMocks
    private AssessmentSummaryModelPopulator assessmentSummaryModelPopulator;

    @Override
    protected AssessmentSummaryController supplyControllerUnderTest() {
        return new AssessmentSummaryController();
    }

    @Test
    public void getSummary() throws Exception {
        long applicationId = 1L;

        CompetitionResource competitionResource = setupCompetitionResource();
        AssessmentResource assessmentResource = setupAssessment(applicationId, competitionResource.getId());
        List<QuestionResource> questionResources = setupQuestions(competitionResource.getId(), assessmentResource.getId());

        AssessmentSummaryQuestionViewModel expectedReviewQuestion1ViewModel = new AssessmentSummaryQuestionViewModel(
                questionResources.get(1).getId(), "Scope", "", false, null, null, null, true, true);

        AssessmentSummaryQuestionViewModel expectedReviewQuestion2ViewModel = new AssessmentSummaryQuestionViewModel(
                questionResources.get(2).getId(), "1. Business opportunity", "Q1", true, 15, 20, "feedback", null, true);

        AssessmentSummaryQuestionViewModel expectedReviewQuestion3ViewModel = new AssessmentSummaryQuestionViewModel(
                questionResources.get(3).getId(), "2. Potential market", "Q2", false, null, null, null, null, false);

        AssessmentSummaryForm expectedForm = new AssessmentSummaryForm();

        AssessmentSummaryViewModel expectedViewModel = new AssessmentSummaryViewModel(assessmentResource.getId(),
                applicationId,
                "Application name",
                3L,
                50L,
                asList(expectedReviewQuestion1ViewModel, expectedReviewQuestion2ViewModel, expectedReviewQuestion3ViewModel),
                15,
                20,
                75);

        mockMvc.perform(get("/{assessmentId}/summary", assessmentResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessment/application-summary"));
    }

    @Test
    public void getSummary_withExistingFundingConfirmation() throws Exception {
        long applicationId = 1L;
        String expectedFeedback = "feedback";
        String expectedComment = "comment";

        CompetitionResource competitionResource = setupCompetitionResource();

        AssessmentResource assessmentResource = newAssessmentResource()
                .withApplication(applicationId)
                .withCompetition(competitionResource.getId())
                .withFundingDecision(newAssessmentFundingDecisionOutcomeResource()
                        .withFundingConfirmation(true)
                        .withFeedback(expectedFeedback)
                        .withComment(expectedComment)
                        .build())
                .build();
        when(assessmentService.getById(assessmentResource.getId())).thenReturn(assessmentResource);

        AssessmentSummaryForm expectedForm = new AssessmentSummaryForm();
        expectedForm.setFundingConfirmation(true);
        expectedForm.setFeedback(expectedFeedback);
        expectedForm.setComment(expectedComment);

        mockMvc.perform(get("/{assessmentId}/summary", assessmentResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("assessment/application-summary"));
    }

    @Test
    public void save() throws Exception {
        CompetitionResource competition = setupCompetitionResource();

        AssessmentResource assessmentResource = newAssessmentResource()
                .withCompetition(competition.getId())
                .build();

        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));

        when(assessmentService.getById(assessmentResource.getId())).thenReturn(assessmentResource);
        when(assessmentService.recommend(assessmentResource.getId(), true, feedback, comment))
                .thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/summary", assessmentResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/" + competition.getId()));

        verify(assessmentService).recommend(assessmentResource.getId(), true, feedback, comment);
        verify(assessmentService).getById(assessmentResource.getId());
    }

    @Test
    public void save_noFundingConfirmation() throws Exception {
        CompetitionResource competition = setupCompetitionResource();
        AssessmentResource assessmentResource = setupAssessment(1L, competition.getId());

        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "fundingConfirmation"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertNull(form.getFundingConfirmation());
        assertEquals(feedback, form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("fundingConfirmation"));
        assertEquals("Please indicate your decision.", bindingResult.getFieldError("fundingConfirmation")
                .getDefaultMessage());

        verify(assessmentService).getById(assessmentResource.getId());
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void save_noFeedbackAndFundingConfirmationIsTrue() throws Exception {
        CompetitionResource competition = setupCompetitionResource();
        AssessmentResource assessmentResource = setupAssessment(1L, competition.getId());

        String comment = String.join(" ", nCopies(100, "comment"));

        when(assessmentService.recommend(assessmentResource.getId(), true, null, comment))
                .thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/summary", assessmentResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("comment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/" + competition.getId()));

        verify(assessmentService).recommend(assessmentResource.getId(), true, null, comment);
    }

    @Test
    public void save_noFeedbackAndFundingConfirmationIsFalse() throws Exception {
        CompetitionResource competition = setupCompetitionResource();
        AssessmentResource assessmentResource = setupAssessment(1L, competition.getId());

        String comment = String.join(" ", nCopies(100, "comment"));

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "false")
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertFalse(form.getFundingConfirmation());
        assertNull(form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("feedback"));
        assertEquals("Please enter your feedback.", bindingResult.getFieldError("feedback").getDefaultMessage());

        verify(assessmentService).getById(assessmentResource.getId());
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void save_noComment() throws Exception {
        CompetitionResource competition = setupCompetitionResource();
        AssessmentResource assessmentResource = setupAssessment(1L, competition.getId());

        String feedback = String.join(" ", nCopies(100, "feedback"));

        when(assessmentService.recommend(assessmentResource.getId(), true, feedback, null))
                .thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/summary", assessmentResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("feedback", feedback))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/" + competition.getId()));

        verify(assessmentService).recommend(assessmentResource.getId(), true, feedback, null);
    }

    @Test
    public void save_exceedsCharacterSizeLimit() throws Exception {
        CompetitionResource competition = setupCompetitionResource();
        AssessmentResource assessmentResource = setupAssessment(1L, competition.getId());

        String feedback = RandomStringUtils.random(5001);
        String comment = RandomStringUtils.random(5001);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "feedback"))
                .andExpect(model().attributeHasFieldErrors("form", "comment"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertTrue(form.getFundingConfirmation());
        assertEquals(feedback, form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("feedback"));
        assertTrue(bindingResult.hasFieldErrors("comment"));
        assertEquals("This field cannot contain more than {1} characters.",
                bindingResult.getFieldError("feedback").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("feedback").getArguments()[1]);
        assertEquals("This field cannot contain more than {1} characters.",
                bindingResult.getFieldError("comment").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("comment").getArguments()[1]);

        verify(assessmentService).getById(assessmentResource.getId());
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void save_exceedsWordLimit() throws Exception {
        CompetitionResource competition = setupCompetitionResource();
        AssessmentResource assessmentResource = setupAssessment(1L, competition.getId());

        String feedback = String.join(" ", nCopies(101, "feedback"));
        String comment = String.join(" ", nCopies(101, "comment"));

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "feedback"))
                .andExpect(model().attributeHasFieldErrors("form", "comment"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertTrue(form.getFundingConfirmation());
        assertEquals(feedback, form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("feedback"));
        assertTrue(bindingResult.hasFieldErrors("comment"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.",
                bindingResult.getFieldError("feedback").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("feedback").getArguments()[1]);
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.",
                bindingResult.getFieldError("comment").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("comment").getArguments()[1]);

        verify(assessmentService).getById(assessmentResource.getId());
        verifyNoMoreInteractions(assessmentService);
    }

    private CompetitionResource setupCompetitionResource() {
        ZonedDateTime now = ZonedDateTime.now();

        CompetitionResource competitionResource = newCompetitionResource()
                .withAssessorAcceptsDate(now.minusDays(2))
                .withAssessorDeadlineDate(now.plusDays(4))
                .build();

        when(competitionService.getById(competitionResource.getId())).thenReturn(competitionResource);

        return competitionResource;
    }

    private List<QuestionResource> setupQuestions(long competitionId, long assessmentId) {
        List<SectionResource> sectionResources = newSectionResource()
                .build(2);

        QuestionResource question1 = newQuestionResource()
                .withSection(sectionResources.get(0).getId())
                .withQuestionNumber((String) null)
                .withShortName("Application details")
                .withAssessorMaximumScore((Integer) null)
                .build();

        QuestionResource question2 = newQuestionResource()
                .withSection(sectionResources.get(0).getId())
                .withQuestionNumber((String) null)
                .withShortName("Scope")
                .withAssessorMaximumScore((Integer) null)
                .build();

        QuestionResource question3 = newQuestionResource()
                .withSection(sectionResources.get(1).getId())
                .withQuestionNumber("1")
                .withShortName("Business opportunity")
                .withAssessorMaximumScore(20)
                .build();

        QuestionResource question4 = newQuestionResource()
                .withSection(sectionResources.get(1).getId())
                .withQuestionNumber("2")
                .withShortName("Potential market")
                .withAssessorMaximumScore((Integer) null)
                .build();

        List<QuestionResource> questionResources = asList(question1, question2, question3, question4);
        when(questionService.getQuestionsByAssessment(assessmentId)).thenReturn(questionResources);

        FormInputType anotherTypeOfFormInput = ASSESSOR_RESEARCH_CATEGORY;

        // The first question will have no form inputs, therefore no assessment required and should not appear in the summary
        List<FormInputResource> formInputsForQuestion1 = emptyList();

        // The second question will have 'application in scope' type amongst the form inputs meaning that the AssessmentSummaryQuestionViewModel.applicationInScope should get populated with any response to this input
        List<FormInputResource> formInputsForQuestion2 = newFormInputResource()
                .withType(anotherTypeOfFormInput, ASSESSOR_APPLICATION_IN_SCOPE)
                .withQuestion(question2.getId())
                .build(2);

        // The third question will have 'feedback' and 'score' types amongst the form inputs meaning that the AssessmentSummaryQuestionViewModel.feedback and .scoreGiven should get populated with any response to this input
        List<FormInputResource> formInputsForQuestion3 = newFormInputResource()
                .withType(anotherTypeOfFormInput, ASSESSOR_SCORE, TEXTAREA)
                .withQuestion(question3.getId())
                .build(3);
        when(formInputService.findAssessmentInputsByCompetition(competitionId)).thenReturn(
                combineLists(formInputsForQuestion1, formInputsForQuestion2, formInputsForQuestion3));

        // The fourth question will have form inputs without a complete set of responses meaning that it should be incomplete
        List<FormInputResource> formInputsForQuestion4 = newFormInputResource()
                .withType(anotherTypeOfFormInput, TEXTAREA)
                .withQuestion(question4.getId())
                .build(2);
        when(formInputService.findAssessmentInputsByCompetition(competitionId)).thenReturn(
                concat(
                        concat(
                                concat(formInputsForQuestion1.stream(), formInputsForQuestion2.stream()),
                                formInputsForQuestion3.stream()), formInputsForQuestion4.stream()).collect(toList()));

        List<AssessorFormInputResponseResource> assessorResponses = newAssessorFormInputResponseResource()
                .withQuestion(
                        question2.getId(),
                        question2.getId(),
                        question3.getId(),
                        question3.getId(),
                        question3.getId(),
                        question4.getId())
                .withFormInput(
                        formInputsForQuestion2.get(0).getId(),
                        formInputsForQuestion2.get(1).getId(),
                        formInputsForQuestion3.get(0).getId(),
                        formInputsForQuestion3.get(1).getId(),
                        formInputsForQuestion3.get(2).getId(),
                        formInputsForQuestion4.get(0).getId())
                .withValue("another response", "true", "another response", "15", "feedback", "another response")
                .build(6);
        when(assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId))
                .thenReturn(assessorResponses);

        return questionResources;
    }

    private AssessmentResource setupAssessment(long applicationId, long competitionId) {
        AssessmentResource assessmentResource = newAssessmentResource()
                .withApplication(applicationId)
                .withApplicationName("Application name")
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getById(assessmentResource.getId())).thenReturn(assessmentResource);

        return assessmentResource;
    }
}
