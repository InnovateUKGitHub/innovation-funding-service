package org.innovateuk.ifs.competitionsetup.completionstage.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * A view model to back the Completion stage selection page.  Contains a list of milestones to show on that page
 * plus helper methods to help with selecting the correct radiofield option.
 */
public class CompletionStageViewModel extends CompetitionSetupViewModel {

    private final List<MilestoneType> nonSelectableMilestones;

    public CompletionStageViewModel(GeneralSetupViewModel generalSetupViewModel) {

        this.generalSetupViewModel = generalSetupViewModel;

        this.nonSelectableMilestones = simpleFilter(MilestoneType.presetValues(),
                type -> MilestoneType.RELEASE_FEEDBACK != type &&
                        MilestoneType.REGISTRATION_DATE != type);
    }

    public List<MilestoneType> getNonSelectableMilestones() {
        return nonSelectableMilestones;
    }

    public CompetitionCompletionStage getReleaseFeedbackCompletionStage() {
        return CompetitionCompletionStage.RELEASE_FEEDBACK;
    }

    public CompetitionCompletionStage getProjectSetupCompletionStage() {
        return CompetitionCompletionStage.PROJECT_SETUP;
    }
}
