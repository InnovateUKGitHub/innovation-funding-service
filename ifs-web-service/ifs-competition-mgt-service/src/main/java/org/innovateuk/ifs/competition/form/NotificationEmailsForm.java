package org.innovateuk.ifs.competition.form;

import org.hibernate.validator.constraints.NotEmpty;

public class NotificationEmailsForm {

    @NotEmpty (message="{validation.field.must.not.be.blank}")
    private String summary;

    @NotEmpty(message="{validation.field.must.not.be.blank}")
    private String message;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
