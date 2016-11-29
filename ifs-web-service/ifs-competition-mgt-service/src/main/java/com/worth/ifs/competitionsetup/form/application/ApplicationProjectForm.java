package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by simon on 26/11/16.
 */
public class ApplicationProjectForm extends AbstractApplicationQuestionForm {

    public int getGuidanceRowsCount() {
        return getQuestion().getGuidanceRows().size();
    }
}
