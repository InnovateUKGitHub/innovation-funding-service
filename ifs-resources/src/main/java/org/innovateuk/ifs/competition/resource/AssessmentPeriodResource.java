package org.innovateuk.ifs.competition.resource;

public class AssessmentPeriodResource {

    private Long id;
    private String name;
    private Long competitionId;

    public AssessmentPeriodResource() {
    }

    public AssessmentPeriodResource(Long competitionId, String name) {
        this.competitionId = competitionId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
