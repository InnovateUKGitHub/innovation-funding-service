package org.innovateuk.ifs.competitionsetup.application.form;

import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.competition.resource.ApplicationFinanceType;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

@FieldRequiredIf(required = "includeGrowthTable", argument = "financesRequired", predicate = true, message = "{competition.setup.finances.includeGrowthTable.required}")
@FieldRequiredIf(required = "fundingRules", argument = "financesRequired", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "includeJesForm", argument = "financesRequired", predicate = true, message = "{competition.setup.finances.includeJesForm.required}")
public class FinanceForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private ApplicationFinanceType applicationFinanceType;

    private boolean financesRequired;

    private Boolean includeJesForm;

    private Boolean includeGrowthTable;

    private String fundingRules;

    public ApplicationFinanceType getApplicationFinanceType() {
        return applicationFinanceType;
    }

    public void setApplicationFinanceType(ApplicationFinanceType applicationFinanceType) {
        this.applicationFinanceType = applicationFinanceType;
    }

    public boolean isFinancesRequired() {
        return financesRequired;
    }

    public void setFinancesRequired(boolean financesRequired) {
        this.financesRequired = financesRequired;
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

    public Boolean getIncludeJesForm() {
        return includeJesForm;
    }

    public void setIncludeJesForm(Boolean includeJesForm) {
        this.includeJesForm = includeJesForm;
    }
}
