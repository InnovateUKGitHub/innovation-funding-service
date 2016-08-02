package com.worth.ifs.project.viewmodel;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

/**
 * View model to back the Spend Profile page
 */
public class ProjectSpendProfileViewModel {

    private Long projectId;
    private String projectName;
    private LocalDate targetProjectStartDate;
    private Long durationInMonths;
    private CompetitionResource competition;

    public ProjectSpendProfileViewModel(ProjectResource project, CompetitionResource competition) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.targetProjectStartDate = project.getTargetStartDate();
        this.durationInMonths = project.getDurationInMonths();
        this.competition = competition;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectSpendProfileViewModel viewModel = (ProjectSpendProfileViewModel) o;

        return new EqualsBuilder()
                .append(projectId, viewModel.projectId)
                .append(targetProjectStartDate, viewModel.targetProjectStartDate)
                .append(durationInMonths, viewModel.durationInMonths)
                .append(competition, viewModel.competition)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .toHashCode();
    }
}
