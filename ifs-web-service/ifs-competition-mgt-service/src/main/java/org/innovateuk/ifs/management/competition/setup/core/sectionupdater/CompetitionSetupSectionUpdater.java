package org.innovateuk.ifs.management.competition.setup.core.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import static java.lang.String.format;

/**
 * Interface for saving competition setup sections.
 */
public interface CompetitionSetupSectionUpdater extends CompetitionSetupUpdater {

    default String getNextSection(CompetitionSetupForm competitionSetupForm, CompetitionResource competition, CompetitionSetupSection section) {
        return format("redirect:/competition/setup/%d/section/%s", competition.getId(), section.getPostMarkCompletePath());
    }
}
