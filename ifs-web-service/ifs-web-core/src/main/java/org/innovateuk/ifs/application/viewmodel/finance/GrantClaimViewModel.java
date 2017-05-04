package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

public class GrantClaimViewModel extends AbstractCostViewModel {

    private Long organisationGrantClaimPercentageId;
    private Integer maximumGrantClaimPercentage;
    private Integer organisationGrantClaimPercentage;

    @Override
    protected FormInputType formInputType() {
        return FormInputType.FINANCE;
    }

    @Override
    public FinanceRowType rowType() {
        return FinanceRowType.FINANCE;
    }
}
