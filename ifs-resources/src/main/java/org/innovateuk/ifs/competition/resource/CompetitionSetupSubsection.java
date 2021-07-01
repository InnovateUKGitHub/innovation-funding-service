package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.form.resource.SectionType;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum defines all sections of competition setup.
 * It is used when recording which sections are marked as complete during the competition setup process.
 */
public enum CompetitionSetupSubsection {
	PROJECT_DETAILS(1L, "project", SectionType.PROJECT_DETAILS),
	QUESTIONS(2L, "question", SectionType.APPLICATION_QUESTIONS),
	FINANCES(3L, "finance", SectionType.FINANCES),
	APPLICATION_DETAILS(4L, "detail", SectionType.APPLICATION_QUESTIONS),
	KTP_ASSESSMENT(5L, "ktp_assessment", SectionType.KTP_ASSESSMENT);

	private Long id;
	private String path;
	private SectionType sectionType;

	private static Map<String, CompetitionSetupSubsection> PATH_MAP;

	static {
		PATH_MAP = new HashMap<>();
		for(CompetitionSetupSubsection section: values()){
			PATH_MAP.put(section.getPath(), section);
		}
	}

	CompetitionSetupSubsection(Long id, String path, SectionType sectionType) {
		this.id = id;
		this.path = path;
		this.sectionType = sectionType;
	}

    public Long getId() {
        return id;
    }

    public SectionType getName() {
		return sectionType;
	}
	
	public String getPath() {
		return path;
	}

	public static CompetitionSetupSubsection fromPath(String path) {
		return PATH_MAP.get(path);
	}
	
}
