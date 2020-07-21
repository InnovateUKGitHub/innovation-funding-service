package org.innovateuk.ifs.management.competition.setup.application.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupSubsectionViewModel;

public class FinanceViewModel implements CompetitionSetupSubsectionViewModel {

    private boolean noFinancesCompetition;

    private boolean KtpCompetition;

    public FinanceViewModel(boolean noFinancesCompetition, boolean KtpCompetition) {
        this.noFinancesCompetition = noFinancesCompetition;
        this.KtpCompetition = KtpCompetition;
    }

    public boolean isNoFinancesCompetition() {
        return noFinancesCompetition;
    }

    public boolean isKtpCompetition() {
        return KtpCompetition;
    }
}
