package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class NotificationResourceDocs {
    public static final FieldDescriptor[] notificationResourceFields = {
            fieldWithPath("subject").description("The subject of the notification"),
            fieldWithPath("messageBody").description("The message body of the notification"),
            fieldWithPath("applicationIds").description("Ids of each application to notify the lead applicant from"),

    };

    //TODO: Add Notification resource builder
}
