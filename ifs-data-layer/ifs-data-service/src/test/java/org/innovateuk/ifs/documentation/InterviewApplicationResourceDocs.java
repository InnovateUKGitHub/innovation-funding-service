package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewApplicationResourceDocs extends PageResourceDocs {

    public static final FieldDescriptor[] InterviewApplicationResourceFields = {
            fieldWithPath("id").description("Id of the application"),
            fieldWithPath("name").description("Name of the application"),
            fieldWithPath("leadOrganisation").description("The name of the lead organisation"),
            fieldWithPath("numberOfAssessors").description("The number of assessors already allocated to the application"),
    };

}
