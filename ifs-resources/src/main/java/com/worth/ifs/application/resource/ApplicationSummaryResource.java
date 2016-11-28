package com.worth.ifs.application.resource;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JsonIgnore
	public String getFormattedId(){	return ApplicationResource.formatter.format(id); }
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
	public boolean isFunded() {
		return FundingDecision.FUNDED.equals(fundingDecision);
	}
	public FundingDecision getFundingDecision() {
		return fundingDecision;
	}
	public void setFundingDecision(FundingDecision fundingDecision) {
		this.fundingDecision = fundingDecision;
	}

}
