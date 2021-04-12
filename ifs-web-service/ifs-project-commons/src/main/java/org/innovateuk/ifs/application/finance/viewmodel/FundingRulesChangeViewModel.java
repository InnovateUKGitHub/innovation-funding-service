package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.competition.resource.FundingRules;

public class FundingRulesChangeViewModel {

    private Boolean applicationFinanceNiDeclaration;
    private Boolean projectFinanceNiDeclaration;

    public FundingRulesChangeViewModel(Boolean applicationFinanceNiDeclaration, Boolean projectFinanceNiDeclaration) {
        this.applicationFinanceNiDeclaration = applicationFinanceNiDeclaration;
        this.projectFinanceNiDeclaration = projectFinanceNiDeclaration;
    }

    public String getApplicationBasis() {
        return basis(applicationFinanceNiDeclaration);
    }
    public String getProjectBasis() {
        return basis(projectFinanceNiDeclaration);
    }

    public boolean isRulesDifferent() {
        return applicationFinanceNiDeclaration != null && projectFinanceNiDeclaration != null
                && !applicationFinanceNiDeclaration.equals(projectFinanceNiDeclaration);
    }

    private static String basis(Boolean niDeclaration) {
        if (Boolean.TRUE.equals(niDeclaration)) {
            return FundingRules.STATE_AID.getDisplayName();
        }
        if (Boolean.FALSE.equals(niDeclaration)) {
            return FundingRules.SUBSIDY_CONTROL.getDisplayName();
        }
        return null;
    }
}
