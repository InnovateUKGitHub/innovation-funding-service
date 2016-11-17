package com.worth.ifs.finance.domain;

import com.worth.ifs.application.domain.Question;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class ApplicationFinanceRow extends FinanceRow<ApplicationFinance> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private ApplicationFinance target;

    // Required for ORM - don't remove
    public ApplicationFinanceRow() {
        super();
    }

    public ApplicationFinanceRow(ApplicationFinance target) {
        super();
        this.target = target;
    }

    public ApplicationFinanceRow(Long id, String name, String item, String description, Integer quantity, BigDecimal cost,
                                 ApplicationFinance applicationFinance, Question question) {
        super(id, name, item, description, quantity, cost, question);
        this.target = applicationFinance;
    }

    public ApplicationFinanceRow(ApplicationFinance applicationFinance, Question question) {
        super(question);
        this.target = applicationFinance;
    }

    @Override
    public void setTarget(ApplicationFinance application) {
        target = application;
    }

    @Override
    public ApplicationFinance getTarget() {
        return target;
    }
}
