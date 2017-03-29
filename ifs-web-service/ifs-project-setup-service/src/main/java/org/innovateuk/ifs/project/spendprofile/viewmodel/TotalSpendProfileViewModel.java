package org.innovateuk.ifs.project.spendprofile.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.project.model.SpendProfileSummaryModel;
import org.innovateuk.ifs.project.resource.ProjectResource;

/**
 * View model for the review and send project spend profile.
 */
public class TotalSpendProfileViewModel {
    private ProjectResource project;
    private TotalProjectSpendProfileTableViewModel table;
    private SpendProfileSummaryModel summary;

    public TotalSpendProfileViewModel(ProjectResource project, TotalProjectSpendProfileTableViewModel table, SpendProfileSummaryModel summary) {
        this.project = project;
        this.table = table;
        this.summary = summary;
    }

    public ProjectResource getProject() {
        return project;
    }
    public TotalProjectSpendProfileTableViewModel getTable() {
        return table;
    }
    public SpendProfileSummaryModel getSummary() { return summary; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TotalSpendProfileViewModel that = (TotalSpendProfileViewModel) o;

        return new EqualsBuilder()
                .append(project, that.project)
                .append(table, that.table)
                .append(summary, that.summary)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(project)
                .append(table)
                .append(summary)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("project", project)
                .append("table", table)
                .append("summary", summary)
                .toString();
    }
}

