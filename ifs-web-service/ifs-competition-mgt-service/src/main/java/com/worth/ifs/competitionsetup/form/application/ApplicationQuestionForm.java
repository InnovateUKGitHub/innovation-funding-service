package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.viewmodel.GuidanceRowViewModel;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


/**
 * Form for the application question form competition setup section.
 */
public class ApplicationQuestionForm extends CompetitionSetupForm {
    @Valid
    @NotNull
    private CompetitionSetupQuestionResource question;

    @Valid
    @NotEmpty(message = "Please enter a guidance row")
    private List<GuidanceRowViewModel> guidanceRows = new ArrayList<>();

    public CompetitionSetupQuestionResource getQuestion() {
        return question;
    }

    public void setQuestion(CompetitionSetupQuestionResource question) {
        this.question = question;
    }

    public List<GuidanceRowViewModel> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<GuidanceRowViewModel> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }

    public int getGuidanceRowsCount() {
        return guidanceRows.size();
    }

}
