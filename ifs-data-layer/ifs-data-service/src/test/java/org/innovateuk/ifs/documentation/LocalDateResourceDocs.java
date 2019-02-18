package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class LocalDateResourceDocs {
    public static final FieldDescriptor[] localDateFields = {
            fieldWithPath("day").description("The day of the local date"),
            fieldWithPath("month").description("The month of the local date"),
            fieldWithPath("year").description("The year of the local date"),
    };
}
