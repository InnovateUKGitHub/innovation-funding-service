package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.email.service.EmailService;
import org.innovateuk.ifs.transactional.TransactionalHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EmailNotificationSender.class},
    properties = {
        "ifs.emailNotification.blacklist=example.com,another.com",
        "ifs.emailNotification.whitelist=test.org,foo.com"})
public class WhiteBlackDomainFilterComponentTest {

    @Autowired
    private EmailNotificationSender emailNotificationSender;

    @MockBean
    private EmailService emailService;

    @MockBean
    private NotificationTemplateRenderer notificationTemplateRenderer;

    @MockBean
    private TransactionalHelper transactionalHelper;

    @Test
    public void componentTest() {
        // Check the spring EL works from specified properties
        assertThat(emailNotificationSender.whitelist.size(), equalTo(2));
        assertThat(emailNotificationSender.whitelist.get(0), equalTo("test.org"));
        assertThat(emailNotificationSender.whitelist.get(1), equalTo("foo.com"));

        assertThat(emailNotificationSender.blacklist.size(), equalTo(2));
        assertThat(emailNotificationSender.blacklist.get(0), equalTo("example.com"));
        assertThat(emailNotificationSender.blacklist.get(1), equalTo("another.com"));
    }

}