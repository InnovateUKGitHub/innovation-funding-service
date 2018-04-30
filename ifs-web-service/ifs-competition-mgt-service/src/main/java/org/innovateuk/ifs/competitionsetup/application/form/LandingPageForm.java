package org.innovateuk.ifs.competitionsetup.application.form;

import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

import javax.validation.Valid;
import java.util.List;

/**
 * Form for the application form competition setup section.
 */
public class LandingPageForm extends CompetitionSetupForm {

    @Valid
    private List<CompetitionSetupForm> questions;

    @Valid
    private ApplicationFinanceForm financeForm;

    @Valid
    private ApplicationDetailsForm detailsForm;

    public List<CompetitionSetupForm> getQuestions() {
        return questions;
    }

    public void setQuestions(List<CompetitionSetupForm> questions) {
        this.questions = questions;
    }

    public ApplicationFinanceForm getFinanceForm() {
        return financeForm;
    }

    public void setFinanceForm(ApplicationFinanceForm financeForm) {
        this.financeForm = financeForm;
    }

    public ApplicationDetailsForm getDetailsForm() {
        return detailsForm;
    }

    public void setDetailsForm(ApplicationDetailsForm detailsForm) {
        this.detailsForm = detailsForm;
    }
}
