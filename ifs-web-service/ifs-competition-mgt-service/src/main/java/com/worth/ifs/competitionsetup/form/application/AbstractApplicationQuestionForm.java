package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by simon on 28/11/16.
 */
public class AbstractApplicationQuestionForm extends CompetitionSetupForm {

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
