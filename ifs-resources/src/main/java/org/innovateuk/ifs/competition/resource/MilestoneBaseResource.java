package org.innovateuk.ifs.competition.resource;

import java.time.ZonedDateTime;

public abstract class MilestoneBaseResource {
    private Long id;

    private MilestoneType type;

    private ZonedDateTime date;

    private Long competitionId;

    public MilestoneBaseResource() {
    }

    public MilestoneBaseResource(MilestoneType type, Long competitionId) {
        this.type = type;
        this.competitionId = competitionId;
    }

    public MilestoneBaseResource(MilestoneType type, ZonedDateTime date, Long competitionId) {
        this.type = type;
        this.date = date;
        this.competitionId = competitionId;
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
}
