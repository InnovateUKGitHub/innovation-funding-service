package org.innovateuk.ifs.notifications.builders;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationSource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class NotificationBuilder extends BaseBuilder<Notification, NotificationBuilder> {

    private NotificationBuilder(List<BiConsumer<Integer, Notification>> multiActions) {
        super(multiActions);
    }

    public static NotificationBuilder newNotification() {
        return new NotificationBuilder(emptyList());
    }

    @Override
    protected NotificationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Notification>> actions) {
        return new NotificationBuilder(actions);
    }

    @Override
    protected Notification createInitial() {
        return newInstance(Notification.class);
    }

    public NotificationBuilder withSource(NotificationSource source) {
        return with(notification -> setField("from", source, notification));
    }

    public NotificationBuilder withTargets(List<NotificationTarget> targets) {
        return with(notification -> setField("to", targets, notification));
    }

    public NotificationBuilder withMessageKey(Enum<?> messageKey) {
        return with(notification -> setField("messageKey", messageKey, notification));
    }

    public NotificationBuilder withArguments(Map<String, Object> arguments) {
        return with(notification -> setField("arguments", arguments, notification));
    }
}
