package org.innovateuk.ifs.competitionsetup.core.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

import java.util.Optional;

/**
 * The interface for saving and autosaving competition forms.
 */
public interface CompetitionSetupUpdater {

    ServiceResult<Void> autoSaveSectionField(CompetitionResource competitionResource, CompetitionSetupForm form, String fieldName, String value, Optional<Long> ObjectId);

    boolean supportsForm(Class<? extends CompetitionSetupForm> clazz);

    ServiceResult<Void> saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm);

    CompetitionSetupSection sectionToSave();
}
