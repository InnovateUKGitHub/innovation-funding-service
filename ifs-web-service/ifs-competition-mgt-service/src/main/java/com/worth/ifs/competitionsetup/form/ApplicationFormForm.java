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
    private Question question;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
