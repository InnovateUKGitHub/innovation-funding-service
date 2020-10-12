package org.innovateuk.ifs.management.competition.setup.application.form;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class KtpAssessmentForm extends AbstractQuestionForm {

    @Valid
    private List<GuidanceRowForm> guidanceRows = new ArrayList<>();

    public List<GuidanceRowForm> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<GuidanceRowForm> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }

}
