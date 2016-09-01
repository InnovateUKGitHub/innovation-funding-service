package com.worth.ifs.project.viewmodel;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

/**
 * View model to back the Spend Profile page. Also wraps SpendProfileSummaryModel for summary table below spend profile.
 */
public class ProjectSpendProfileViewModel {

    private Long projectId;
    private String projectName;
    private LocalDate targetProjectStartDate;
    private Long durationInMonths;
    private SpendProfileSummaryModel summary;
    private SpendProfileTableResource table;

    public ProjectSpendProfileViewModel(ProjectResource project, SpendProfileTableResource table, SpendProfileSummaryModel summary) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.targetProjectStartDate = project.getTargetStartDate();
        this.durationInMonths = project.getDurationInMonths();
        this.summary = summary;
        this.table = table;
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

    public SpendProfileTableResource getTable() {
        return table;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setTargetProjectStartDate(LocalDate targetProjectStartDate) {
        this.targetProjectStartDate = targetProjectStartDate;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public void setSummary(SpendProfileSummaryModel summary) {
        this.summary = summary;
    }

    public void setTable(SpendProfileTableResource table) {
        this.table = table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectSpendProfileViewModel that = (ProjectSpendProfileViewModel) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(targetProjectStartDate, that.targetProjectStartDate)
                .append(durationInMonths, that.durationInMonths)
                .append(summary, that.summary)
                .append(table, that.table)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(projectName)
                .append(targetProjectStartDate)
                .append(durationInMonths)
                .append(summary)
                .append(table)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("projectId", projectId)
                .append("projectName", projectName)
                .append("targetProjectStartDate", targetProjectStartDate)
                .append("durationInMonths", durationInMonths)
                .append("summary", summary)
                .append("table", table)
                .toString();
    }
}
