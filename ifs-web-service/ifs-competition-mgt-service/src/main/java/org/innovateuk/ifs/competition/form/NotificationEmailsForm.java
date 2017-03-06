package org.innovateuk.ifs.competition.form;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class NotificationEmailsForm {

    @NotEmpty (message="{validation.field.must.not.be.blank}")
    private String subject;

    @NotEmpty(message="{validation.field.must.not.be.blank}")
    private String message;

    @NotEmpty(message="{validation.manage.funding.applications.no.application.selected}")
    private List<Long> applicationIds;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Long> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(List<Long> applicationIds) {
        this.applicationIds = applicationIds;
    }
}
