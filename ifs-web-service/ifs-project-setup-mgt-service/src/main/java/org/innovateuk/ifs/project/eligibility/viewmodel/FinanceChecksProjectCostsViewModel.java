package org.innovateuk.ifs.project.eligibility.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

public class FinanceChecksProjectCostsViewModel extends YourProjectCostsViewModel {

    private final FinanceRowType editableRowType;

    public FinanceChecksProjectCostsViewModel(boolean open, FinanceRowType editableRowType) {
        super(open, true, false);
        this.editableRowType = editableRowType;
    }

    @Override
    public boolean isReadOnly(FinanceRowType type) {
        return isReadOnly() || !type.equals(editableRowType);
    }
}
