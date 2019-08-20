package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;

import java.util.EnumSet;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

public final class CompetitionInitialiser {

    public static Competition initialiseFinanceTypes(Competition competition) {
        switch (competition.getFundingType()) {
            case GRANT:
                addGrantFinanceTypes(competition);
                break;
            case LOAN:
                addLoanFinanceTypes(competition);
                break;
            case PROCUREMENT:
                addProcurementFinanceTypes(competition);
            default:
                break;
        }
        return competition;
    }

    private static void addLoanFinanceTypes(Competition competition) {
        competition.getFinanceRowTypes().addAll(EnumSet.of(
                LABOUR,
                OVERHEADS,
                MATERIALS,
                CAPITAL_USAGE,
                SUBCONTRACTING_COSTS,
                TRAVEL,
                OTHER_COSTS,
                GRANT_CLAIM_AMOUNT,
                OTHER_FUNDING,
                YOUR_FINANCE
        ));
    }

    private static void addProcurementFinanceTypes(Competition competition) {
        competition.getFinanceRowTypes().addAll(EnumSet.of(
                LABOUR,
                PROCUREMENT_OVERHEADS,
                MATERIALS,
                CAPITAL_USAGE,
                SUBCONTRACTING_COSTS,
                TRAVEL,
                OTHER_COSTS,
                FINANCE,
                OTHER_FUNDING,
                VAT,
                YOUR_FINANCE
        ));
    }

    private static void addGrantFinanceTypes(Competition competition) {
        competition.getFinanceRowTypes().addAll(EnumSet.of(
                LABOUR,
                OVERHEADS,
                MATERIALS,
                CAPITAL_USAGE,
                SUBCONTRACTING_COSTS,
                TRAVEL,
                OTHER_COSTS,
                FINANCE,
                OTHER_FUNDING,
                YOUR_FINANCE
        ));
    }
}
