package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Contains the information required when sending an email to notify of an application funding decision.
 */
@SuppressWarnings("unchecked")
public class FundingNotificationResource {
    private String messageBody;
    private Map<Long, Decision> decisions;

    public FundingNotificationResource(String messageBody, Map<Long, Decision> decisions) {
        this.messageBody = messageBody;
        this.decisions = decisions;
    }

    public FundingNotificationResource()
    {
        //default constructor
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public List<Long> calculateApplicationIds() {
        return getDecisions() != null ? new ArrayList<>(getDecisions().keySet()) : Collections.EMPTY_LIST;
    }
    
    public Map<Long, Decision> getDecisions() {
        return decisions;
    }

    public void setDecisions(Map<Long, Decision> decisions) {
        this.decisions = decisions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FundingNotificationResource that = (FundingNotificationResource) o;

        return new EqualsBuilder()
                .append(messageBody, that.messageBody)
                .append(decisions, that.decisions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(messageBody)
                .append(decisions)
                .toHashCode();
    }

}
