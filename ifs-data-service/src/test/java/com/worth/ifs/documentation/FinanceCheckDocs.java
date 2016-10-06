package com.worth.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class FinanceCheckDocs {

    public static final FieldDescriptor[] financeCheckApprovalStatusFields = {
            fieldWithPath("canApprove").description("Is the Finance Check currently in a state where it can be approved"),
            fieldWithPath("currentState").description("The current state of the Finance Check process"),
            fieldWithPath("participant").description("The latest ProjectUser to interact with the Finance Check process"),
            fieldWithPath("internalParticipant").description("The latest internal User to interact with the Finance Check process"),
            fieldWithPath("modifiedDate").description("The latest time that the Finance Check process was updated")
    };
}
