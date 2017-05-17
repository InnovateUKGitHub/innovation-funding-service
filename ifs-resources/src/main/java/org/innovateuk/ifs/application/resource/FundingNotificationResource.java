package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;

/**
 * Contains the information required when sending an email to notify of an application funding decision.
 */
public class FundingNotificationResource {
    private String messageBody;
    private Map<Long, FundingDecision> fundingDecisions;

    public FundingNotificationResource(String messageBody, Map<Long, FundingDecision> fundingDecisions) {
        this.messageBody = messageBody;
        this.fundingDecisions = fundingDecisions;
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
        return getFundingDecisions() != null ? new ArrayList<>(getFundingDecisions().keySet()) : Collections.EMPTY_LIST;
    }
    
    public Map<Long, FundingDecision> getFundingDecisions() {
        return fundingDecisions;
    }

    public void setFundingDecisions(Map<Long, FundingDecision> fundingDecisions) {
        this.fundingDecisions = fundingDecisions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FundingNotificationResource that = (FundingNotificationResource) o;

        return new EqualsBuilder()
                .append(messageBody, that.messageBody)
                .append(fundingDecisions, that.fundingDecisions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(messageBody)
                .append(fundingDecisions)
                .toHashCode();
    }

}
