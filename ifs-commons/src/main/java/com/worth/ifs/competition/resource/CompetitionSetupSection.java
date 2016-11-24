package com.worth.ifs.competition.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.competition.resource.CompetitionSetupSubsection.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * This enum defines all sections of competition setup.
 * It is used when recording which sections are marked as complete during the competition setup process.
 */
public enum CompetitionSetupSection {
	HOME("home", "Home Page", emptyList()),
	INITIAL_DETAILS("initial", "Initial Details", emptyList()),
	ADDITIONAL_INFO("additional", "Funding Information", emptyList()),
	ELIGIBILITY("eligibility", "Eligibility", emptyList()),
	MILESTONES("milestones", "Milestones", emptyList()),
	APPLICATION_FORM("application", "Application", asList(PROJECT_DETAILS, QUESTIONS, FINANCES, APPLICATION_DETAILS)),
	ASSESSORS("assessors", "Assessors", emptyList());
	
	private String path;
	private String name;
	private List<CompetitionSetupSubsection> subsections;
	
	private static Map<String, CompetitionSetupSection> PATH_MAP;
	
	static {
		PATH_MAP = new HashMap<>();
		for(CompetitionSetupSection section: values()){
			PATH_MAP.put(section.getPath(), section);
		}
	};
	
	CompetitionSetupSection(String sectionPath, String sectionName, List<CompetitionSetupSubsection> subsections) {
		this.path = sectionPath;
		this.name = sectionName;
		this.subsections = subsections;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}

	public List<CompetitionSetupSubsection> getSubsections() {
		return subsections;
	}

	public static CompetitionSetupSection fromPath(String path) {
		return PATH_MAP.get(path);
	}
	
}
