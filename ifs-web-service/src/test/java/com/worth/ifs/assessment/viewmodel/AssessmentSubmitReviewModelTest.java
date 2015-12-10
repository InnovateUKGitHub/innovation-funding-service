package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.builder.AssessorFeedbackBuilder;
import com.worth.ifs.application.builder.ResponseBuilder;
import com.worth.ifs.application.builder.SectionBuilder;
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
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.forEachWithIndex;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

/**
 * Tests for the view model that backs the Assessor's Assessment Review page.
 */
public class AssessmentSubmitReviewModelTest {

    @Test
    public void test_newReviewModel() {

        //
        // Build the data
        //
        ProcessRole assessorProcessRole = newProcessRole().build();
        Assessment assessment = newAssessment().build();

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

        assessorProcessRole.setApplication(application);
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
            assertEquals(2, summarySection.getQuestionsRequiringFeedback().size());
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
    public void test_assessorsSeeOwnScoresOnly() {

        //
        // Build the data
        //
        ProcessRole assessorProcessRole = newProcessRole().build();
        ProcessRole differentAssessor = newProcessRole().build();

        List<Question> questions = newQuestion().build(2);
        Section section = newSection().withQuestions(questions).build();
        Competition competition = newCompetition().withSections(asList(section)).build();
        Application application = newApplication().withCompetition(competition).build();
        Assessment assessment = newAssessment().build();

        assessorProcessRole.setApplication(application);
        differentAssessor.setApplication(application);

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

    @Test
    public void test_onlyCertainSectionsIncludedInSummary() {

        //
        // Build the data
        //
        ProcessRole assessorProcessRole = newProcessRole().build();

        SectionBuilder sectionBuilder = newSection().withDisplayInAssessmentApplicationSummary(false);

        Section section1 = sectionBuilder.withQuestions(newQuestion().build(1)).build();
        Section section2ToBeIncluded = sectionBuilder.withDisplayInAssessmentApplicationSummary(true).withQuestions(newQuestion().build(1)).build();
        Section section3 = sectionBuilder.withQuestions(newQuestion().build(1)).build();
        List<Section> sections = asList(section1, section2ToBeIncluded, section3);

        Competition competition = newCompetition().withSections(sections).build();
        Application application = newApplication().withCompetition(competition).build();
        Assessment assessment = newAssessment().build();

        assessorProcessRole.setApplication(application);
        //
        // Build the model
        //
        AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, emptyList(), assessorProcessRole);

        //
        // test we only see the section marked to be included
        //
        assertEquals(1, model.getAssessmentSummarySections().size());
        AssessmentSummarySection summarySection = model.getAssessmentSummarySections().get(0);
        assertEquals(section2ToBeIncluded.getId(), summarySection.getId());
        assertEquals(section2ToBeIncluded.getName(), summarySection.getName());
        assertEquals(section2ToBeIncluded.getQuestions().size(), summarySection.getQuestionsRequiringFeedback().size());
    }

    // TODO DW - test questions that aren't scoreable

    @Test
    public void test_onlyScorableQuestionsIncluded() {

        //
        // Build the data
        //
        ProcessRole assessorProcessRole = newProcessRole().build();

        Question scorableQuestion = newQuestion().build();
        Question nonScorableQuestion = newQuestion().withNeedingAssessorScore(false).build();

        List<Section> sections = newSection()
                .withQuestions(asList(scorableQuestion, nonScorableQuestion))
                .build(1);

        Competition competition = newCompetition().withSections(sections).build();
        Application application = newApplication().withCompetition(competition).build();
        Assessment assessment = newAssessment().build();

        assessorProcessRole.setApplication(application);

        List<AssessorFeedback> feedback = newFeedback().
                withAssessor(assessorProcessRole).
                withAssessmentValue((i, fb) -> (i + 1) + "").
                build(1);

        List<Response> responses = newResponse().withApplication(application).
                withQuestions(asList(scorableQuestion)).withFeedback(feedback).build(1);

        //
        // Build the model
        //
        AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, responses, assessorProcessRole);

        //
        // Test the top-level model attributes
        //
        assertEquals(2, model.getQuestions().size());
        assertEquals(1, model.getScorableQuestions().size());
        assertEquals(1, model.getTotalScore());
        assertEquals(10 * 1 * 1, model.getPossibleScore());
        assertEquals(10, model.getScorePercentage());

        //
        // test the section details - in particular, test that only the scorable question is in the Section
        //
        assertNotNull(model.getAssessmentSummarySections());
        assertEquals(1, model.getAssessmentSummarySections().size());

        AssessmentSummarySection summarySection = model.getAssessmentSummarySections().get(0);
        assertEquals(1, summarySection.getQuestionsRequiringFeedback().size());
        AssessmentSummarySectionQuestion summaryQuestion = summarySection.getQuestionsRequiringFeedback().get(0);
        assertEquals(scorableQuestion.getId(), summaryQuestion.getId());
        assertEquals(scorableQuestion.getName(), summaryQuestion.getName());
    }

}
