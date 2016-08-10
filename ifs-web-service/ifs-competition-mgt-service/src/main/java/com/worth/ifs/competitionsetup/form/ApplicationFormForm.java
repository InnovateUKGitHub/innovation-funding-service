package com.worth.ifs.competitionsetup.form;

import com.worth.ifs.competitionsetup.model.Question;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * Form for the application form competition setup section.
 */
public class ApplicationFormForm extends CompetitionSetupForm {
    @Valid
    @NotNull
    private Question questionToUpdate;

    public Question getQuestionToUpdate() {
        return questionToUpdate;
    }

    public void setQuestionToUpdate(Question questionToUpdate) {
        this.questionToUpdate = questionToUpdate;
    }
}
