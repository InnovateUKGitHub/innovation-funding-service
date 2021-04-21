package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.validation.constraints.Min;
import java.math.BigInteger;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.VALUE_MUST_BE_HIGHER_MESSAGE;

public class AcademicAndSecretarialSupportCostRowForm extends AbstractCostRowForm<AcademicAndSecretarialSupport> {

    private BigInteger cost;

    public AcademicAndSecretarialSupportCostRowForm() {}

    public AcademicAndSecretarialSupportCostRowForm(AcademicAndSecretarialSupport cost) {
        super(cost);
        this.cost = cost.getCost();
    }

    public BigInteger getCost() {
        return cost;
    }

    public void setCost(BigInteger cost) {
        this.cost = cost;
    }

    @Override
    public boolean isBlank() {
        return cost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT;
    }

    @Override
    public AcademicAndSecretarialSupport toCost(Long financeId) {
        return new AcademicAndSecretarialSupport(financeId);
    }
}
