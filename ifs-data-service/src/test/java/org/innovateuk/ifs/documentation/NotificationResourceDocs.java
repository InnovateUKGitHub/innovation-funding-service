package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.NotificationResourceBuilder;
import org.innovateuk.ifs.util.MapFunctions;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.application.builder.NotificationResourceBuilder.newNotificationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.innovateuk.ifs.application.resource.FundingDecision.ON_HOLD;
import static org.innovateuk.ifs.application.resource.FundingDecision.UNFUNDED;

public class NotificationResourceDocs {
    public static final FieldDescriptor[] notificationResourceFields = {
            fieldWithPath("subject").description("The subject of the notification"),
            fieldWithPath("messageBody").description("The message body of the notification"),
            fieldWithPath("fundingDecisions").description("Map which holds the funding decision per application for which to notify the lead applicant"),
    };

    public static final NotificationResourceBuilder notificationResourceBuilder = newNotificationResource()
            .withSubject("subject")
            .withMessageBody("message body")
            .withFundingDecisions(MapFunctions.asMap(1L, FUNDED, 2L, UNFUNDED, 3L, ON_HOLD));
}
