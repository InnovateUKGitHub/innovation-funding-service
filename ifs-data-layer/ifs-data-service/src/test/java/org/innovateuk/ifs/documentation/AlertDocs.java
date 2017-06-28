package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

/**
 * Helper for Spring REST Docs, specifically for alerts.
 */
public class AlertDocs {

    public static final FieldDescriptor[] alertResourceFields = {
            fieldWithPath("id").description("id of the alert"),
            fieldWithPath("message").description("message of the alert"),
            fieldWithPath("type").description("type of the alert"),
            fieldWithPath("validFromDate").description("date that the alert is visible from"),
            fieldWithPath("validToDate").description("date that the alert is visible until")
    };

}
