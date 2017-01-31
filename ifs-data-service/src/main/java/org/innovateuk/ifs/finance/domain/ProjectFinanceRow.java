package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Question;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * An Entity similar to ApplicationFinanceRow, which represents a Project Finance Checks version of an Application Finance Row
 * (e.g. a single Labour row from the original Application Form Finances).  The original Application row is retained as
 * a link in this entity
 */
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

    public ProjectFinanceRow(ProjectFinance projectFinance, Question question) {
        super(question);
        this.target = projectFinance;
    }

    public ProjectFinanceRow(Long id, String name, String item, String description, Integer quantity,
                             BigDecimal cost, ProjectFinance projectFinance, Question question) {
        super(id, name, item, description, quantity, cost, question);
        this.target = projectFinance;
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
