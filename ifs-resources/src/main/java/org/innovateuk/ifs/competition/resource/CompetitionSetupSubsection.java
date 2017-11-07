package org.innovateuk.ifs.competition.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum defines all sections of competition setup.
 * It is used when recording which sections are marked as complete during the competition setup process.
 */
public enum CompetitionSetupSubsection {
	PROJECT_DETAILS(1L, "project", "Project Details"),
	QUESTIONS(2L, "question", "Questions"),
	FINANCES(3L, "finance", "Finances"),
	APPLICATION_DETAILS(4L, "detail", "Application Details");

	private Long id;
	private String path;
	private String name;

	private static Map<String, CompetitionSetupSubsection> PATH_MAP;

	static {
		PATH_MAP = new HashMap<>();
		for(CompetitionSetupSubsection section: values()){
			PATH_MAP.put(section.getPath(), section);
		}
	};

	CompetitionSetupSubsection(Long id, String path, String name) {
		this.id = id;
		this.path = path;
		this.name = name;
	}

    public Long getId() {
        return id;
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
