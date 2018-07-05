package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionOpenQueryResourceDocs {

    public static final FieldDescriptor[] competitionOpenQueryFields = {
            fieldWithPath("applicationId").description("Application Id"),
            fieldWithPath("organisationId").description("Organisation Id"),
            fieldWithPath("organisationName").description("organisation name"),
            fieldWithPath("projectId").description("Project Id"),
            fieldWithPath("projectName").description("Project Name"),
    };
}
