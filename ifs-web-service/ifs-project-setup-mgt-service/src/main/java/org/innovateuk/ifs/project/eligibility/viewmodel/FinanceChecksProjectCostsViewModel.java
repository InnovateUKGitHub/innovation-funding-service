package org.innovateuk.ifs.project.eligibility.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.List;

public class FinanceChecksProjectCostsViewModel extends YourProjectCostsViewModel {

//    private final FinanceRowType editableRowType;
    private final boolean isInEditPage;

    public FinanceChecksProjectCostsViewModel(long applicationId, String competitionName, boolean open, boolean isInEditPage, List<FinanceRowType> financeRowTypes, boolean overheadAlwaysTwenty, boolean ktpCompetition) {
        super(open, true, false, ktpCompetition, financeRowTypes, overheadAlwaysTwenty, competitionName, applicationId);
        this.isInEditPage = isInEditPage;
//        this.editableRowType = editableRowType;
    }

    public boolean isInEditPage() {
        return isInEditPage;
    }

    @Override
    public boolean isReadOnly(FinanceRowType type) {
                return isReadOnly();
    }
//        return isReadOnly() || !type.equals(editableRowType);
//    }
//
//    public FinanceRowType getEditableRowType() {
//        return editableRowType;
//    }
}
