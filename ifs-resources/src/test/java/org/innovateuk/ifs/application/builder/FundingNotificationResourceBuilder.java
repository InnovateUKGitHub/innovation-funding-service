package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class FundingNotificationResourceBuilder extends BaseBuilder<FundingNotificationResource, FundingNotificationResourceBuilder> {

    private FundingNotificationResourceBuilder(List<BiConsumer<Integer, FundingNotificationResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected FundingNotificationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FundingNotificationResource>> actions) {
        return new FundingNotificationResourceBuilder(actions);
    }

    public static FundingNotificationResourceBuilder newFundingNotificationResource() {
        return new FundingNotificationResourceBuilder(emptyList())
                .with(uniqueIds());
    }

    public FundingNotificationResourceBuilder withSubject(String... subjects) {
        return withArray((subject, object) -> setField("subject", subject, object), subjects);
    }

    public FundingNotificationResourceBuilder withMessageBody(String... messageBodies) {
        return withArray((messageBody, object) -> setField("messageBody", messageBody, object), messageBodies);
    }

    public FundingNotificationResourceBuilder withFundingDecisions(Map<Long, FundingDecision> fundingDecisions) {
        return with(notification -> setField("fundingDecisions", fundingDecisions, notification));
    }

    @Override
    protected FundingNotificationResource createInitial() {
        return new FundingNotificationResource();
    }
}
