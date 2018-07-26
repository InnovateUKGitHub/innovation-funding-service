package org.innovateuk.ifs.competitionsetup.domain;

import org.innovateuk.ifs.competition.domain.Competition;

import javax.persistence.*;

@Entity
public class ProjectDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitionId", referencedColumnName = "id")
    private Competition competition;

    private String title;

    private String guidance;

    private boolean editable;
    private boolean enabled;
    private boolean pdf;
    private boolean spreadsheet;

    public ProjectDocument(Competition competition, String title, String guidance, boolean editable, boolean enabled, boolean pdf, boolean spreadsheet) {
        this.competition = competition;
        this.title = title;
        this.guidance = guidance;
        this.editable = editable;
        this.enabled = enabled;
        this.pdf = pdf;
        this.spreadsheet = spreadsheet;
    }

    public ProjectDocument() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
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
