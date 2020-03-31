package org.innovateuk.ifs.project.eligibility.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.Set;

public class FinanceChecksProjectCostsViewModel extends YourProjectCostsViewModel {

    private final FinanceRowType editableRowType;

    public FinanceChecksProjectCostsViewModel(boolean open, FinanceRowType editableRowType, Set<FinanceRowType> financeRowTypes, long competitionId) {
        super(open, true, false, financeRowTypes, competitionId);
        this.editableRowType = editableRowType;
    }

    @Override
    public boolean isReadOnly(FinanceRowType type) {
        return isReadOnly() || !type.equals(editableRowType);
    }

    public FinanceRowType getEditableRowType() {
        return editableRowType;
    }
}
