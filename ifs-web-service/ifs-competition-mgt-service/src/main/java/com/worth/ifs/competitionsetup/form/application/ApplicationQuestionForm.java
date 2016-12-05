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
public class ApplicationQuestionForm extends AbstractApplicationQuestionForm {

    @Valid
    private List<GuidanceRowViewModel> guidanceRows = new ArrayList<>();

    public List<GuidanceRowViewModel> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<GuidanceRowViewModel> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }
}
