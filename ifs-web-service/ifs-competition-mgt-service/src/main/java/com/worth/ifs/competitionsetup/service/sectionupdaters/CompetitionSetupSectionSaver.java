package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.competition.resource.CompetitionSetupSection;

/**
 * Interface for saving competition setup sections.
 */
public interface CompetitionSetupSectionSaver extends CompetitionSetupSaver {

	CompetitionSetupSection sectionToSave();
	
}
