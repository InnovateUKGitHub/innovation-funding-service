package org.innovateuk.ifs.finance.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.innovateuk.ifs.application.domain.Question;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Entity representing an Organisation's high-level Finances in an Application Form
 */
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

    /**
     * Used for comparing application and project finance rows.  Doesn't consider associated meta fields.
     * @param another
     * @return
     */
    public boolean matches(ApplicationFinanceRow another){
        if (another == null) return false;

        return new EqualsBuilder()
                .append(getItem(), another.getItem())
                .append(getCost(), another.getCost())
                .append(getDescription(), another.getDescription())
                .append(getName(), another.getName())
                .append(getQuantity(), another.getQuantity())
                .isEquals();
    }
}
