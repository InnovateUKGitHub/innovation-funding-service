package com.worth.ifs.assessment.builder;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentFeedback;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentFeedbackBuilder.newAssessmentFeedback;
import static org.junit.Assert.assertEquals;

public class AssessmentFeedbackBuilderTest {

    @Test
    public void buildOne() {
        final Long expectedId = 1L;
        final Assessment expectedAssessment = newAssessment().build();
        final String expectedFeedback = "Sample message";
        final Integer expectedScore = 10;
        final Question expectedQuestion = newQuestion().build();

        final AssessmentFeedback assessmentFeedback = newAssessmentFeedback()
                .withId(expectedId)
                .withAssessment(expectedAssessment)
                .withFeedback(expectedFeedback)
                .withScore(expectedScore)
                .withQuestion(expectedQuestion)
                .build();

        assertEquals(expectedId, assessmentFeedback.getId());
        assertEquals(expectedAssessment, assessmentFeedback.getAssessment());
        assertEquals(expectedFeedback, assessmentFeedback.getFeedback());
        assertEquals(expectedScore, assessmentFeedback.getScore());
        assertEquals(expectedQuestion, assessmentFeedback.getQuestion());
    }

    @Test
    public void buildMany() {
        final Long[] expectedIds = { 1L, 2L };
        final Assessment[] expectedAssessments = newAssessment().buildArray(2, Assessment.class);
        final String[] expectedFeedbacks = { "Sample message 1", "Sample message 2" };
        final Integer[] expectedScores = { 10, 10 };
        final Question[] expectedQuestions = newQuestion().buildArray(2, Question.class);
        final List<AssessmentFeedback> assessmentFeedbacks = newAssessmentFeedback()
                .withId(expectedIds)
                .withAssessment(expectedAssessments)
                .withFeedback(expectedFeedbacks)
                .withScore(expectedScores)
                .withQuestion(expectedQuestions)
                .build(2);

        final AssessmentFeedback first = assessmentFeedbacks.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedAssessments[0], first.getAssessment());
        assertEquals(expectedFeedbacks[0], first.getFeedback());
        assertEquals(expectedScores[0], first.getScore());
        assertEquals(expectedQuestions[0], first.getQuestion());

        final AssessmentFeedback second = assessmentFeedbacks.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedAssessments[1], second.getAssessment());
        assertEquals(expectedFeedbacks[1], second.getFeedback());
        assertEquals(expectedScores[1], second.getScore());
        assertEquals(expectedQuestions[1], second.getQuestion());
    }

}