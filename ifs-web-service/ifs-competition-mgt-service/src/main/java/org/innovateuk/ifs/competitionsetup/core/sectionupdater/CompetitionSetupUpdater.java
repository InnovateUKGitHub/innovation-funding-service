package org.innovateuk.ifs.competitionsetup.core.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

/**
 * The interface for saving and autosaving competition forms.
 */
public interface CompetitionSetupUpdater {

    boolean supportsForm(Class<? extends CompetitionSetupForm> clazz);

    ServiceResult<Void> saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm);

    CompetitionSetupSection sectionToSave();
}
