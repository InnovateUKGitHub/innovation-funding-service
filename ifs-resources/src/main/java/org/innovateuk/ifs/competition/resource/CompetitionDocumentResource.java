package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.validation.constraints.NotBlank;

import java.util.List;

/*
 *  Resource for project document.
 */

public class CompetitionDocumentResource {
    public static final String COLLABORATION_AGREEMENT_TITLE = "Collaboration agreement";

    private Long id;

    private Long competition;

    @NotBlank(message = "{validation.standard.title.required}")
    private String title;

    @NotBlank(message = "{validation.documentform.guidance.required}")
    private String guidance;

    private boolean editable;
    private boolean enabled;

    private List<Long> fileTypes;

    public CompetitionDocumentResource() {
    }

    public CompetitionDocumentResource(Long competition, String title, String guidance, boolean editable, boolean enabled, List<Long> fileTypes) {
        this.competition = competition;
        this.title = title;
        this.guidance = guidance;
        this.editable = editable;
        this.enabled = enabled;
        this.fileTypes = fileTypes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
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

    public List<Long> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<Long> fileTypes) {
        this.fileTypes = fileTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionDocumentResource that = (CompetitionDocumentResource) o;

        return new EqualsBuilder()
                .append(editable, that.editable)
                .append(enabled, that.enabled)
                .append(id, that.id)
                .append(competition, that.competition)
                .append(title, that.title)
                .append(guidance, that.guidance)
                .append(fileTypes, that.fileTypes)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(competition)
                .append(title)
                .append(guidance)
                .append(editable)
                .append(enabled)
                .append(fileTypes)
                .toHashCode();
    }
}
