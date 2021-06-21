package org.innovateuk.ifs.project.eligibility.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.List;

public class FinanceChecksProjectCostsViewModel extends YourProjectCostsViewModel {

    private final boolean canEditProjectCosts;
    private final boolean auditor;

    public FinanceChecksProjectCostsViewModel(long applicationId, String competitionName, boolean open, List<FinanceRowType> financeRowTypes, boolean overheadAlwaysTwenty, boolean ktpCompetition, boolean canEditProjectCosts, boolean auditor) {
        super(open, true, false, ktpCompetition, financeRowTypes, overheadAlwaysTwenty, competitionName, applicationId, auditor);
        this.canEditProjectCosts = canEditProjectCosts;
        this.auditor = auditor;
    }

    @Override
    public boolean isReadOnly() {
        return !canEditProjectCosts || isAuditor();
    }
    public boolean isCanEditProjectCosts() {
        return canEditProjectCosts;
    }

    @Override
    public boolean isAuditor() {
        return auditor;
    }
}
