package org.innovateuk.ifs.notifications.domain;

import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMessage;
import org.innovateuk.ifs.notifications.resource.UserNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.junit.Test;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.notifications.domain.NotificationTest.DummyNotification.DUMMY;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.util.MapFunctions.combineMaps;
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

        UserNotificationSource source = new UserNotificationSource("1", "1@1.1");
        NotificationMessage target1 = new NotificationMessage(new UserNotificationTarget("2", "2@2.2"));
        NotificationMessage target2 = new NotificationMessage(new UserNotificationTarget("3", "3@3.3"));

        Map<String, Object> globalTemplateReplacements = asMap("thing1", 1L, "thing2", "a string");
        Notification notification = new Notification(source, asList(target1, target2), DUMMY, globalTemplateReplacements);

        assertEquals(globalTemplateReplacements, notification.getTemplateArgumentsForRecipient(target1));
        assertEquals(globalTemplateReplacements, notification.getTemplateArgumentsForRecipient(target2));
    }

    @Test
    public void testMergingOfTemplateReplacementsForRecipientsWithTargetSpecificReplacements() {
        Map<String, Object> globalTemplateReplacements = asMap("thing1", Long.valueOf(1L), "thing2", "a string");
        Map<String, Object> target1SpecificTemplateReplacements = asMap("thing2", Long.valueOf(222L), "thing3", Long.valueOf(3L));

        UserNotificationSource source = new UserNotificationSource("1", "1@1.1");
        NotificationMessage target1 = new NotificationMessage(new UserNotificationTarget("2", "2@2.2"), target1SpecificTemplateReplacements);
        NotificationMessage target2 = new NotificationMessage(new UserNotificationTarget("3", "3@3.3"));

        Notification notification = new Notification(source, asList(target1, target2), DUMMY, globalTemplateReplacements);

        Map<String, Object> expectedTarget1Replacements = combineMaps(asMap("thing1", Long.valueOf(1L)), target1SpecificTemplateReplacements);

        assertEquals(expectedTarget1Replacements, notification.getTemplateArgumentsForRecipient(target1));
        assertEquals(globalTemplateReplacements, notification.getTemplateArgumentsForRecipient(target2));
    }
}
