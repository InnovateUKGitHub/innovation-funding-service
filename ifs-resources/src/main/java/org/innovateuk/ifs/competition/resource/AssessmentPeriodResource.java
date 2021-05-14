package org.innovateuk.ifs.competition.resource;

public class AssessmentPeriodResource {

    private Long id;
    private Long competitionId;
    private boolean open;
    private boolean inAssessment;
    private boolean assessmentClosed;

    public AssessmentPeriodResource() {
    }

    public AssessmentPeriodResource(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isInAssessment() {
        return inAssessment;
    }

    public void setInAssessment(boolean inAssessment) {
        this.inAssessment = inAssessment;
    }

    public boolean isAssessmentClosed() {
        return assessmentClosed;
    }

    public void setAssessmentClosed(boolean assessmentClosed) {
        this.assessmentClosed = assessmentClosed;
    }
}
