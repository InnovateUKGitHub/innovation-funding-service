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
	HOME("home", "Home Page", emptyList(), false),
	INITIAL_DETAILS("initial", "Initial details", emptyList(), true),
	ADDITIONAL_INFO("additional", "Funding information", emptyList(), true),
	ELIGIBILITY("eligibility", "Eligibility", emptyList(), false),
	MILESTONES("milestones", "Milestones", emptyList(), true),
	APPLICATION_FORM("application", "Application", asList(PROJECT_DETAILS, QUESTIONS, FINANCES, APPLICATION_DETAILS), false),
	ASSESSORS("assessors", "Assessors", emptyList(), true),
	CONTENT("content", "Public content", emptyList(), true);
	
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
	};
	
	CompetitionSetupSection(String sectionPath, String sectionName, List<CompetitionSetupSubsection> subsections, Boolean editableAfterSetupAndLive) {
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
			if (competitionResource.isAssessmentClosed()) {
				return true;
			}
		} else if (competitionResource.isSetupAndLive()) {
			return !this.getEditableAfterSetupAndLive();
		}

		return false;
	}

	public boolean isComplete(CompetitionResource competitionResource) {
		return competitionResource.getSectionSetupStatus().getOrDefault(this, false);
	}

	public boolean hasDisplayableSetupFragment() {
		return HOME != this;
	}

}
