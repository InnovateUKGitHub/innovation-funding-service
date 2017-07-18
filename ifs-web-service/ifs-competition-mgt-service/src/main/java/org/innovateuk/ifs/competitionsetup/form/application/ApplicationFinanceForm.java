package org.innovateuk.ifs.competitionsetup.form.application;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;

public class ApplicationFinanceForm extends CompetitionSetupForm {

    private boolean fullApplicationFinance;

    private boolean includeGrowthTable;

    @NotBlank(message = "{validation.field.must.not.be.blank}")
    private String fundingRules;

    public boolean isFullApplicationFinance() {
        return fullApplicationFinance;
    }

    public void setFullApplicationFinance(boolean fullApplicationFinance) {
        this.fullApplicationFinance = fullApplicationFinance;
    }

    public boolean isIncludeGrowthTable() {
        return includeGrowthTable;
    }

    public void setIncludeGrowthTable(boolean includeGrowthTable) {
        this.includeGrowthTable = includeGrowthTable;
    }

    public String getFundingRules() {
        return fundingRules;
    }

    public void setFundingRules(String fundingRules) {
        this.fundingRules = fundingRules;
    }
}
