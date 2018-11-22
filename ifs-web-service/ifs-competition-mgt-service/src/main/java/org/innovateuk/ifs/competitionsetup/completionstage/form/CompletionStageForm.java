package org.innovateuk.ifs.competitionsetup.completionstage.form;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

/**
 * TODO DW - comment
 */
public class CompletionStageForm extends CompetitionSetupForm {

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
