package com.worth.ifs.documentation;

import com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDateTime;

import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorFormInputResponseDocs {
    public static final FieldDescriptor[] AssessorFormInputResponseFields = {
            fieldWithPath("id").description("Id of the assessor form input response"),
            fieldWithPath("assessment").description("assessment process that the response belongs to"),
            fieldWithPath("question").description("question that the response is associated with"),
            fieldWithPath("formInput").description("form input that the response is associated with"),
            fieldWithPath("value").description("entered value of the response"),
            fieldWithPath("formInputMaxWordCount").description("maximum number of words allowed for the response"),
            fieldWithPath("updatedDate").description("when the response was last updated"),
    };

    public static final AssessorFormInputResponseResourceBuilder assessorFormInputResponseResourceBuilder  = newAssessorFormInputResponseResource()
            .withId(1L)
            .withAssessment(2L)
            .withFormInput(3L)
            .withQuestion(4L)
            .withFormInputMaxWordCount(100)
            .withValue("message")
            .withUpdatedDate(LocalDateTime.parse("2016-07-12T16:10:50.21"));
}
