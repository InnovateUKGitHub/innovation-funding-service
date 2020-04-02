package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.Set;

public class FinanceChecksProjectCostsViewModel extends YourProjectCostsViewModel {

    public FinanceChecksProjectCostsViewModel(Set<FinanceRowType> financeRowTypes, long competitionId) {
        super(false, false, false, financeRowTypes, competitionId);
    }
}
