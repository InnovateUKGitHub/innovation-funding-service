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
        Long expectedFormInput = 3L;
        Integer expectedNumericValue = 999;
        String expectedTextValue = "Blah";
        LocalDateTime expectedUpdatedDate = LocalDateTime.parse("2016-07-12T16:10:50.21");

        AssessorFormInputResponseResource assessorFormInputResponse = newAssessorFormInputResponseResource()
                .withId(expectedId)
                .withAssessment(expectedAssessment)
                .withFormInput(expectedFormInput)
                .withNumericValue(expectedNumericValue)
                .withTextValue(expectedTextValue)
                .withUpdatedDate(expectedUpdatedDate)
                .build();

        assertEquals(expectedId, assessorFormInputResponse.getId());
        assertEquals(expectedAssessment, assessorFormInputResponse.getAssessment());
        assertEquals(expectedFormInput, assessorFormInputResponse.getFormInput());
        assertEquals(expectedNumericValue, assessorFormInputResponse.getNumericValue());
        assertEquals(expectedTextValue, assessorFormInputResponse.getTextValue());
        assertEquals(expectedUpdatedDate, assessorFormInputResponse.getUpdatedDate());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedIds = {1L, 2L};
        Long[] expectedAssessments = {3L, 4L};
        Long[] expectedFormInputs = {5L, 6L};
        Integer[] expectedNumericValues = {999, 888};
        String[] expectedTextValues = {"Sample message 1", "Sample message 2"};
        LocalDateTime[] expectedUpdatedDates = {LocalDateTime.parse("2016-07-12T16:10:50.21"), LocalDateTime.parse("2016-07-12T16:15:25.42")};

        List<AssessorFormInputResponseResource> assessorFormInputResponses = newAssessorFormInputResponseResource()
                .withId(expectedIds)
                .withAssessment(expectedAssessments)
                .withFormInput(expectedFormInputs)
                .withNumericValue(expectedNumericValues)
                .withTextValue(expectedTextValues)
                .withUpdatedDate(expectedUpdatedDates)
                .build(2);

        AssessorFormInputResponseResource first = assessorFormInputResponses.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedAssessments[0], first.getAssessment());
        assertEquals(expectedFormInputs[0], first.getFormInput());
        assertEquals(expectedNumericValues[0], first.getNumericValue());
        assertEquals(expectedTextValues[0], first.getTextValue());
        assertEquals(expectedUpdatedDates[0], first.getUpdatedDate());

        AssessorFormInputResponseResource second = assessorFormInputResponses.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedAssessments[1], second.getAssessment());
        assertEquals(expectedFormInputs[1], second.getFormInput());
        assertEquals(expectedNumericValues[1], second.getNumericValue());
        assertEquals(expectedTextValues[1], second.getTextValue());
        assertEquals(expectedUpdatedDates[1], second.getUpdatedDate());
    }

}