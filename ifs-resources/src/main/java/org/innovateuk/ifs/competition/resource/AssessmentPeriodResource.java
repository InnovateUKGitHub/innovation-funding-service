package org.innovateuk.ifs.competition.resource;

public class AssessmentPeriodResource {

    private Long id;
    private Long competitionId;

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
}
