package com.worth.ifs.competitionsetup.form;

import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;

import javax.validation.Valid;
import java.util.List;

/**
 * Form for the application form competition setup section.
 */
public class LandingPageForm extends CompetitionSetupForm {

    @Valid
    private List<ApplicationQuestionForm> question;

    @Valid
    private ApplicationFinanceForm financeForm;

    @Valid
    private ApplicationDetailsForm detailsForm;

    public List<ApplicationQuestionForm> getQuestion() {
        return question;
    }

    public void setQuestion(List<ApplicationQuestionForm> question) {
        this.question = question;
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
