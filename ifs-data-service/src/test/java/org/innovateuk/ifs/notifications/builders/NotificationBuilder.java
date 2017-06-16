package org.innovateuk.ifs.notifications.builders;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationSource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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

    public NotificationBuilder withSource(NotificationSource ...source) {
        return withArraySetFieldByReflection("from", source);
    }

    public NotificationBuilder withTargets(List<NotificationTarget> ...targets) {
        return withArraySetFieldByReflection("to", targets);
    }

    public NotificationBuilder withMessageKey(Enum<?> ...messageKey) {
        return withArraySetFieldByReflection("messageKey", messageKey);
    }

    public NotificationBuilder withGlobalArguments(Map<String, Object> ...arguments) {
        return withArraySetFieldByReflection("globalArguments", arguments);
    }

    public NotificationBuilder withPerNotificationTargetArguments(Map<NotificationTarget, Map<String, Object>> ...arguments) {
        return withArraySetFieldByReflection("perNotificationTargetArguments", arguments);
    }
}
