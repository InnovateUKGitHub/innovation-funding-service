package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.model.application.Question;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * Form for the application question form competition setup section.
 */
public class ApplicationQuestionForm extends CompetitionSetupForm {
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
