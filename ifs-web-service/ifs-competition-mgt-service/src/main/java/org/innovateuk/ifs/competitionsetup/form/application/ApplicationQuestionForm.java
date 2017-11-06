package org.innovateuk.ifs.competitionsetup.form.application;

import org.innovateuk.ifs.competitionsetup.form.GuidanceRowForm;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * Form for the application question form competition setup section.
 */
public class ApplicationQuestionForm extends AbstractApplicationQuestionForm {

    @Valid
    private List<GuidanceRowForm> guidanceRows = new ArrayList<>();

    private boolean removable;

    public List<GuidanceRowForm> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<GuidanceRowForm> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }

    @Override
    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }
}
