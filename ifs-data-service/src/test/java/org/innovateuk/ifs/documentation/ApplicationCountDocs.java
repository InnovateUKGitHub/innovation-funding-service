package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationCountDocs {
    public static final FieldDescriptor[] applicationCountSummaryResourcesFields = {
            fieldWithPath("[].id").description("ID of the application"),
            fieldWithPath("[].name").description("Name of the application"),
            fieldWithPath("[].leadOrganisationId").description("The lead organisation ID"),
            fieldWithPath("[].assessors").description("Count of assessors"),
            fieldWithPath("[].accepted").description("Count of accepted assessments"),
            fieldWithPath("[].submitted").description("Count of submitted assessments")
    };

    public static final ApplicationCountSummaryResourceBuilder applicationCountSummaryResourceBuilder = newApplicationCountSummaryResource()
            .withId(1L)
            .withName("application name")
            .withLeadOrganisationId(1L)
            .withAssessors(4L)
            .withAccepted(2L)
            .withSubmitted(1L);
}
