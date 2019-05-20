package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class QuestionStatusResourceDocs {

    public static final FieldDescriptor[] questionStatusResourceFields = {
            fieldWithPath("id").description("The id of the question status"),
            fieldWithPath("markedAsComplete").description("True if the question status has been marked as complete"),
            fieldWithPath("markedAsCompleteBy").description("The id of the process role that marked the question status as complete"),
            fieldWithPath("markedAsCompleteByUserId").description("The id of the user that marked the question status as complete"),
            fieldWithPath("markedAsCompleteByUserName").description("The name of the user that marked the question status as complete"),
            fieldWithPath("markedAsCompleteOn").description("The timestamp when the question status was marked as complete"),
            fieldWithPath("question").description("The question of the question status"),
            fieldWithPath("assignee").description("The assignee of the question status"),
            fieldWithPath("assignedDate").description("The assigned date of the question status"),
            fieldWithPath("application").description("The application of the question status"),
            fieldWithPath("assignedBy").description("The assigned by of the question status"),
            fieldWithPath("notified").description("The notified of the question status"),
            fieldWithPath("assigneeName").description("The assignee name of the question status"),
            fieldWithPath("assignedByName").description("The assigned by name of the question status"),
            fieldWithPath("assigneeUserId").description("The assignee user id of the question status"),
            fieldWithPath("assignedByUserId").description("The assigned by user id of the question status"),
    };
}
