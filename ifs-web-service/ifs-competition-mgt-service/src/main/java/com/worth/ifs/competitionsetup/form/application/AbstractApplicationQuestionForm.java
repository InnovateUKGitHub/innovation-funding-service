package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Abstract class for common functionality in application question and application project forms.
 */
abstract class AbstractApplicationQuestionForm extends CompetitionSetupForm {

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
