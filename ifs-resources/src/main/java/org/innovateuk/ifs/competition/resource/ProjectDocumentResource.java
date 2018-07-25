package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProjectDocumentResource {

    private Long id;

    private Long competition;

    private String title;

    private String guidance;

    private boolean enabled;
    private boolean pdf;
    private boolean spreadsheet;

    public ProjectDocumentResource(Long competition, String title, String guidance, boolean enabled, boolean pdf, boolean spreadsheet) {
        this.competition = competition;
        this.title = title;
        this.guidance = guidance;
        this.enabled = enabled;
        this.pdf = pdf;
        this.spreadsheet = spreadsheet;
    }

    public ProjectDocumentResource() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectDocumentResource that = (ProjectDocumentResource) o;

        return new EqualsBuilder()
                .append(enabled, that.enabled)
                .append(pdf, that.pdf)
                .append(spreadsheet, that.spreadsheet)
                .append(id, that.id)
                .append(competition, that.competition)
                .append(title, that.title)
                .append(guidance, that.guidance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(competition)
                .append(title)
                .append(guidance)
                .append(enabled)
                .append(pdf)
                .append(spreadsheet)
                .toHashCode();
    }
}
