package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorCountOptionResourceDocs {
    public static final FieldDescriptor[] assessorCountOptionResourceFields = {
            fieldWithPath("[0].id").description("Id of the assessor option"),
            fieldWithPath("[0].competitionType").description("Id of the competition type"),
            fieldWithPath("[0].optionName").description("Assessor option name. Can be used as label"),
            fieldWithPath("[0].optionValue").description("Assessor count value to be stored in the database"),
            fieldWithPath("[0].defaultOption").description("Is this the default option for the given competition type")
    };
}
