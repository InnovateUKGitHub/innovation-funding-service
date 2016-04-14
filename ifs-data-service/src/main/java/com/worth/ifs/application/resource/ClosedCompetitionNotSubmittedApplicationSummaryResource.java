package com.worth.ifs.application.resource;

public class ClosedCompetitionNotSubmittedApplicationSummaryResource {

	private Long id;
	private String name;
	private String lead;
	private Integer completedPercentage;
	
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
	public Integer getCompletedPercentage() {
		return completedPercentage;
	}
	public void setCompletedPercentage(Integer completedPercentage) {
		this.completedPercentage = completedPercentage;
	}
	
}
