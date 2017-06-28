package org.innovateuk.ifs.notifications.resource;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * Test that Spring is correctly injecting the correct System values into SystemNotificationSource
 */
public class SystemNotificationSourceIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private SystemNotificationSource source;

    @Test
    public void testNameAndEmailAddressSetCorrectly() {
        assertEquals("Innovation Funding Service", source.getName());
        assertEquals("noreply-innovateuk@example.com", source.getEmailAddress());
    }
}
