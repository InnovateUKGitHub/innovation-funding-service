package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder;

import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;

public class AssessorFormInputResponseDocs {

    public static final AssessorFormInputResponseResourceBuilder assessorFormInputResponseResourceBuilder = newAssessorFormInputResponseResource()
            .withId(1L)
            .withAssessment(2L)
            .withFormInput(3L)
            .withQuestion(4L)
            .withValue("message");
}
