package com.worth.ifs.assessment.builder;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessorFormInputResponse;
import com.worth.ifs.form.domain.FormInput;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.assertEquals;

public class AssessorFormInputResponseBuilderTest {

    @Test
    public void testBuildOne() {
        Long expectedId = 1L;
        Assessment expectedAssessment = newAssessment().build();
        FormInput expectedFormInput = newFormInput().build();
        Integer expectedNumericValue = 999;
        String expectedTextValue = "Blah";
        LocalDateTime expectedUpdatedDate = LocalDateTime.parse("2016-07-12T16:10:50.21");

        AssessorFormInputResponse assessorFormInputResponse = newAssessorFormInputResponse()
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
        Assessment[] expectedAssessments = newAssessment().buildArray(2, Assessment.class);
        FormInput[] expectedFormInputs = newFormInput().buildArray(2, FormInput.class);
        Integer[] expectedNumericValues = {999, 888};
        String[] expectedTextValues = {"Sample message 1", "Sample message 2"};
        LocalDateTime[] expectedUpdatedDates = {LocalDateTime.parse("2016-07-12T16:10:50.21"), LocalDateTime.parse("2016-07-12T16:15:25.42")};

        List<AssessorFormInputResponse> assessorFormInputResponses = newAssessorFormInputResponse()
                .withId(expectedIds)
                .withAssessment(expectedAssessments)
                .withFormInput(expectedFormInputs)
                .withNumericValue(expectedNumericValues)
                .withTextValue(expectedTextValues)
                .withUpdatedDate(expectedUpdatedDates)
                .build(2);

        AssessorFormInputResponse first = assessorFormInputResponses.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedAssessments[0], first.getAssessment());
        assertEquals(expectedFormInputs[0], first.getFormInput());
        assertEquals(expectedNumericValues[0], first.getNumericValue());
        assertEquals(expectedTextValues[0], first.getTextValue());
        assertEquals(expectedUpdatedDates[0], first.getUpdatedDate());

        AssessorFormInputResponse second = assessorFormInputResponses.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedAssessments[1], second.getAssessment());
        assertEquals(expectedFormInputs[1], second.getFormInput());
        assertEquals(expectedNumericValues[1], second.getNumericValue());
        assertEquals(expectedTextValues[1], second.getTextValue());
        assertEquals(expectedUpdatedDates[1], second.getUpdatedDate());
    }

}