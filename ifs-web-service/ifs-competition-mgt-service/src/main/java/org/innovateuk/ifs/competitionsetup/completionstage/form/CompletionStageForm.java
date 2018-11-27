package org.innovateuk.ifs.competitionsetup.completionstage.form;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

/**
 * Form to capture the selection of a Completion Stage in Competition Setup.
 */
public class CompletionStageForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.completionstageform.completion.stage.required}")
    private CompetitionCompletionStage selectedCompletionStage;

    public CompletionStageForm() {
    }

    public CompletionStageForm(CompetitionCompletionStage selectedCompletionStage) {
        this.selectedCompletionStage = selectedCompletionStage;
    }

    public CompetitionCompletionStage getSelectedCompletionStage() {
        return selectedCompletionStage;
    }

    public void setSelectedCompletionStage(CompetitionCompletionStage selectedCompletionStage) {
        this.selectedCompletionStage = selectedCompletionStage;
    }
}
