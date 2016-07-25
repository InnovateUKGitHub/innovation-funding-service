package com.worth.ifs.competition.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum defines all sections of competition setup.
 * It is used when recording which sections are marked as complete during the competition setup process.
 */
public enum CompetitionSetupSection {
	HOME("home", "Home Page"),
	INITIAL_DETAILS("initial", "Initial Details"),
	ADDITIONAL_INFO("additional", "Funding Information"),
	ELIGIBILITY("eligibility", "Eligibility"),
	MILESTONES("milestones", "Milestones"),
	APPLICATION_FORM("application", "Application Questions"),
	FINANCE("finance", "Application Finances"),
	ASSESSORS("assessors", "Assessors"),
	DESCRIPTION_AND_BRIEF("description", "Description and brief");


	
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
