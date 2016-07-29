package com.worth.ifs.project.viewmodel;

import com.worth.ifs.competition.resource.CompetitionResource;

import java.time.LocalDate;

/**
 * View model to back the Spend Profile page
 */
public class ProjectSpendProfileViewModel {

    private Long projectId;
    private LocalDate targetProjectStartDate;
    private Long durationInMonths;
    private CompetitionResource competition;

    public ProjectSpendProfileViewModel(Long projectId, LocalDate targetProjectStartDate, Long durationInMonths, CompetitionResource competition) {
        this.projectId = projectId;
        this.targetProjectStartDate = targetProjectStartDate;
        this.durationInMonths = durationInMonths;
        this.competition = competition;
    }

    public Long getProjectId() {
        return projectId;
    }

    public LocalDate getTargetProjectStartDate() {
        return targetProjectStartDate;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }
}
