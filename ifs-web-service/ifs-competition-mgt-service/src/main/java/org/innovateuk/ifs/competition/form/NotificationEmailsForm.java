package org.innovateuk.ifs.competition.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class NotificationEmailsForm {

    @NotEmpty (message="{validation.field.must.not.be.blank}")
    private String subject;

    @NotEmpty(message="{validation.field.must.not.be.blank}")
    private String message;

    @NotNull
    private List<Long> ids;

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

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
