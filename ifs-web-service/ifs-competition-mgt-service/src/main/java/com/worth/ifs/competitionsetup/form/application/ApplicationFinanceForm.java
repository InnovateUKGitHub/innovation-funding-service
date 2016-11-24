package com.worth.ifs.competitionsetup.form.application;

import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

public class ApplicationFinanceForm extends CompetitionSetupForm {

    private boolean fullApplicationFinance;

    private boolean includeGrowthTable;

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
}
