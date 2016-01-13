package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.notifications.resource.ExternalUserNotificationTarget;
import com.worth.ifs.notifications.resource.UserNotificationSource;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import com.worth.ifs.user.domain.User;
import org.junit.Test;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

/**
 * Tests around the resolving of email addresses for Notification sources and targets of different types
 */
public class EmailAddressResolverTest {

    @Test
    public void testFromNotificationSourceWithUserNotificationSource() {

        User user = newUser().with(name("My User")).withEmailAddress("my@email.com").build();
        UserNotificationSource notificationSource = new UserNotificationSource(user);

        EmailAddress resolvedEmailAddress = EmailAddressResolver.fromNotificationSource(notificationSource);
        assertEquals("My User", resolvedEmailAddress.getName());
        assertEquals("my@email.com", resolvedEmailAddress.getEmailAddress());
    }

    @Test
    public void testFromNotificationTargetWithUserNotificationTarget() {

        User user = newUser().with(name("My User")).withEmailAddress("my@email.com").build();
        UserNotificationTarget notificationTarget = new UserNotificationTarget(user);

        EmailAddress resolvedEmailAddress = EmailAddressResolver.fromNotificationTarget(notificationTarget);
        assertEquals("My User", resolvedEmailAddress.getName());
        assertEquals("my@email.com", resolvedEmailAddress.getEmailAddress());
    }

    @Test
    public void testFromNotificationSourceWithExternalUserNotificationTarget() {

        ExternalUserNotificationTarget notificationSource = new ExternalUserNotificationTarget("My User", "my@email.com");

        EmailAddress resolvedEmailAddress = EmailAddressResolver.fromNotificationTarget(notificationSource);
        assertEquals("My User", resolvedEmailAddress.getName());
        assertEquals("my@email.com", resolvedEmailAddress.getEmailAddress());
    }
}
