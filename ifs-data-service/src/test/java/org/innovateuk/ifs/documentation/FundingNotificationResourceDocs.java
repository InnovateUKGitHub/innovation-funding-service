package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.FundingNotificationResourceBuilder;
import org.innovateuk.ifs.util.MapFunctions;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.application.builder.FundingNotificationResourceBuilder.newFundingNotificationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.innovateuk.ifs.application.resource.FundingDecision.ON_HOLD;
import static org.innovateuk.ifs.application.resource.FundingDecision.UNFUNDED;

public class FundingNotificationResourceDocs {
    public static final FieldDescriptor[] fundingNotificationResourceFields = {
            fieldWithPath("messageBody").description("The message body of the funding notification"),
            fieldWithPath("fundingDecisions").description("Map which holds the funding decision per application for which to notify the lead applicant"),
    };

    public static final FundingNotificationResourceBuilder FUNDING_NOTIFICATION_RESOURCE_BUILDER = newFundingNotificationResource()
            .withMessageBody("message body")
            .withFundingDecisions(MapFunctions.asMap(1L, FUNDED, 2L, UNFUNDED, 3L, ON_HOLD));
}
