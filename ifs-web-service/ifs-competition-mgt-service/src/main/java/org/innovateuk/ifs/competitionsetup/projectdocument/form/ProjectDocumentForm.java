package org.innovateuk.ifs.competitionsetup.projectdocument.form;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form to capture the details of the new project document
 */
public class ProjectDocumentForm extends BaseBindingResultTarget {

    private Long projectDocumentId;

    @NotBlank(message = "{validation.standard.title.required}")
    private String title;

    @Length(max = 5000, message = "{validation.field.too.many.characters}")
    @NotBlank(message = "{validation.documentform.guidance.required}")
    private String guidance;

    private boolean editable;
    private boolean enabled;
    private boolean pdf;
    private boolean spreadsheet;
    // Added for binding errors, unsaved
    private String acceptedFileTypesId;

    // for spring form binding
    public ProjectDocumentForm() {
    }

    public ProjectDocumentForm(boolean editable, boolean enabled) {
        this.editable = editable;
        this.enabled = enabled;
    }

    public ProjectDocumentForm(Long projectDocumentId, String title, String guidance, boolean editable, boolean enabled) {
        this.projectDocumentId = projectDocumentId;
        this.title = title;
        this.guidance = guidance;
        this.editable = editable;
        this.enabled = enabled;
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
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
    
    public String getAcceptedFileTypesId() { return acceptedFileTypesId; }

    public void setAcceptedFileTypesId(String acceptedFileTypesId) {
        this.acceptedFileTypesId = acceptedFileTypesId;
    }
}

