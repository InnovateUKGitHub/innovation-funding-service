package org.innovateuk.ifs.application.resource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Represents a high-level overview of an application.
 */
public class ApplicationSummaryResource {
    private long id;
    private String name;
    private String lead;
    private String leadApplicant;
    private String status;
    private int completedPercentage;
    private int numberOfPartners;
    private BigDecimal grantRequested;
    private BigDecimal totalProjectCost;
    private long duration;
    private FundingDecision fundingDecision;
    private String innovationArea;
    private ZonedDateTime manageFundingEmailDate;
    private boolean ineligibleInformed;

    public ZonedDateTime getManageFundingEmailDate() {
        return manageFundingEmailDate;
    }

    public void setManageFundingEmailDate(ZonedDateTime manageFundingEmailDate) {
        this.manageFundingEmailDate = manageFundingEmailDate;
    }

    public boolean isIneligibleInformed() {
        return ineligibleInformed;
    }

    public void setIneligibleInformed(boolean ineligibleInformed) {
        this.ineligibleInformed = ineligibleInformed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(String leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCompletedPercentage() {
        return completedPercentage;
    }

    public void setCompletedPercentage(int completedPercentage) {
        this.completedPercentage = completedPercentage;
    }

    public int getNumberOfPartners() {
        return numberOfPartners;
    }

    public void setNumberOfPartners(int numberOfPartners) {
        this.numberOfPartners = numberOfPartners;
    }

    public BigDecimal getGrantRequested() {
        return grantRequested;
    }

    public void setGrantRequested(BigDecimal grantRequested) {
        this.grantRequested = grantRequested;
    }

    public BigDecimal getTotalProjectCost() {
        return totalProjectCost;
    }

    public void setTotalProjectCost(BigDecimal totalProjectCost) {
        this.totalProjectCost = totalProjectCost;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isFunded() {
        return FundingDecision.FUNDED.equals(fundingDecision);
    }

    public FundingDecision getFundingDecision() {
        return fundingDecision;
    }

    public void setFundingDecision(FundingDecision fundingDecision) {
        this.fundingDecision = fundingDecision;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(String innovationArea) {
        this.innovationArea = innovationArea;
    }

    public boolean applicationFundingDecisionIsChangeable() {
        if(this.manageFundingEmailDate != null && fundingDecision.equals(FundingDecision.FUNDED)) {
            return false;
        }

        return true;
    }
}
