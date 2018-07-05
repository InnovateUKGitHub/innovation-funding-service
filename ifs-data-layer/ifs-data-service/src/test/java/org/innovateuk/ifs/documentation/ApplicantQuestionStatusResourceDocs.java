package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicantQuestionStatusResourceDocs {

    public static final FieldDescriptor[] applicantQuestionStatusResourceFields = {
            fieldWithPath("status").description("The status of the applicant question status"),
            fieldWithPath("markedAsCompleteBy").description("The marked as complete by of the applicant question status"),
            fieldWithPath("assignee").description("The assignee of the applicant question status"),
            fieldWithPath("assignedBy").description("The assigned by of the applicant question status"),
    };
}
