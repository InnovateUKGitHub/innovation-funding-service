package org.innovateuk.ifs.application.resource;

/**
 * Contains the information required when sending an email to notify of an application funding decision.
 */
public class ApplicationNotificationTemplateResource {
    private String messageBody;

    public ApplicationNotificationTemplateResource() {}

    public ApplicationNotificationTemplateResource(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
