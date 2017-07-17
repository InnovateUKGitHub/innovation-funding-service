package org.innovateuk.ifs.notifications.service.senders.email;

import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.UserNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;

import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

/**
 * Tests around the resolving of email addresses for Notification sources and targets of different types
 */
public class EmailAddressResolverTest {

    @Test
    public void testFromNotificationSourceWithUserNotificationSource() {

        User user = newUser().withFirstName("My").withLastName("User").withEmailAddress("my@email.com").build();
        UserNotificationSource notificationSource = new UserNotificationSource(user);

        EmailAddress resolvedEmailAddress = EmailAddressResolver.fromNotificationSource(notificationSource);
        assertEquals("My User", resolvedEmailAddress.getName());
        assertEquals("my@email.com", resolvedEmailAddress.getEmailAddress());
    }

    @Test
    public void testFromNotificationTargetWithUserNotificationTarget() {

        User user = newUser().withFirstName("My").withLastName("User").withEmailAddress("my@email.com").build();
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
