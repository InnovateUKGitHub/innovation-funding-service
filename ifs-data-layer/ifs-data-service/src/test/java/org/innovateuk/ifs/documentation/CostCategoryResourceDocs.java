package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CostCategoryResourceDocs {
    public static final FieldDescriptor[] costCategoryResourceFields = {
            fieldWithPath("id").description("Id of the cost category"),
            fieldWithPath("name").description("Name of the cost category"),
            fieldWithPath("label").description("Label of the cost category"),
    };
}
