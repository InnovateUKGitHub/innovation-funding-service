package com.worth.ifs.notifications.builders;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.notifications.resource.NotificationResource;
import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class NotificationResourceBuilder extends BaseBuilder<NotificationResource, NotificationResourceBuilder> {

    private NotificationResourceBuilder(List<BiConsumer<Integer, NotificationResource>> multiActions) {
        super(multiActions);
    }

    public static NotificationResourceBuilder newNotificationResource() {
        return new NotificationResourceBuilder(emptyList());
    }

    @Override
    protected NotificationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, NotificationResource>> actions) {
        return new NotificationResourceBuilder(actions);
    }

    @Override
    protected NotificationResource createInitial() {
        return new NotificationResource();
    }

    public NotificationResourceBuilder withSource(NotificationSource source) {
        return with(notification -> setField("from", source, notification));
    }

    public NotificationResourceBuilder withTargets(List<NotificationTarget> targets) {
        return with(notification -> setField("to", targets, notification));
    }

    public NotificationResourceBuilder withMessageKey(Enum<?> messageKey) {
        return with(notification -> setField("messageKey", messageKey, notification));
    }

    public NotificationResourceBuilder withArguments(Map<String, Object> arguments) {
        return with(notification -> setField("arguments", arguments, notification));
    }
}
