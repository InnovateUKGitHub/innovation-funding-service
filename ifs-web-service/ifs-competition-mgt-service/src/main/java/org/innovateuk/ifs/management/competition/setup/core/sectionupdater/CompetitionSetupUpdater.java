package org.innovateuk.ifs.management.competition.setup.core.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * The interface for saving competition forms.
 */
public interface CompetitionSetupUpdater {

    boolean supportsForm(Class<? extends CompetitionSetupForm> clazz);

    ServiceResult<Void> saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm, UserResource loggedInUser);

    CompetitionSetupSection sectionToSave();
}
