package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InnovationAreaResourceDocs {
    public static final FieldDescriptor[] innovationAreaResourceFields = {
            fieldWithPath("id").description("The id of the innovation area"),
            fieldWithPath("name").description("The name of the innovation area"),
            fieldWithPath("description").description("The description of the innovation area"),
            fieldWithPath("priority").description("The priority of the innovation area"),
            fieldWithPath("sector").description("The sector of the innovation area"),
            fieldWithPath("sectorName").description("The sector name of the innovation area"),
            fieldWithPath("type").description("The type of the innovation area"),
    };

}
