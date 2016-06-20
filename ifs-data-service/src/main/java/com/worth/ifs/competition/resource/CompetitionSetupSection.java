package com.worth.ifs.competition.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum defines all sections of competition setup.
 * It is used when recording which sections are marked as complete during the competition setup process.
 */
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
	
	private static Map<String, CompetitionSetupSection> PATH_MAP;
	
	static {
		PATH_MAP = new HashMap<>();
		for(CompetitionSetupSection section: values()){
			PATH_MAP.put(section.getPath(), section);
		}
	};
	
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

	public static CompetitionSetupSection fromPath(String path) {
		return PATH_MAP.get(path);
	}
	
}
