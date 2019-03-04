package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationFormInputDocs {
    public static final FieldDescriptor[] applicationFormResourceFields = {
            fieldWithPath("formInput").description("Form input of the application form"),
            fieldWithPath("applicantResponses").description("Applicant responses of the application form"),
    };
}
