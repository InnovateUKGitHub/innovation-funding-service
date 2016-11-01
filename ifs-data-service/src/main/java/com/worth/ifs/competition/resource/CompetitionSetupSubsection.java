package com.worth.ifs.competition.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum defines all sections of competition setup.
 * It is used when recording which sections are marked as complete during the competition setup process.
 */
public enum CompetitionSetupSubsection {
	PROJECT_DETAILS("project", "Project Details"),
	QUESTIONS("question", "Questions"),
	FINANCES("finance", "Finances");

	private String path;
	private String name;

	private static Map<String, CompetitionSetupSubsection> PATH_MAP;

	static {
		PATH_MAP = new HashMap<>();
		for(CompetitionSetupSubsection section: values()){
			PATH_MAP.put(section.getPath(), section);
		}
	};

	private CompetitionSetupSubsection(String path, String name) {
		this.path = path;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}

	public static CompetitionSetupSubsection fromPath(String path) {
		return PATH_MAP.get(path);
	}
	
}
