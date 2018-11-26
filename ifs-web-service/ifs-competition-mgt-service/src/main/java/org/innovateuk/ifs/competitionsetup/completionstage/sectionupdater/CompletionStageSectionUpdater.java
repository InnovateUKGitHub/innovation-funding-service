package org.innovateuk.ifs.competitionsetup.completionstage.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.competitionsetup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO DW - comment
 */
@Service
public class CompletionStageSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.COMPLETION_STAGE;
    }

    @Autowired
    public CompletionStageSectionUpdater(CompetitionSetupMilestoneService competitionSetupMilestoneService) {
        this.competitionSetupMilestoneService = competitionSetupMilestoneService;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        return competitionSetupMilestoneService.updateCompletionStage(competition.getId(),
                ((CompletionStageForm)competitionSetupForm).getSelectedCompletionStage());
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return CompletionStageForm.class.equals(clazz);
    }
}
