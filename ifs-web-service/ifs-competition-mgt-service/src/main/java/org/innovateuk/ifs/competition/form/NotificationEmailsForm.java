package org.innovateuk.ifs.competition.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class NotificationEmailsForm {

    @NotEmpty (message="{validation.field.must.not.be.blank}")
    private String summary;

    @NotEmpty(message="{validation.field.must.not.be.blank}")
    private String message;

    @NotNull
    private List<Long> ids;

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

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
