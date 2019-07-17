package org.innovateuk.ifs.management.competition.setup.core.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;

/**
 * Interface for saving competition setup subsections.
 */
public interface CompetitionSetupSubsectionUpdater extends CompetitionSetupUpdater {

	CompetitionSetupSubsection subsectionToSave();

}
