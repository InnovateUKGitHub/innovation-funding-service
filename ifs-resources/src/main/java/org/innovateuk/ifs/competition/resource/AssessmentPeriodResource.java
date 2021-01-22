package org.innovateuk.ifs.competition.resource;

public class AssessmentPeriodResource {

    private Long id;
    private Integer index;
    private Long competitionId;

    public AssessmentPeriodResource() {
    }

    public AssessmentPeriodResource(Long competitionId, Integer index) {
        this.competitionId = competitionId;
        this.index = index;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
