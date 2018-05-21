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
    private FinanceForm financeForm;

    @Valid
    private DetailsForm detailsForm;

    public List<CompetitionSetupForm> getQuestions() {
        return questions;
    }

    public void setQuestions(List<CompetitionSetupForm> questions) {
        this.questions = questions;
    }

    public FinanceForm getFinanceForm() {
        return financeForm;
    }

    public void setFinanceForm(FinanceForm financeForm) {
        this.financeForm = financeForm;
    }

    public DetailsForm getDetailsForm() {
        return detailsForm;
    }

    public void setDetailsForm(DetailsForm detailsForm) {
        this.detailsForm = detailsForm;
    }
}
