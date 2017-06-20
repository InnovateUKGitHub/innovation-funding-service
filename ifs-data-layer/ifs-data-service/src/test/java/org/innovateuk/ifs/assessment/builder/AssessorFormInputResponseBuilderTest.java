package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.form.domain.FormInput;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseBuilder.newAssessorFormInputResponse;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.*;

public class AssessorFormInputResponseBuilderTest {

    @Test
    public void testBuildOne() {
        Long expectedId = 1L;
        Assessment expectedAssessment = newAssessment().build();
        FormInput expectedFormInput = newFormInput().build();
        String expectedValue = "Blah";
        ZonedDateTime expectedUpdatedDate = LocalDateTime.parse("2016-07-12T16:10:50.21").atZone(ZoneId.systemDefault());

        AssessorFormInputResponse assessorFormInputResponse = newAssessorFormInputResponse()
                .withId(expectedId)
                .withAssessment(expectedAssessment)
                .withFormInput(expectedFormInput)
                .withValue(expectedValue)
                .withUpdatedDate(expectedUpdatedDate)
                .build();

        assertEquals(expectedId, assessorFormInputResponse.getId());
        assertEquals(expectedAssessment, assessorFormInputResponse.getAssessment());
        assertEquals(expectedFormInput, assessorFormInputResponse.getFormInput());
        assertEquals(expectedValue, assessorFormInputResponse.getValue());
        assertEquals(expectedUpdatedDate, assessorFormInputResponse.getUpdatedDate());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedIds = {1L, 2L};
        Assessment[] expectedAssessments = newAssessment().buildArray(2, Assessment.class);
        FormInput[] expectedFormInputs = newFormInput().buildArray(2, FormInput.class);
        String[] expectedValues = {"Sample message 1", "Sample message 2"};
        ZonedDateTime[] expectedUpdatedDates = {LocalDateTime.parse("2016-07-12T16:10:50.21").atZone(ZoneId.systemDefault()), LocalDateTime.parse("2016-07-12T16:15:25.42").atZone(ZoneId.systemDefault())};

        List<AssessorFormInputResponse> assessorFormInputResponses = newAssessorFormInputResponse()
                .withId(expectedIds)
                .withAssessment(expectedAssessments)
                .withFormInput(expectedFormInputs)
                .withValue(expectedValues)
                .withUpdatedDate(expectedUpdatedDates)
                .build(2);

        AssessorFormInputResponse first = assessorFormInputResponses.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedAssessments[0], first.getAssessment());
        assertEquals(expectedFormInputs[0], first.getFormInput());
        assertEquals(expectedValues[0], first.getValue());
        assertEquals(expectedUpdatedDates[0], first.getUpdatedDate());

        AssessorFormInputResponse second = assessorFormInputResponses.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedAssessments[1], second.getAssessment());
        assertEquals(expectedFormInputs[1], second.getFormInput());
        assertEquals(expectedValues[1], second.getValue());
        assertEquals(expectedUpdatedDates[1], second.getUpdatedDate());
    }

}
