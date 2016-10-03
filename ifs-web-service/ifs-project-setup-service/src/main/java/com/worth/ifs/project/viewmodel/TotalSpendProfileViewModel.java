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
}

