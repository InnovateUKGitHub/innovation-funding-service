package org.innovateuk.ifs.competitionsetup.application.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.setup.resource.ApplicationFinanceType;

public class FinanceForm extends CompetitionSetupForm {

    private ApplicationFinanceType applicationFinanceType;

    private boolean includeGrowthTable;

    @NotBlank(message = "{validation.field.must.not.be.blank}")
    private String fundingRules;

    public ApplicationFinanceType getApplicationFinanceType() {
        return applicationFinanceType;
    }

    public void setApplicationFinanceType(ApplicationFinanceType applicationFinanceType) {
        this.applicationFinanceType = applicationFinanceType;
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
