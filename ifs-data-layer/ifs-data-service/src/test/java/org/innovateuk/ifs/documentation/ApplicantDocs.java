package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicantDocs {
    public static final FieldDescriptor[] applicantResourceFields = {
            fieldWithPath("processRole").description("Process role of the applicant"),
            fieldWithPath("organisation").description("Organisation of the applicant"),
            fieldWithPath("name").description("Name of the applicant"),
    };
}
