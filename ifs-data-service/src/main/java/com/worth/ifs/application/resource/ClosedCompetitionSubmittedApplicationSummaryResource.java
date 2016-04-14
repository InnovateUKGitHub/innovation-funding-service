package com.worth.ifs.application.resource;

import java.math.BigDecimal;

public class ClosedCompetitionSubmittedApplicationSummaryResource {

	private Long id;
	private String name;
	private String lead;
	private Integer numberOfPartners;
	private BigDecimal grantRequested;
	private BigDecimal totalProjectCost;
	private Long duration;
	
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
}
