package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.NotificationResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.Arrays;

import static org.innovateuk.ifs.application.builder.NotificationResourceBuilder.newNotificationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class NotificationResourceDocs {
    public static final FieldDescriptor[] notificationResourceFields = {
            fieldWithPath("subject").description("The subject of the notification"),
            fieldWithPath("messageBody").description("The message body of the notification"),
            fieldWithPath("applicationIds").description("Ids of each application to notify the lead applicant from"),
    };

    public static final NotificationResourceBuilder notificationResourceBuilder = newNotificationResource()
            .withSubject("subject")
            .withMessageBody("message body")
            .withApplicationIds(Arrays.asList(1L, 2L, 3L));
}
