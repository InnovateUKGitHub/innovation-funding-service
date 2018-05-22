package org.innovateuk.ifs.competitionsetup.core.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;

/**
 * Interface for saving competition setup subsections.
 */
public interface CompetitionSetupSubsectionUpdater extends CompetitionSetupUpdater {

	CompetitionSetupSubsection subsectionToSave();

}
