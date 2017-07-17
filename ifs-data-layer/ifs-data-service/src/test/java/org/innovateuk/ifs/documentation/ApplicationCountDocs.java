package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationCountDocs {
    public static final FieldDescriptor[] applicationCountSummaryResourcesFields = {
            fieldWithPath("totalElements").description("The total number of application counts"),
            fieldWithPath("totalPages").description("The total number of pages of application counts"),
            fieldWithPath("number").description("Current page number"),
            fieldWithPath("size").description("The size of a page"),
            fieldWithPath("content").description("The list of application counts"),
            fieldWithPath("content.[].id").description("ID of the application"),
            fieldWithPath("content.[].name").description("Name of the application"),
            fieldWithPath("content.[].leadOrganisation").description("The lead organisation name"),
            fieldWithPath("content.[].assessors").description("Count of assessors"),
            fieldWithPath("content.[].accepted").description("Count of accepted assessments"),
            fieldWithPath("content.[].submitted").description("Count of submitted assessments")
    };

    public static final ApplicationCountSummaryResourceBuilder applicationCountSummaryResourceBuilder = newApplicationCountSummaryResource()
            .withId(1L)
            .withName("application name")
            .withLeadOrganisation("lead organisation name")
            .withAssessors(4L)
            .withAccepted(2L)
            .withSubmitted(1L);
}
