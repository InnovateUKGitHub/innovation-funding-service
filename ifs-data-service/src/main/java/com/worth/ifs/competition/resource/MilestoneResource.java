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
        OPEN_DATE("1. Open date"),
        BRIEFING_EVENT("2. Briefing event"),
        SUBMISSION_DATE("3. Submission date"),
        ALLOCATE_ASSESSORS("4. Allocate accessors"),
        ASSESSOR_BRIEFING("5. Assessor briefing"),
        ASSESSOR_ACCEPTS("6. Assessor accepts"),
        ASSESSOR_DEADLINE("7. Assessor deadline"),
        LINE_DRAW("8. Line draw"),
        ASSESSMENT_PANEL("9. Assessment panel"),
        PANEL_DATE("10. Panel date"),
        FUNDERS_PANEL("11. Funders panel"),
        NOTIFICATIONS("12. Notifications"),
        RELEASE_FEEDBACK("13. Release feedback");

        private String milestoneDescription;

        MilestoneName(String milestoneDescription) {
            this.milestoneDescription = milestoneDescription;
        }

        public String getMilestoneDescription() {
            return milestoneDescription;
        }
    }
}
