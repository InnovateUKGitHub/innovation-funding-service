package com.worth.ifs.finance.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ProjectFinanceRow extends FinanceRow<ProjectFinance> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private ProjectFinance target;

    private Long applicationRowId;

    // Required for ORM - don't remove
    public ProjectFinanceRow() {
    }

    public ProjectFinanceRow(ProjectFinance target) {
        this.target = target;
    }

    @Override
    public void setTarget(ProjectFinance application) {
        target = application;
    }

    @Override
    public ProjectFinance getTarget() {
        return target;
    }

    public Long getApplicationRowId() {
        return applicationRowId;
    }

    public void setApplicationRowId(Long applicationRowId) {
        this.applicationRowId = applicationRowId;
    }
}
