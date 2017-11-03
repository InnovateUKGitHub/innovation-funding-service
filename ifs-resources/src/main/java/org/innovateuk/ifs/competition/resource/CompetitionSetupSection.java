package org.innovateuk.ifs.competition.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * This enum defines all sections of competition setup.
 * It is used when recording which sections are marked as complete during the competition setup process.
 */
public enum CompetitionSetupSection {
	HOME(1L, "home", "Home Page", emptyList(), false),
	INITIAL_DETAILS(2L, "initial", "Initial details", emptyList(), true),
	ADDITIONAL_INFO(3L, "additional", "Funding information", emptyList(), true),
	ELIGIBILITY(4L, "eligibility", "Eligibility", emptyList(), false),
	MILESTONES(5L, "milestones", "Milestones", emptyList(), true),
	APPLICATION_FORM(6L, "application", "Application", asList(PROJECT_DETAILS, QUESTIONS, FINANCES, APPLICATION_DETAILS), false),
	ASSESSORS(7L, "assessors", "Assessors", emptyList(), true),
	CONTENT(8L, "content", "Public content", emptyList(), true);
	
	private Long id;
	private String path;
	private String name;
	private List<CompetitionSetupSubsection> subsections;

	private Boolean editableAfterSetupAndLive;
	
	private static Map<String, CompetitionSetupSection> PATH_MAP;
	
	static {
		PATH_MAP = new HashMap<>();
		for(CompetitionSetupSection section: values()){
			PATH_MAP.put(section.getPath(), section);
		}
	}
	
	CompetitionSetupSection(Long id, String sectionPath, String sectionName, List<CompetitionSetupSubsection> subsections, Boolean editableAfterSetupAndLive) {
		this.id = id;
		this.path = sectionPath;
		this.name = sectionName;
		this.subsections = subsections;
		this.editableAfterSetupAndLive = editableAfterSetupAndLive;
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

	public Boolean getEditableAfterSetupAndLive() {
		return editableAfterSetupAndLive;
	}

	public boolean preventEdit(CompetitionResource competitionResource) {
		if (competitionResource.isSetupAndAfterNotifications()) {
            return true;
        }
        if (this == ASSESSORS) {
			return competitionResource.isAssessmentClosed();
		} else if (competitionResource.isSetupAndLive()) {
			return !this.getEditableAfterSetupAndLive();
		}

		return false;
	}

	public boolean hasDisplayableSetupFragment() {
		return HOME != this;
	}

	public Long getId() {
		return id;
	}
}
