package com.worth.ifs.competition.resource;

public enum CompetitionSetupSection {

	INITIAL_DETAILS("initial", "Initial Details"),
	ADDITIONAL_INFO("additional", "Additional Information"),
	ELIGIBILITY("eligibility", "Eligibility"),
	MILESTONES("milestones", "Milestones"),
	ASSESSORS("assessors", "Assessors"),
	APPLICATION_FORM("application", "Application Form"),
	FINANCE("finance", "Finance");
	
	private String path;
	private String name;
	
	private CompetitionSetupSection(String path, String name) {
		this.path = path;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
}
