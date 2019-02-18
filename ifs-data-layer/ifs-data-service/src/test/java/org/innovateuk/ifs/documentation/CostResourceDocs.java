package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CostResourceDocs {
    public static final FieldDescriptor[] costResourceFields = {
            fieldWithPath("id").description("Id of the cost"),
            fieldWithPath("value").description("Value of the cost"),
            fieldWithPath("costTimePeriod").description("Cost time period of the cost"),
            fieldWithPath("costCategory").description("Cost category of the cost"),
    };
}
