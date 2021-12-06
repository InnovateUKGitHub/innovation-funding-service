package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.FundingNotificationResourceBuilder;
import org.innovateuk.ifs.util.MapFunctions;

import static org.innovateuk.ifs.application.builder.FundingNotificationResourceBuilder.newFundingNotificationResource;
import static org.innovateuk.ifs.application.resource.FundingDecision.*;

public class FundingNotificationResourceDocs {

    public static final FundingNotificationResourceBuilder FUNDING_NOTIFICATION_RESOURCE_BUILDER = newFundingNotificationResource()
            .withMessageBody("message body")
            .withFundingDecisions(MapFunctions.asMap(1L, FUNDED, 2L, UNFUNDED, 3L, ON_HOLD));
}
