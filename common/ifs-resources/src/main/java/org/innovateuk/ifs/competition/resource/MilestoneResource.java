package org.innovateuk.ifs.competition.resource;

import java.time.ZonedDateTime;

public class MilestoneResource {
    private Long id;
    private MilestoneType type;
    private ZonedDateTime date;
    private Long competitionId;
    private Long assessmentPeriodId;

    public MilestoneResource() {
    }

    public MilestoneResource(MilestoneType type, Long competitionId) {
        this.type = type;
        this.competitionId = competitionId;
    }

    public MilestoneResource(MilestoneType type, ZonedDateTime date, Long competitionId) {
        this.type = type;
        this.date = date;
        this.competitionId = competitionId;
    }

    public MilestoneResource(MilestoneType type, ZonedDateTime date, Long competitionId, Long assessmentPeriodId) {
        this.type = type;
        this.date = date;
        this.competitionId = competitionId;
        this.assessmentPeriodId = assessmentPeriodId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MilestoneType getType() {
        return type;
    }

    public void setType(MilestoneType milestoneType) {
        this.type = milestoneType;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public void setAssessmentPeriodId(Long assessmentPeriodId) {
        this.assessmentPeriodId = assessmentPeriodId;
    }
}
