package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.Set;

public class FinanceChecksProjectCostsViewModel extends YourProjectCostsViewModel {

    public FinanceChecksProjectCostsViewModel(long applicationId, Set<FinanceRowType> financeRowTypes, boolean overheadAlwaysTwenty, String competitionName) {
        super(false, false, false, financeRowTypes, overheadAlwaysTwenty, competitionName, applicationId);
    }
}
