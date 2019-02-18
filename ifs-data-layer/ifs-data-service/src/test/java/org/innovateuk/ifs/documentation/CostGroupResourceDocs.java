package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CostGroupResourceDocs {
    public static final FieldDescriptor[] costGroupResourceFields = {
            fieldWithPath("id").description("Id of the costs group"),
            fieldWithPath("costs").description("Costs of the cost group"),
            fieldWithPath("description").description("Description of the cost group"),
    };
}
