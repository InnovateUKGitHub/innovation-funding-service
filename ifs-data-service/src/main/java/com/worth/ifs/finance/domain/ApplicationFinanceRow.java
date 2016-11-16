package com.worth.ifs.finance.domain;

import com.worth.ifs.Application;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ApplicationFinanceRow extends FinanceRow<Application> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private Application target;

    // Required for ORM - don't remove
    public ApplicationFinanceRow() {
    }

    public ApplicationFinanceRow(Application target) {
        this.target = target;
    }

    @Override
    public void setTarget(Application application) {
        target = application;
    }

    @Override
    public Application getTarget() {
        return target;
    }
}
