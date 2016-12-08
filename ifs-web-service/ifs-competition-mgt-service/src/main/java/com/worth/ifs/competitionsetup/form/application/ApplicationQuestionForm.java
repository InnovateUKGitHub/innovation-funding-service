package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competitionsetup.viewmodel.GuidanceRowViewModel;

import javax.validation.Valid;
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
