package org.innovateuk.ifs.competition.viewmodel;

import java.time.LocalDateTime;

/**
 * View model for the competition overview with the public content data
 */
public class CompetitionOverviewViewModel {
    private String competitionTitle;
    private LocalDateTime competitionOpenDate;
    private LocalDateTime competitionCloseDate;
    private Long competitionId;
    private String shortDescription;

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public void setCompetitionTitle(String competitionTitle) {
        this.competitionTitle = competitionTitle;
    }

    public LocalDateTime getCompetitionOpenDate() {
        return competitionOpenDate;
    }

    public void setCompetitionOpenDate(LocalDateTime competitionOpenDate) {
        this.competitionOpenDate = competitionOpenDate;
    }

    public LocalDateTime getCompetitionCloseDate() {
        return competitionCloseDate;
    }

    public void setCompetitionCloseDate(LocalDateTime competitionCloseDate) {
        this.competitionCloseDate = competitionCloseDate;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
}
