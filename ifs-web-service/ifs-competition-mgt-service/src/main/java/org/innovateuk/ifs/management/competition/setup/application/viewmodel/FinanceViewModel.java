package org.innovateuk.ifs.management.competition.setup.application.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupSubsectionViewModel;

public class FinanceViewModel implements CompetitionSetupSubsectionViewModel {

    private boolean noFinancesCompetition;

    private boolean KTPCompetition;

    public FinanceViewModel(boolean noFinancesCompetition, boolean KTPCompetition) {
        this.noFinancesCompetition = noFinancesCompetition;
        this.KTPCompetition = KTPCompetition;
    }

    public boolean isNoFinancesCompetition() {
        return noFinancesCompetition;
    }

    public boolean isKTPCompetition() {
        return KTPCompetition;
    }
}
