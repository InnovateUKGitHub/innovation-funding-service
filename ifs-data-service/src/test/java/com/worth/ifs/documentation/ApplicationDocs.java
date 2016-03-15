package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationDocs {
    public static final FieldDescriptor[] applicationResourceFields = {
            fieldWithPath("id").description("Id of the application"),
            fieldWithPath("name").description("Name of the application"),
            fieldWithPath("startDate").description("Estimated timescales: project start date"),
            fieldWithPath("submittedDate").description("The date the applicant has submitted this application."),
            fieldWithPath("durationInMonths").description("Estimated timescales: project duration in months"),
            fieldWithPath("processRoles").description("list of ProcessRole Id's"),
            fieldWithPath("applicationStatus").description("ApplicationStatus Id"),
            fieldWithPath("applicationStatusName").description("ApplicationStatus name"),
            fieldWithPath("competition").description("Competition Id"),
            fieldWithPath("competitionName").description("Competition Name"),
            fieldWithPath("applicationFinances").description("list of ApplicationFinance Id's")
    };
}
