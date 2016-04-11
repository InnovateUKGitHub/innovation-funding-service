package com.worth.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a high-level overview of an application.
 */
public class ApplicationSummaryResource {
    private Long id;
    private String name;
    private String lead;
    private String status;
    private Integer completedPercentage;
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
	
    
}
