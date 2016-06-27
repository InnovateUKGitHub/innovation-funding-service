package com.worth.ifs.assessment.builder;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static org.junit.Assert.assertEquals;

public class AssessmentFeedbackResourceBuilderTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void buildOne() {
        final Long expectedId = 9999L;
        final Long expectedAssessment = 8888L;
        final String expectedFeedback = "Sample message";
        final Integer expectedScore = 10;
        final Long expectedQuestion = 7777L;

        final AssessmentFeedbackResource assessmentFeedback = newAssessmentFeedbackResource()
                .withId(expectedId)
                .withAssessment(expectedAssessment)
                .withFeedback(expectedFeedback)
                .withScore(expectedScore)
                .withQuestion(expectedQuestion)
                .build();

        assertEquals(Long.valueOf(expectedId), assessmentFeedback.getId());
        assertEquals(expectedAssessment, assessmentFeedback.getAssessment());
        assertEquals(expectedFeedback, assessmentFeedback.getFeedback());
        assertEquals(expectedScore, assessmentFeedback.getScore());
        assertEquals(expectedQuestion, assessmentFeedback.getQuestion());
    }

    @Test
    public void buildMany() {
        final Long[] expectedIds = { 8888L, 9999L };
        final Long[] expectedAssessments = { 7777L, 6666L };
        final String[] expectedFeedbacks = { "Sample message 1", "Sample message 2" };
        final Integer[] expectedScores = { 10, 10 };
        final Long[] expectedQuestions = { 5555L, 4444L };
        final List<AssessmentFeedbackResource> assessmentFeedbackResources = newAssessmentFeedbackResource()
                .withId(expectedIds)
                .withAssessment(expectedAssessments)
                .withFeedback(expectedFeedbacks)
                .withScore(expectedScores)
                .withQuestion(expectedQuestions)
                .build(2);

        final AssessmentFeedbackResource first = assessmentFeedbackResources.get(0);
        assertEquals(Long.valueOf(expectedIds[0]), first.getId());
        assertEquals(expectedAssessments[0], first.getAssessment());
        assertEquals(expectedFeedbacks[0], first.getFeedback());
        assertEquals(expectedScores[0], first.getScore());
        assertEquals(expectedQuestions[0], first.getQuestion());

        final AssessmentFeedbackResource second = assessmentFeedbackResources.get(1);
        assertEquals(Long.valueOf(expectedIds[1]), second.getId());
        assertEquals(expectedAssessments[1], second.getAssessment());
        assertEquals(expectedFeedbacks[1], second.getFeedback());
        assertEquals(expectedScores[1], second.getScore());
        assertEquals(expectedQuestions[1], second.getQuestion());
    }

}