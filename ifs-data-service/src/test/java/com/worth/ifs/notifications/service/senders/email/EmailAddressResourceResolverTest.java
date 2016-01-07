package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.email.resource.EmailAddressResource;
import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.UserNotificationSourceResource;
import com.worth.ifs.notifications.resource.UserNotificationTargetResource;
import com.worth.ifs.user.domain.User;
import org.junit.Test;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

/**
 * Tests around the resolving of email addresses for Notification sources and targets of different types
 */
public class EmailAddressResourceResolverTest {

    @Test
    public void testFromNotificationSourceWithUserNotificationSource() {

        User user = newUser().with(name("My User")).withEmailAddress("my@email.com").build();
        UserNotificationSourceResource notificationSource = new UserNotificationSourceResource(user);

        EmailAddressResource resolvedEmailAddress = EmailAddressResourceResolver.fromNotificationSource(notificationSource);
        assertEquals("My User", resolvedEmailAddress.getName());
        assertEquals("my@email.com", resolvedEmailAddress.getEmailAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromNotificationSourceWithUnsupportedNotificationSource() {
        NotificationSource unknownSource = new NotificationSource() {};
        EmailAddressResourceResolver.fromNotificationSource(unknownSource);
    }

    @Test
    public void testFromNotificationTargetWithUserNotificationTarget() {

        User user = newUser().with(name("My User")).withEmailAddress("my@email.com").build();
        UserNotificationTargetResource notificationTarget = new UserNotificationTargetResource(user);

        EmailAddressResource resolvedEmailAddress = EmailAddressResourceResolver.fromNotificationTarget(notificationTarget);
        assertEquals("My User", resolvedEmailAddress.getName());
        assertEquals("my@email.com", resolvedEmailAddress.getEmailAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromNotificationTargetWithUnsupportedNotificationTarget() {
        NotificationTarget unknownTarget = new NotificationTarget() {};
        EmailAddressResourceResolver.fromNotificationTarget(unknownTarget);
    }

}
