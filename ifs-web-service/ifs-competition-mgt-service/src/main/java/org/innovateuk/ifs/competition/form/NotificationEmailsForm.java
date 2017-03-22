package org.innovateuk.ifs.competition.form;

import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.application.resource.FundingDecision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotificationEmailsForm {

    @NotEmpty (message="{validation.manage.funding.notifications.title.required}")
    private String subject;

    @NotEmpty(message="{validation.manage.funding.notifications.message.required}")
    private String message;

    @NotEmpty(message="{validation.manage.funding.applications.no.application.selected}")
    private Map<Long, FundingDecision> fundingDecisions;

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
        return getFundingDecisions() != null ? new ArrayList<>(getFundingDecisions().keySet()) : Collections.EMPTY_LIST;
    }

    public Map<Long, FundingDecision> getFundingDecisions() {
        return fundingDecisions;
    }

    public void setFundingDecisions(Map<Long, FundingDecision> fundingDecisions) {
        this.fundingDecisions = fundingDecisions;
    }
}
