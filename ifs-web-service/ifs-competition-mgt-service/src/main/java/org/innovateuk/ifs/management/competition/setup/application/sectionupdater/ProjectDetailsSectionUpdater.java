package org.innovateuk.ifs.management.competition.setup.application.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.application.form.ProjectForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.PROJECT_DETAILS;

/**
 * Competition setup section saver for the application form section.
 */
@Service
public class ProjectDetailsSectionUpdater extends AbstractApplicationSectionUpdater<ProjectForm> implements CompetitionSetupSubsectionUpdater {

    @Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return ProjectForm.class.equals(clazz);
	}

    @Override
    public CompetitionSetupSubsection subsectionToSave() {
        return PROJECT_DETAILS;
    }

    @Override
    protected void mapAppendix(ProjectForm form) {
        //nothing to do here. Project details don't have appendices
    }

    @Override
    protected void mapGuidanceRows(ProjectForm form) {
        //nothing to do here. Project details don't have assessor guidance rows
    }
}
