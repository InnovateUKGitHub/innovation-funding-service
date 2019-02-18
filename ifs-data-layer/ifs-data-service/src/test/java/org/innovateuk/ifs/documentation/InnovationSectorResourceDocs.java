package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InnovationSectorResourceDocs {
    public static final FieldDescriptor[] innovationSectorResourceFields = {
            fieldWithPath("id").description("The id of the innovation sector"),
            fieldWithPath("name").description("The name of the innovation sector"),
            fieldWithPath("description").description("The description of the innovation sector"),
            fieldWithPath("priority").description("The priority of the innovation sector"),
            fieldWithPath("type").description("The type of the innovation sector"),
    };
}
