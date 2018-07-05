package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class SpendProfileStatusResourceDocs {

    public static final FieldDescriptor[] spendProfileStatusFields = {
            fieldWithPath("applicationId").description("The application id of the spend profile status"),
            fieldWithPath("projectId").description("The project id of the spend profile status"),
            fieldWithPath("projectName").description("The project name of the spend profile status"),
    };
}
