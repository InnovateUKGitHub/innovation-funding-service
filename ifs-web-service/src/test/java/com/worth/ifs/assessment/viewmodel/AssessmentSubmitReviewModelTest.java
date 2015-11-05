package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.builder.AssessorFeedbackBuilder;
import com.worth.ifs.application.builder.ResponseBuilder;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.AssessorFeedbackBuilder.newFeedback;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.ResponseBuilder.newResponse;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.assessment.AssessmentBuilder.newAssessment;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.util.IfsFunctions.combineLists;
import static com.worth.ifs.util.IfsFunctions.forEachWithIndex;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests for the view model that backs the Assessor's Assessment Review page.
 *
 * Created by dwatson on 09/10/15.
 */
public class AssessmentSubmitReviewModelTest {

    @Test
    public void test_newReviewModel() {

        //
        // Build the data
        //
        ProcessRole assessorProcessRole = newProcessRole().build();

        List<Question> section1Questions = newQuestion().build(2);
        List<Question> section2Questions = newQuestion().build(2);

        List<Section> sections = newSection()
                .withQuestionSets(asList(section1Questions, section2Questions))
                .build(2);

        Competition competition = newCompetition()
                .withSections(sections)
                .build();

        Application application = newApplication().
                withCompetition(competition).
                build();

        ResponseBuilder responseBuilder = newResponse().
                withApplication(application);

        AssessorFeedbackBuilder feedbackBuilder = newFeedback().
                withAssessor(assessorProcessRole).
                withAssessmentValue((i, feedback) -> (i + 1) + "");

        List<AssessorFeedback> section1ResponseFeedback = feedbackBuilder.build(2);
        List<AssessorFeedback> section2ResponseFeedback = feedbackBuilder.build(2);

        List<Response> section1Responses = responseBuilder.
                withQuestions(section1Questions).
                withFeedback(section1ResponseFeedback).
                build(2);

        List<Response> section2Responses = responseBuilder.
                withQuestions(section2Questions).
                withFeedback(section2ResponseFeedback).
                build(2);

        Assessment assessment = newAssessment().
                withApplication(application).
                build();

        List<Response> allResponses = combineLists(section1Responses, section2Responses);

        //
        // Build the model
        //
        AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, allResponses, assessorProcessRole);

        Map<Question, AssessorFeedback> originalQuestionToFeedback = new HashMap<>();
        IntStream.range(0, section1Questions.size()).forEach(i -> originalQuestionToFeedback.put(section1Questions.get(i), section1ResponseFeedback.get(i)));
        IntStream.range(0, section2Questions.size()).forEach(i -> originalQuestionToFeedback.put(section2Questions.get(i), section2ResponseFeedback.get(i)));

        //
        // Test the top-level model attributes
        //
        assertNotNull(model);
        assertEquals(application, model.getApplication());
        assertEquals(assessment, model.getAssessment());
        assertEquals(competition, model.getCompetition());

        originalQuestionToFeedback.entrySet().forEach(entry -> {
            Question question = entry.getKey();
            AssessorFeedback feedback = entry.getValue();
            assertEquals(feedback, model.getFeedbackForQuestion(question));
        });

        //
        // test the questions and score sections
        //
        List<Question> allQuestions = combineLists(section1Questions, section2Questions);
        assertEquals(allQuestions, model.getQuestions());
        assertEquals(allQuestions, model.getScorableQuestions());
        assertEquals(1 + 2 + 1 + 2, model.getTotalScore());
        assertEquals(10 * 2 * 2, model.getPossibleScore());
        assertEquals(15, model.getScorePercentage());
        allQuestions.forEach(question -> assertNotNull(model.getFeedbackForQuestion(question)));

        //
        // test the section details
        //
        assertNotNull(model.getAssessmentSummarySections());
        assertEquals(2, model.getAssessmentSummarySections().size());

