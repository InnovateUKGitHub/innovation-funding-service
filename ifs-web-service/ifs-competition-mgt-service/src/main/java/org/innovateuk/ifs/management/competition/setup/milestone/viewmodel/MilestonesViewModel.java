package org.innovateuk.ifs.management.competition.setup.milestone.viewmodel;

import org.innovateuk.ifs.management.competition.setup.completionstage.util.CompletionStageUtils;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

public class MilestonesViewModel extends CompetitionSetupViewModel {

    private CompletionStageUtils completionStageUtils;

    public MilestonesViewModel(GeneralSetupViewModel generalSetupViewModel, CompletionStageUtils completionStageUtils) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.completionStageUtils = completionStageUtils;
    }

    public boolean isApplicationSubmissionEnabled() {
        return completionStageUtils.isApplicationSubmissionEnabled(generalSetupViewModel.getCompetition().getCompletionStage());
    }
}
