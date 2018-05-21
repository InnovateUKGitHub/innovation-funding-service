package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.competitionsetup.application.form.ProjectForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
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

    @Override
    protected ServiceResult<Void> autoSaveGuidanceRowSubject(GuidanceRowResource guidanceRow, String fieldName, String value) {
        if (fieldName.endsWith("subject")) {
            guidanceRow.setSubject(value);
            return serviceSuccess();
        } else {
            return serviceFailure(new Error("Field not found", HttpStatus.BAD_REQUEST));
        }
    }
}
