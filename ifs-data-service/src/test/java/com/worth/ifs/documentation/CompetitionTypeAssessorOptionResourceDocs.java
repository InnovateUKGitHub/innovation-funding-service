package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionTypeAssessorOptionResourceDocs {
    public static final FieldDescriptor[] competitionTypeResourceOptionResourceFields = {
            fieldWithPath("[0].id").description("Id of the assessor option"),
            fieldWithPath("[0].competitionTypeId").description("Id of the competition type"),
            fieldWithPath("[0].assessorOptionName").description("Assessor option name. Can be used as label"),
            fieldWithPath("[0].assessorOptionValue").description("Assessor count value to be stored in the database"),
            fieldWithPath("[0].defaultOption").description("Is this the default option for the given competition type")
    };
}
