package org.innovateuk.ifs.management.competition.setup.application.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupSubsectionViewModel;

public class FinanceViewModel implements CompetitionSetupSubsectionViewModel {

    private boolean noFinancesCompetition;
    private boolean showPaymentMilestonesInCompetition;
    private boolean ktpCompetition;

    public FinanceViewModel(boolean noFinancesCompetition, boolean ktpCompetition, boolean noPaymentMilestonesCompetition) {
        this.noFinancesCompetition = noFinancesCompetition;
        this.ktpCompetition = ktpCompetition;
        this.showPaymentMilestonesInCompetition = noPaymentMilestonesCompetition;
    }

    public boolean isShowPaymentMilestonesInCompetition() {
        return showPaymentMilestonesInCompetition;
    }

    public boolean isNoFinancesCompetition() {
        return noFinancesCompetition;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }
}
