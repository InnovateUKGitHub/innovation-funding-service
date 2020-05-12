package org.innovateuk.ifs.application.resource;

/**
 * Contains the information required when sending an email to notify of an application funding decision.
 */
public class FundingDecisionToSendApplicationResource {
    private long id;
    private String title;
    private String lead;
    private FundingDecision fundingDecision;

    private FundingDecisionToSendApplicationResource() {}

    public FundingDecisionToSendApplicationResource(long id, String title, String lead, FundingDecision fundingDecision) {
        this.id = id;
        this.title = title;
        this.lead = lead;
        this.fundingDecision = fundingDecision;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLead() {
        return lead;
    }

    public FundingDecision getFundingDecision() {
        return fundingDecision;
    }
}