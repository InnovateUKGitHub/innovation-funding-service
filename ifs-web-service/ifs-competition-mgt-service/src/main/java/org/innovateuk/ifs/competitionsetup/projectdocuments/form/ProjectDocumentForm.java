package org.innovateuk.ifs.competitionsetup.projectdocuments.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form to capture the details of the new project document
 */
public class ProjectDocumentForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.field.must.not.be.blank}")
    private String title;

    @NotBlank(message = "{validation.field.must.not.be.blank}")
    private String guidance;

    private boolean enabled;
    private boolean pdf;
    private boolean spreadsheet;

    // for spring form binding
    public ProjectDocumentForm() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPdf() {
        return pdf;
    }

    public void setPdf(boolean pdf) {
        this.pdf = pdf;
    }

    public boolean isSpreadsheet() {
        return spreadsheet;
    }

    public void setSpreadsheet(boolean spreadsheet) {
        this.spreadsheet = spreadsheet;
    }
}

