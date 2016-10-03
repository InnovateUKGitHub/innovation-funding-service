package com.worth.ifs.project.viewmodel;

import com.worth.ifs.project.resource.ProjectResource;

/**
 * View model for the review and submit project spend profile.
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

        if (project != null ? !project.equals(that.project) : that.project != null) return false;
        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        return summary != null ? summary.equals(that.summary) : that.summary == null;

    }

    @Override
    public int hashCode() {
        int result = project != null ? project.hashCode() : 0;
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TotalSpendProfileViewModel{" +
                "project=" + project +
                ", table=" + table +
                ", summary=" + summary +
                '}';
    }
}

