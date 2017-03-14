package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.NotificationResource;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class NotificationResourceBuilder extends BaseBuilder<NotificationResource, NotificationResourceBuilder> {

    private NotificationResourceBuilder(List<BiConsumer<Integer, NotificationResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected NotificationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, NotificationResource>> actions) {
        return new NotificationResourceBuilder(actions);
    }

    public static NotificationResourceBuilder newNotificationResource() {
        return new NotificationResourceBuilder(emptyList())
                .with(uniqueIds());
    }

    public NotificationResourceBuilder withSubject(String... subjects) {
        return withArray((subject, object) -> setField("subject", subject, object), subjects);
    }

    public NotificationResourceBuilder withMessageBody(String... messageBodies) {
        return withArray((messageBody, object) -> setField("messageBody", messageBody, object), messageBodies);
    }

    public NotificationResourceBuilder withFundingDecisions(Map<Long, FundingDecision> fundingDecisions) {
        return with(notification -> setField("fundingDecisions", fundingDecisions, notification));
    }

    @Override
    protected NotificationResource createInitial() {
        return new NotificationResource();
    }
}
