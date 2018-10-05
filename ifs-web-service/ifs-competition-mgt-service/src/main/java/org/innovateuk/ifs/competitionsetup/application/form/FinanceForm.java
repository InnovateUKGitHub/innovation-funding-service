package org.innovateuk.ifs.competitionsetup.application.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.competition.resource.ApplicationFinanceType;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

public class FinanceForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private ApplicationFinanceType applicationFinanceType;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Boolean includeGrowthTable;

    @NotBlank(message = "{validation.field.must.not.be.blank}")
    private String fundingRules;

    public ApplicationFinanceType getApplicationFinanceType() {
        return applicationFinanceType;
    }

    public void setApplicationFinanceType(ApplicationFinanceType applicationFinanceType) {
        this.applicationFinanceType = applicationFinanceType;
    }

    public Boolean getIncludeGrowthTable() {
        return includeGrowthTable;
    }

    public void setIncludeGrowthTable(Boolean includeGrowthTable) {
        this.includeGrowthTable = includeGrowthTable;
    }
    
    public String getFundingRules() {
        return fundingRules;
    }

    public void setFundingRules(String fundingRules) {
        this.fundingRules = fundingRules;
    }
}
