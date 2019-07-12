package org.innovateuk.ifs.management.competition.setup.completionstage.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to update the Completion Stage section of Competition Setup.
 */
@Service
public class CompletionStageSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    private MilestoneRestService milestoneRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.COMPLETION_STAGE;
    }

    @Autowired
    public CompletionStageSectionUpdater(MilestoneRestService milestoneRestService) {
        this.milestoneRestService = milestoneRestService;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        return milestoneRestService.updateCompletionStage(competition.getId(),
                ((CompletionStageForm)competitionSetupForm).getSelectedCompletionStage()).toServiceResult();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return CompletionStageForm.class.equals(clazz);
    }
}
