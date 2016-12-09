package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.competition.resource.CompetitionSetupSubsection;

/**
 * Interface for saving competition setup subsections.
 */
public interface CompetitionSetupSubsectionSaver extends CompetitionSetupSaver {

	CompetitionSetupSubsection sectionToSave();

}
