package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.*;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.worth.ifs.application.domain.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.domain.AssessorFeedbackBuilder.newFeedback;
import static com.worth.ifs.application.domain.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.domain.ResponseBuilder.newResponse;
import static com.worth.ifs.application.domain.SectionBuilder.newSection;
import static com.worth.ifs.assessment.AssessmentBuilder.newAssessment;
import static com.worth.ifs.competition.domain.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.domain.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.util.IfsFunctions.combineLists;
import static com.worth.ifs.util.IfsFunctions.forEachWithIndex;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for the view model that backs the Assessor's Assessment Review page.
 *
 * Created by dwatson on 09/10/15.
 */
public class AssessmentSubmitReviewModelTest {

    @Test
    public void test_newReviewModel() {

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

        AssessorFeedbackBuilder feedbackBuilder = newFeedback().withAssessor(assessorProcessRole);
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

        AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, allResponses, assessorProcessRole);

        //
        // Test the top-level attributes
        //
        assertNotNull(model);
        assertEquals(application, model.getApplication());
        assertEquals(assessment, model.getAssessment());
        assertEquals(competition, model.getCompetition());

        //
        // test the questions and score sections
        //
        List<Question> allQuestions = combineLists(section1Questions, section2Questions);
        assertEquals(allQuestions, model.getQuestions());
        assertEquals(allQuestions, model.getScorableQuestions());
        assertEquals(10 * 2 * 2, model.getPossibleScore());
        assertEquals(0, model.getScorePercentage());
        assertEquals(0, model.getTotalScore());
        allQuestions.forEach(question -> assertNotNull(model.getFeedbackForQuestion(question)));

        //
        // test the section details
        //
        Map<Question, AssessorFeedback> originalQuestionToFeedback = new HashMap<>();
        IntStream.range(0, section1Questions.size()).forEach(i -> originalQuestionToFeedback.put(section1Questions.get(i), section1ResponseFeedback.get(i)));
        IntStream.range(0, section2Questions.size()).forEach(i -> originalQuestionToFeedback.put(section2Questions.get(i), section2ResponseFeedback.get(i)));

        assertNotNull(model.getAssessmentSummarySections());
        assertEquals(2, model.getAssessmentSummarySections().size());

        forEachWithIndex(model.getAssessmentSummarySections(), (i, summarySection) -> {

            // check the original section details against the modelled section details
            Section originalSection = sections.get(i);
            assertEquals(originalSection.getId(), summarySection.getId());
            assertEquals(originalSection.getName(), summarySection.getName());
            assertEquals(originalSection.getQuestions().size(), summarySection.getQuestionsRequiringFeedback().size());

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

}
