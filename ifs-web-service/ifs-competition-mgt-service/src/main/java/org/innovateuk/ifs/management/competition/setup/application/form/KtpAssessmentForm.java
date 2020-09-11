package org.innovateuk.ifs.management.competition.setup.application.form;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class KtpAssessmentForm extends AbstractQuestionForm {

    private String writtenFeedback;

    private String guidanceTitle;

    @Valid
    private List<GuidanceRowForm> guidanceRows = new ArrayList<>();

    private Integer maxWordCount;

    public String getWrittenFeedback() {
        return writtenFeedback;
    }

    public void setWrittenFeedback(String writtenFeedback) {
        this.writtenFeedback = writtenFeedback;
    }

    public String getGuidanceTitle() {
        return guidanceTitle;
    }

    public void setGuidanceTitle(String guidanceTitle) {
        this.guidanceTitle = guidanceTitle;
    }

    public List<GuidanceRowForm> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<GuidanceRowForm> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }

    public Integer getMaxWordCount() {
        return maxWordCount;
    }

    public void setMaxWordCount(Integer maxWordCount) {
        this.maxWordCount = maxWordCount;
    }
}
