package com.worth.ifs.notifications.domain;

import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.UserNotificationSource;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import org.junit.Test;

import java.util.Map;

import static com.worth.ifs.notifications.domain.NotificationTest.DummyNotification.DUMMY;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.util.MapFunctions.asMap;
import static com.worth.ifs.util.MapFunctions.combineMaps;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Testing the more interesting parts of a Notification
 */
public class NotificationTest {

    enum DummyNotification {
        DUMMY
    }

    @Test
    public void testMergingOfTemplateReplacementsForRecipientsWithoutTargetSpecificReplacements() {

        UserNotificationSource source = new UserNotificationSource(newUser().build());
        UserNotificationTarget target1 = new UserNotificationTarget(newUser().build());
        UserNotificationTarget target2 = new UserNotificationTarget(newUser().build());

        Map<String, Object> globalTemplateReplacements = asMap("thing1", 1L, "thing2", "a string");
        Notification notification = new Notification(source, asList(target1, target2), DUMMY, globalTemplateReplacements);

        assertEquals(globalTemplateReplacements, notification.getTemplateArgumentsForRecipient(target1));
        assertEquals(globalTemplateReplacements, notification.getTemplateArgumentsForRecipient(target2));
    }

    @Test
    public void testMergingOfTemplateReplacementsForRecipientsWithTargetSpecificReplacements() {

        UserNotificationSource source = new UserNotificationSource(newUser().build());
        UserNotificationTarget target1 = new UserNotificationTarget(newUser().build());
        UserNotificationTarget target2 = new UserNotificationTarget(newUser().build());

        Map<String, Object> globalTemplateReplacements = asMap("thing1", Long.valueOf(1L), "thing2", "a string");
        Map<String, Object> target1SpecificTemplateReplacements = asMap("thing2", Long.valueOf(222L), "thing3", Long.valueOf(3L));
        Map<String, Object> expectedTarget1Replacements = combineMaps(asMap("thing1", Long.valueOf(1L)), target1SpecificTemplateReplacements);

        Notification notification = new Notification(source, asList(target1, target2), DUMMY, globalTemplateReplacements, asMap(target1, target1SpecificTemplateReplacements));

        assertEquals(expectedTarget1Replacements, notification.getTemplateArgumentsForRecipient(target1));
        assertEquals(globalTemplateReplacements, notification.getTemplateArgumentsForRecipient(target2));
    }
}
