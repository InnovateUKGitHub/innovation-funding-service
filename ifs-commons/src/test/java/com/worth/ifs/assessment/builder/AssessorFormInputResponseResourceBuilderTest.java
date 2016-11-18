package com.worth.ifs.assessment.builder;

import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.junit.Assert.assertEquals;

public class AssessorFormInputResponseResourceBuilderTest {

    @Test
    public void testBuildOne() {
        Long expectedId = 1L;
        Long expectedAssessment = 2L;
        Long expectedQuestion = 3L;
        Long expectedFormInput = 4L;
        String expectedValue = "Feedback";
        Integer expectedFormInputMaxWordCount = 100;
        LocalDateTime expectedUpdatedDate = LocalDateTime.parse("2016-07-12T16:10:50.21");

        AssessorFormInputResponseResource assessorFormInputResponse = newAssessorFormInputResponseResource()
                .withId(expectedId)
                .withAssessment(expectedAssessment)
                .withQuestion(expectedQuestion)
                .withFormInput(expectedFormInput)
                .withValue(expectedValue)
                .withFormInputMaxWordCount(expectedFormInputMaxWordCount)
                .withUpdatedDate(expectedUpdatedDate)
                .build();

        assertEquals(expectedId, assessorFormInputResponse.getId());
        assertEquals(expectedAssessment, assessorFormInputResponse.getAssessment());
        assertEquals(expectedQuestion, assessorFormInputResponse.getQuestion());
        assertEquals(expectedFormInput, assessorFormInputResponse.getFormInput());
        assertEquals(expectedValue, assessorFormInputResponse.getValue());
        assertEquals(expectedFormInputMaxWordCount, assessorFormInputResponse.getFormInputMaxWordCount());
        assertEquals(expectedUpdatedDate, assessorFormInputResponse.getUpdatedDate());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedIds = {1L, 2L};
        Long[] expectedAssessments = {3L, 4L};
        Long[] expectedQuestions = {5L, 6L};
        Long[] expectedFormInputs = {7L, 8L};
        String[] expectedValues = {"Sample message 1", "Sample message 2"};
        Integer[] expectedFormInputMaxWordCounts = {100, 200};
        LocalDateTime[] expectedUpdatedDates = {LocalDateTime.parse("2016-07-12T16:10:50.21"), LocalDateTime.parse("2016-07-12T16:15:25.42")};

        List<AssessorFormInputResponseResource> assessorFormInputResponses = newAssessorFormInputResponseResource()
                .withId(expectedIds)
                .withAssessment(expectedAssessments)
                .withQuestion(expectedQuestions)
                .withFormInput(expectedFormInputs)
                .withValue(expectedValues)
                .withFormInputMaxWordCount(expectedFormInputMaxWordCounts)
                .withUpdatedDate(expectedUpdatedDates)
                .build(2);

        AssessorFormInputResponseResource first = assessorFormInputResponses.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedAssessments[0], first.getAssessment());
        assertEquals(expectedQuestions[0], first.getQuestion());
        assertEquals(expectedFormInputs[0], first.getFormInput());
        assertEquals(expectedValues[0], first.getValue());
        assertEquals(expectedFormInputMaxWordCounts[0], first.getFormInputMaxWordCount());
        assertEquals(expectedUpdatedDates[0], first.getUpdatedDate());

        AssessorFormInputResponseResource second = assessorFormInputResponses.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedAssessments[1], second.getAssessment());
        assertEquals(expectedQuestions[1], second.getQuestion());
        assertEquals(expectedFormInputs[1], second.getFormInput());
        assertEquals(expectedValues[1], second.getValue());
        assertEquals(expectedFormInputMaxWordCounts[1], second.getFormInputMaxWordCount());
        assertEquals(expectedUpdatedDates[1], second.getUpdatedDate());
    }

}