package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * Form for the application question form competition setup section.
 */
public class ApplicationQuestionForm extends CompetitionSetupForm {
    @Valid
    @NotNull
    private CompetitionSetupQuestionResource question;

    public CompetitionSetupQuestionResource getQuestion() {
        return question;
    }

    public void setQuestion(CompetitionSetupQuestionResource question) {
        this.question = question;
    }
}
