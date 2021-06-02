package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.List;

public class FinanceChecksProjectCostsViewModel extends YourProjectCostsViewModel {

    private final boolean canEditProjectCosts;

    public FinanceChecksProjectCostsViewModel(long applicationId, List<FinanceRowType> financeRowTypes, boolean overheadAlwaysTwenty, String competitionName, boolean ktpCompetition, boolean canEditProjectCosts) {
        super(false, false, false, ktpCompetition, financeRowTypes, overheadAlwaysTwenty, competitionName, applicationId);
        this.canEditProjectCosts = canEditProjectCosts;
    }

    public boolean isCanEditProjectCosts() {
        return canEditProjectCosts;
    }
}
