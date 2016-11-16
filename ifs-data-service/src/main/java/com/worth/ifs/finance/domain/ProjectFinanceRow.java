package com.worth.ifs.finance.domain;

import com.worth.ifs.project.domain.Project;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class ProjectFinanceRow extends FinanceRow<Project> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private Project target;

    // Required for ORM - don't remove
    public ProjectFinanceRow() {
    }

    public ProjectFinanceRow(Project target) {
        this.target = target;
    }

    @Override
    public void setTarget(Project application) {
        target = application;
    }

    @Override
    public Project getTarget() {
        return target;
    }
}
