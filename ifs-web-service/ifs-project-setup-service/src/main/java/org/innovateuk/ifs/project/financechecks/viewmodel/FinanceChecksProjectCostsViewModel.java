package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.List;

public class FinanceChecksProjectCostsViewModel extends YourProjectCostsViewModel {

    private final boolean canEditProjectCosts;

    public FinanceChecksProjectCostsViewModel(long applicationId, List<FinanceRowType> financeRowTypes, boolean overheadAlwaysTwenty,
                                              String competitionName, boolean ktpCompetition, boolean ktpPhase2Enabled,
                                              boolean canEditProjectCosts, boolean thirdPartyOfgem, boolean hecpCompetition, String hash) {
        super(false, false, false, false, ktpCompetition, ktpPhase2Enabled, financeRowTypes, overheadAlwaysTwenty, competitionName, applicationId, thirdPartyOfgem, hecpCompetition, hash);
        this.canEditProjectCosts = canEditProjectCosts;
    }

    public boolean isCanEditProjectCosts() {
        return canEditProjectCosts;
    }
}
