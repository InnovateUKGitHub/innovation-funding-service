package org.innovateuk.ifs.management.competition.setup.completionstage.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.management.competition.setup.completionstage.util.CompletionStageUtils;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Service to update the Completion Stage section of Competition Setup.
 */
@Service
public class CompletionStageSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    private MilestoneRestService milestoneRestService;

    private CompletionStageUtils completionStageUtils;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.COMPLETION_STAGE;
    }

    @Autowired
    public CompletionStageSectionUpdater(MilestoneRestService milestoneRestService, CompletionStageUtils completionStageUtils) {
        this.milestoneRestService = milestoneRestService;
        this.completionStageUtils = completionStageUtils;
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

    @Override
    public String getNextSection(CompetitionSetupForm competitionSetupForm, CompetitionResource competition, CompetitionSetupSection section) {
        String sectionPath;
        CompletionStageForm completionStageForm = (CompletionStageForm) competitionSetupForm;

        if (completionStageUtils.isApplicationSubmissionEnabled(completionStageForm.getSelectedCompletionStage())) {
            sectionPath = CompetitionSetupSection.APPLICATION_SUBMISSION.getPath();
        } else {
            sectionPath = CompetitionSetupSection.MILESTONES.getPath();
        }

        return format("redirect:/competition/setup/%d/section/%s", competition.getId(), sectionPath);
    }
}
