package com.worth.ifs.competition.resource;

import java.time.LocalDateTime;

public class MilestoneResource {
    private Long id;
    private MilestoneName name;
    private LocalDateTime date;
    private Long competition;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MilestoneName getName() {
        return name;
    }

    public void setName(MilestoneName name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }
    
    public enum MilestoneName {
        OPEN_DATE,
        BRIEFING_EVENT,
        SUBMISSION_DATE,
        ALLOCATE_ASSESSORS,
        ASSESSOR_BRIEFING,
        ASSESSOR_ACCEPTS,
        ASSESSOR_DEADLINE,
        LINE_DRAW,
        ASSESSMENT_PANEL,
        PANEL_DATE,
        FUNDERS_PANEL,
        NOTIFICATIONS,
        RELEASE_FEEDBACK;
    }
}
