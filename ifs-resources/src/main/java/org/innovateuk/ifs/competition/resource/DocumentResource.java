package org.innovateuk.ifs.competition.resource;

public class DocumentResource {

    private Long id;
    private String title;
    private Long competitionId;

    private Boolean pdfAccepted;
    private Boolean spreadsheetAccepted;
    private String guidance;
    private Boolean included;

    public Boolean getIncluded() {
        return included;
    }

    public void setIncluded(Boolean included) {
        this.included = included;
    }

    public Boolean getPdfAccepted() {
        return pdfAccepted;
    }

    public void setPdfAccepted(Boolean pdfAccepted) {
        this.pdfAccepted = pdfAccepted;
    }

    public Boolean getSpreadsheetAccepted() {
        return spreadsheetAccepted;
    }

    public void setSpreadsheetAccepted(Boolean spreadsheetAccepted) {
        this.spreadsheetAccepted = spreadsheetAccepted;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public String getGuidance() {
        return guidance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
