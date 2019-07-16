package org.innovateuk.ifs.management.competition.setup.application.sectionupdater;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.management.competition.setup.application.form.ProjectForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.PROJECT_DETAILS;

/**
 * Competition setup section saver for the application form section.
 */
@Service
public class ProjectDetailsSectionUpdater extends AbstractApplicationSectionUpdater implements CompetitionSetupSubsectionUpdater {

    @Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return ProjectForm.class.equals(clazz);
	}

    @Override
    public CompetitionSetupSubsection subsectionToSave() {
        return PROJECT_DETAILS;
    }

    @Override
    protected void mapGuidanceRows(AbstractQuestionForm form) {
        //nothing to do here. The guidance rows are set on the form already.
    }
}
