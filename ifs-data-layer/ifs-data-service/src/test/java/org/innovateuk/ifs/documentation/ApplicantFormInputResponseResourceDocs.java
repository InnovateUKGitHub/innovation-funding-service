package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicantFormInputResponseResourceDocs {
    public static final FieldDescriptor[] applicantFormInputResponseResourceFields = {
            fieldWithPath("response").description("The response of the applicant form input response"),
            fieldWithPath("applicant").description("The applicant of the applicant form input response"),
    };
}
