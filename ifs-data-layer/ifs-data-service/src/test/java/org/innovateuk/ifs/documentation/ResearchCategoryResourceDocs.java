package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ResearchCategoryResourceDocs {

    public static final FieldDescriptor[] researchCategoryResourceFields = {
            fieldWithPath("id").description("The id of the research category"),
            fieldWithPath("name").description("The name of the research category"),
            fieldWithPath("description").description("The description of the research category"),
            fieldWithPath("priority").description("The priority of the research category"),
            fieldWithPath("type").description("The type of the research category"),
    };
}
