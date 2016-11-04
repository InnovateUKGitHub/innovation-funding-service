package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.viewmodel.application.QuestionViewModel;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * Form for the application question form competition setup section.
 */
public class ApplicationQuestionForm extends CompetitionSetupForm {
    @Valid
    @NotNull
    private QuestionViewModel question;

    public QuestionViewModel getQuestion() {
        return question;
    }

    public void setQuestion(QuestionViewModel question) {
        this.question = question;
    }
}
