package org.innovateuk.ifs.management.decision.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import org.innovateuk.ifs.application.resource.Decision;
import java.util.*;

public class NotificationEmailsForm {

    @NotBlank(message="{validation.manage.funding.notifications.message.required}")
    private String message;

    @NotEmpty(message="{validation.manage.funding.applications.no.application.selected}")
    private Map<Long, Decision> decisions;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Long> getApplicationIds() {
        return getDecisions() != null ? new ArrayList<>(getDecisions().keySet()) : Collections.EMPTY_LIST;
    }

    public Map<Long, Decision> getDecisions() {
        return decisions;
    }

    public void setDecisions(Map<Long, Decision> decisions) {
        this.decisions = decisions;
    }
}
