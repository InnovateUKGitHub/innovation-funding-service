package org.innovateuk.ifs.competition.resource;

import java.time.LocalDateTime;

public class MilestoneResource {
    private Long id;
    private MilestoneType type;
    private LocalDateTime date;
    private Long competitionId;


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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
