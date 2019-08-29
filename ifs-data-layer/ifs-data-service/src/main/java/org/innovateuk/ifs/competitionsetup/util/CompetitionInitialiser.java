package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;

import java.util.EnumSet;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStages.*;

public final class CompetitionInitialiser {

    public static Competition initialiseFinanceTypes(Competition competition) {
        if (competition.getFundingType() == null) {
            return competition;
        }
        switch (competition.getFundingType()) {
            case GRANT:
                addGrantFinanceTypes(competition);
                addDefaultProjectSetupColumns(competition);
                break;
            case LOAN:
                addLoanFinanceTypes(competition);
                addLoanProjectSetupColumns(competition);
                break;
            case PROCUREMENT:
                addProcurementFinanceTypes(competition);
                addDefaultProjectSetupColumns(competition);
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

    private static void addDefaultProjectSetupColumns(Competition competition) {
        competition.getProjectSetupStages().addAll(EnumSet.of(
                PROJECT_DETAILS,
                PROJECT_TEAM,
                DOCUMENTS,
                MONITORING_OFFICER,
                BANK_DETAILS,
                FINANCE_CHECKS,
                SPEND_PROFILE,
                GRANT_OFFER_LETTER
        ));
    }

    private static void addLoanProjectSetupColumns(Competition competition) {
        competition.getProjectSetupStages().addAll(EnumSet.of(
                PROJECT_DETAILS,
                PROJECT_TEAM,
                DOCUMENTS,
                MONITORING_OFFICER,
                FINANCE_CHECKS,
                SPEND_PROFILE
        ));
    }
}
