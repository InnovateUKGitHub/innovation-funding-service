package org.innovateuk.ifs.project.eligibility.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.List;

public class FinanceChecksProjectCostsViewModel extends YourProjectCostsViewModel {

    private final boolean canEditProjectCosts;

    public FinanceChecksProjectCostsViewModel(long applicationId, String competitionName, boolean open, List<FinanceRowType> financeRowTypes, boolean overheadAlwaysTwenty, boolean ktpCompetition, boolean canEditProjectCosts, boolean isOfGemCompetition) {
        super(open, true, false, false, ktpCompetition, financeRowTypes, overheadAlwaysTwenty, competitionName, applicationId, isOfGemCompetition);
        this.canEditProjectCosts = canEditProjectCosts;
    }

    @Override
    public boolean isReadOnly() {
        return !canEditProjectCosts;
    }
    public boolean isCanEditProjectCosts() {
        return canEditProjectCosts;
    }
}
