package org.innovateuk.ifs.competitionsetup.projectdocument.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form to capture the details of the new project document
 */
public class ProjectDocumentForm extends BaseBindingResultTarget {

    private Long projectDocumentId;

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

    public ProjectDocumentForm(Long projectDocumentId, String title, String guidance, boolean enabled, boolean pdf, boolean spreadsheet) {
        this.projectDocumentId = projectDocumentId;
        this.title = title;
        this.guidance = guidance;
        this.enabled = enabled;
        this.pdf = pdf;
        this.spreadsheet = spreadsheet;
    }

    public Long getProjectDocumentId() {
        return projectDocumentId;
    }

    public void setProjectDocumentId(Long projectDocumentId) {
        this.projectDocumentId = projectDocumentId;
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