        forEachWithIndex(model.getAssessmentSummarySections(), (i, summarySection) -> {

            // check the original section details against the modelled section details
            Section originalSection = sections.get(i);
            assertEquals(originalSection.getId(), summarySection.getId());
            assertEquals(originalSection.getName(), summarySection.getName());
            assertEquals(originalSection.getQuestions().size(), summarySection.getQuestionsRequiringFeedback().size());
            assertEquals(true, summarySection.isAssessmentComplete());

            // check the original questions and each relevant response for this assessor with the modelled question and
            // feedback details
            forEachWithIndex(summarySection.getQuestionsRequiringFeedback(), (j, summaryQuestion) -> {

                Question originalQuestion = originalSection.getQuestions().get(j);
                assertEquals(originalQuestion.getId(), summaryQuestion.getId());
                assertEquals(originalQuestion.getName(), summaryQuestion.getName());

                AssessorFeedback originalFeedback = originalQuestionToFeedback.get(originalQuestion);
                assertEquals(originalFeedback.getAssessmentFeedback(), summaryQuestion.getFeedback().getFeedbackText());
                assertEquals(originalFeedback.getAssessmentValue(), summaryQuestion.getFeedback().getFeedbackValue());
            });
        });
    }


    @Test
    public void test_newReviewModel_assessorsSeeOwnScoresOnly() {

        //
        // Build the data
        //
        ProcessRole assessorProcessRole = newProcessRole().build();
        ProcessRole differentAssessor = newProcessRole().build();

        List<Question> questions = newQuestion().build(2);
        Section section = newSection().withQuestions(questions).build();
        Competition competition = newCompetition().withSections(asList(section)).build();
        Application application = newApplication().withCompetition(competition).build();
        Assessment assessment = newAssessment().withApplication(application).build();

        List<AssessorFeedback> feedback = newFeedback().
                withAssessor(assessorProcessRole).
                withAssessmentValue((i, fb) -> (i + 1) + "").
                build(2);

        List<Response> responses = newResponse().withApplication(application).
                withQuestions(questions).withFeedback(feedback).build(2);

        //
        // Build the model from the first Assessor's point of view
        //
        {
            AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, responses, assessorProcessRole);

            //
            // test the questions and score sections
            //
            assertEquals(1 + 2, model.getTotalScore());
            assertEquals(10 * 2, model.getPossibleScore());
            assertEquals(15, model.getScorePercentage());

            //
            // test the section details
            //
            assertEquals(1, model.getAssessmentSummarySections().size());
            AssessmentSummarySection summarySection = model.getAssessmentSummarySections().get(0);

            Section originalSection = section;

            assertEquals(originalSection.getId(), summarySection.getId());
            assertEquals(originalSection.getName(), summarySection.getName());
            assertEquals(originalSection.getQuestions().size(), summarySection.getQuestionsRequiringFeedback().size());
            assertEquals(true, summarySection.isAssessmentComplete());

            // check the original questions and each relevant response for this assessor with the modelled question and
            // feedback details
            forEachWithIndex(summarySection.getQuestionsRequiringFeedback(), (i, summaryQuestion) -> {

                Question originalQuestion = originalSection.getQuestions().get(i);
                assertEquals(originalQuestion.getId(), summaryQuestion.getId());
                assertEquals(originalQuestion.getName(), summaryQuestion.getName());

                AssessorFeedback originalFeedback = feedback.get(i);
                assertEquals(originalFeedback.getAssessmentFeedback(), summaryQuestion.getFeedback().getFeedbackText());
                assertEquals(originalFeedback.getAssessmentValue(), summaryQuestion.getFeedback().getFeedbackValue());
            });

            assertEquals(feedback.get(0), model.getFeedbackForQuestion(questions.get(0)));
            assertEquals(feedback.get(1), model.getFeedbackForQuestion(questions.get(1)));
        }

        //
        // and now test the same thing but from a different Assessor's point of view (one who didn't provide any
        // feedback)
        //
        {
            AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, responses, differentAssessor);

            //
            // test the questions and score sections
            //
            assertEquals(0, model.getTotalScore());
            assertEquals(10 * 2, model.getPossibleScore());
            assertEquals(0, model.getScorePercentage());

            //
            // test the section details
            //
            assertEquals(1, model.getAssessmentSummarySections().size());
            AssessmentSummarySection summarySection = model.getAssessmentSummarySections().get(0);

            Section originalSection = section;

            assertEquals(originalSection.getId(), summarySection.getId());
            assertEquals(originalSection.getName(), summarySection.getName());
            assertEquals(originalSection.getQuestions().size(), summarySection.getQuestionsRequiringFeedback().size());
            assertEquals(false, summarySection.isAssessmentComplete());

            // check the original questions and each relevant response for this assessor with the modelled question and
            // feedback details
            forEachWithIndex(summarySection.getQuestionsRequiringFeedback(), (i, summaryQuestion) -> {

                Question originalQuestion = originalSection.getQuestions().get(i);
                assertEquals(originalQuestion.getId(), summaryQuestion.getId());
                assertEquals(originalQuestion.getName(), summaryQuestion.getName());
                assertEquals(null, summaryQuestion.getFeedback());
            });

            assertNull(model.getFeedbackForQuestion(questions.get(0)));
            assertNull(model.getFeedbackForQuestion(questions.get(1)));
        }
    }

}
