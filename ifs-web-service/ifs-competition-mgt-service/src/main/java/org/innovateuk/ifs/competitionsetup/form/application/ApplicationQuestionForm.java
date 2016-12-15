package org.innovateuk.ifs.competitionsetup.form.application;

import org.innovateuk.ifs.competitionsetup.form.AssessorsForm;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * Form for the application question form competition setup section.
 */
public class ApplicationQuestionForm extends AbstractApplicationQuestionForm {

    @Valid
    private List<AssessorsForm.GuidanceRowForm> guidanceRows = new ArrayList<>();

    public List<AssessorsForm.GuidanceRowForm> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<AssessorsForm.GuidanceRowForm> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }
}
