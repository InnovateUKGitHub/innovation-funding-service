package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigInteger;

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
        return false;
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
