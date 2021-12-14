package org.innovateuk.ifs.management.competition.setup.application.sectionupdater;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupUpdater;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.http.HttpStatus;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

/**
 * Class to hold all the common functionality in the section savers.
 */
public abstract class AbstractSectionUpdater implements CompetitionSetupUpdater {

    private static final Log LOG = LogFactory.getLog(AbstractSectionUpdater.class);

    public ServiceResult<Void> saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm, UserResource loggedInUser) {
        if(!sectionToSave().preventEdit(competitionResource, loggedInUser)) {
            return doSaveSection(competitionResource, competitionSetupForm, loggedInUser);
        }
        else {
            return serviceFailure(singletonList(new Error("COMPETITION_NOT_EDITABLE", HttpStatus.BAD_REQUEST)));
        }
    }

    protected abstract ServiceResult<Void> doSaveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm, UserResource loggedInUser);

}
