package org.innovateuk.ifs.management.competition.setup.completionstage.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * A view model to back the Completion stage selection page.  Contains a list of milestones to show on that page
 * plus helper methods to help with selecting the correct radiofield option.
 */
public class CompletionStageViewModel extends CompetitionSetupViewModel {

    public CompletionStageViewModel(GeneralSetupViewModel generalSetupViewModel) {

        this.generalSetupViewModel = generalSetupViewModel;

    }

    public List<CompetitionCompletionStage> getCompetitionCompletionStages() {
        return asList(CompetitionCompletionStage.values());
    }

    public CompetitionCompletionStage getReleaseFeedbackCompletionStage() {
        return CompetitionCompletionStage.RELEASE_FEEDBACK;
    }

    public CompetitionCompletionStage getProjectSetupCompletionStage() {
        return CompetitionCompletionStage.PROJECT_SETUP;
    }

    public CompetitionCompletionStage getCompetitionCloseCompletionStage() {
        return CompetitionCompletionStage.COMPETITION_CLOSE;
    }
}
