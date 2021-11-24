package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorFormInputResponseDocs {

    public static final AssessorFormInputResponseResourceBuilder assessorFormInputResponseResourceBuilder = newAssessorFormInputResponseResource()
            .withId(1L)
            .withAssessment(2L)
            .withFormInput(3L)
            .withQuestion(4L)
            .withValue("message");
}
