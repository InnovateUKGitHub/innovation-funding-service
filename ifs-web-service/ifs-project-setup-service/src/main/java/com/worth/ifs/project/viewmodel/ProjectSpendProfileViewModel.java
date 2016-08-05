package com.worth.ifs.project.viewmodel;

import java.time.LocalDate;

import com.worth.ifs.project.resource.ProjectResource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * View model to back the Spend Profile page
 */
public class ProjectSpendProfileViewModel {

    private Long projectId;
    private String projectName;
    private LocalDate targetProjectStartDate;
    private Long durationInMonths;
    private SpendProfileSummaryModel summary;

    public ProjectSpendProfileViewModel(ProjectResource project, final SpendProfileSummaryModel summary) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.targetProjectStartDate = project.getTargetStartDate();
        this.durationInMonths = project.getDurationInMonths();
        this.summary = summary;
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

    public SpendProfileSummaryModel getSummary() {
        return summary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectSpendProfileViewModel viewModel = (ProjectSpendProfileViewModel) o;

        return new EqualsBuilder()
                .append(projectId, viewModel.projectId)
                .append(projectName, viewModel.projectName)
                .append(targetProjectStartDate, viewModel.targetProjectStartDate)
                .append(durationInMonths, viewModel.durationInMonths)
                .append(summary, viewModel.summary)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .toHashCode();
    }
}
