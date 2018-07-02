package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.email.service.EmailService;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.transactional.TransactionalHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_SINGLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.EMAIL_NOTIFICATION_TEMPLATES_PATH;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * A Notification Sender that can, given a Notification, construct an email from it and use the Email Service to send
 * the email to the given recipients
 */
@Component
@SuppressWarnings("unused")
class EmailNotificationSender implements NotificationSender {

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationTemplateRenderer renderer;

    @Autowired
    private TransactionalHelper transactionalHelper;

    @Override
    public NotificationMedium getNotificationMedium() {
        return EMAIL;
    }

    @Override
    public ServiceResult<Notification> sendNotification(Notification notification) {

        return renderTemplates(notification).andOnSuccess(templates -> {

            List<ServiceResult<List<EmailAddress>>> results = simpleMap(templates, (target, content) ->
                    sendEmailWithContent(notification, target, content));

            return processAnyFailuresOrSucceed(results, failures -> {
                Error error = new Error(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE, findStatusCode(failures));
                return serviceFailure(error);
            }, serviceSuccess(notification));
        });
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public ServiceResult<Notification> sendNotificationWithFlush(Notification notification) {

        // flush any pending SQL updates to the database before proceeding to send the Notification, in case any SQL
        // issues occur
        transactionalHelper.flushWithNoCommit();

        // then it's safe to go ahead and attempt to send out the Notification
        return sendNotification(notification);
    }

    private ServiceResult<Map<NotificationTarget, EmailContent>> renderTemplates(Notification notification) {

        Map<NotificationTarget, EmailContent> contents = new HashMap<>();

        List<ServiceResult<Void>> results = simpleMap(notification.getTo(), recipient ->

                find(getSubject(notification, recipient),
                        getPlainTextBody(notification, recipient),
                        getHtmlBody(notification, recipient)).andOnSuccessReturnVoid((subject, text, html) -> {

                    contents.put(recipient, new EmailContent(subject, text, html));
                })
        );

        return aggregate(results).andOnSuccessReturn(() -> contents);
    }

    private ServiceResult<List<EmailAddress>> sendEmailWithContent(Notification notification, NotificationTarget recipient, EmailContent emailContent) {
        return emailService.sendEmail(
                EmailAddressResolver.fromNotificationSource(notification.getFrom()),
                singletonList(EmailAddressResolver.fromNotificationTarget(recipient)),
                emailContent.getSubject(),
                emailContent.getPlainText(),
                emailContent.getHtmlText());
    }

    private ServiceResult<String> getSubject(Notification notification, NotificationTarget recipient) {
        return renderer.renderTemplate(notification.getFrom(), recipient, getTemplatePath(notification, "subject") + ".txt",
                notification.getTemplateArgumentsForRecipient(recipient));
    }

    private ServiceResult<String> getPlainTextBody(Notification notification, NotificationTarget recipient) {
        return renderer.renderTemplate(notification.getFrom(), recipient, getTemplatePath(notification, "text_plain") + ".txt",
                notification.getTemplateArgumentsForRecipient(recipient));
    }

    private ServiceResult<String> getHtmlBody(Notification notification, NotificationTarget recipient) {
        return renderer.renderTemplate(notification.getFrom(), recipient, getTemplatePath(notification, "text_html") + ".html",
                notification.getTemplateArgumentsForRecipient(recipient));
    }

    private String getTemplatePath(Notification notification, String suffix) {
        return EMAIL_NOTIFICATION_TEMPLATES_PATH + notification.getMessageKey().name().toLowerCase() + "_" + suffix;
    }
}
