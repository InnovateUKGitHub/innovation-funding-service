package org.innovateuk.ifs.application.resource;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Represents a high-level overview of an application.
 */
public class ApplicationSummaryResource {
    private Long id;
    private String name;
    private String lead;
    private String leadApplicant;
    private String status;
    private Integer completedPercentage;
    private Integer numberOfPartners;
    private BigDecimal grantRequested;
    private BigDecimal totalProjectCost;
    private Long duration;
    private FundingDecision fundingDecision;
    private String innovationArea;
    private ZonedDateTime manageFundingEmailDate;
    private Boolean ineligibleInformed;
    private boolean inAssessmentPanel;

    public ZonedDateTime getManageFundingEmailDate() {
        return manageFundingEmailDate;
    }

    public void setManageFundingEmailDate(ZonedDateTime manageFundingEmailDate) {
        this.manageFundingEmailDate = manageFundingEmailDate;
    }

    public Boolean isIneligibleInformed() {
        return ineligibleInformed;
    }

    public void setIneligibleInformed(Boolean ineligibleInformed) {
        this.ineligibleInformed = ineligibleInformed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Integer getCompletedPercentage() {
        return completedPercentage;
    }

    public void setCompletedPercentage(Integer completedPercentage) {
        this.completedPercentage = completedPercentage;
    }

    public Integer getNumberOfPartners() {
        return numberOfPartners;
    }

    public void setNumberOfPartners(Integer numberOfPartners) {
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean isFunded() {
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

    public Boolean applicationFundingDecisionIsChangeable() {
        if(this.manageFundingEmailDate != null && fundingDecision.equals(FundingDecision.FUNDED)) {
            return false;
        }

        return true;
    }

    public boolean isInAssessmentPanel() {
        return inAssessmentPanel;
    }

    public void setInAssessmentPanel(boolean inAssessmentPanel) {
        this.inAssessmentPanel = inAssessmentPanel;
    }
}
