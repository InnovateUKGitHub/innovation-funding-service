package org.innovateuk.ifs.competitionsetup.form.common.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;

/**
 * Interface for saving competition setup subsections.
 */
public interface CompetitionSetupSubsectionSaver extends CompetitionSetupSaver {

	CompetitionSetupSubsection subsectionToSave();

}
