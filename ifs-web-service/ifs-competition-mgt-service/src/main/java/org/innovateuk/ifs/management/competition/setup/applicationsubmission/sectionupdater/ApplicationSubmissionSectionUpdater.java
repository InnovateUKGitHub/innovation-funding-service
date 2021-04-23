package org.innovateuk.ifs.management.competition.setup.applicationsubmission.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.form.ApplicationSubmissionForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Service to update the Application Submission section of Competition Setup.
 */
@Service
public class ApplicationSubmissionSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater  {

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.APPLICATION_SUBMISSION;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        ApplicationSubmissionForm form = (ApplicationSubmissionForm) competitionSetupForm;
        competition.setAlwaysOpen(form.getAlwaysOpen());
        return competitionSetupRestService.update(competition).toServiceResult();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return ApplicationSubmissionForm.class.equals(clazz);
    }

    @Override
    public String getNextSection(CompetitionSetupForm competitionSetupForm, CompetitionResource competition, CompetitionSetupSection section) {
        String sectionPath = CompetitionSetupSection.MILESTONES.getPath();
        return format("redirect:/competition/setup/%d/section/%s", competition.getId(), sectionPath);
    }
}
