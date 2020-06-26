package org.innovateuk.ifs.management.competition.setup.application.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * Form for the application question form competition setup section.
 */
public class QuestionForm extends AbstractQuestionForm {

    @Valid
    private List<GuidanceRowForm> guidanceRows = new ArrayList<>();

    private MultipartFile templateDocumentFile;
    private boolean removable;
    private Integer appendixCount;

    public List<GuidanceRowForm> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<GuidanceRowForm> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }

    public MultipartFile getTemplateDocumentFile() {
        return templateDocumentFile;
    }

    public void setTemplateDocumentFile(MultipartFile templateDocumentFile) {
        this.templateDocumentFile = templateDocumentFile;
    }

    @Override
    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public Integer getAppendixCount() {
        return appendixCount;
    }

    public void setAppendixCount(Integer appendixCount) {
        this.appendixCount = appendixCount;
    }
}
