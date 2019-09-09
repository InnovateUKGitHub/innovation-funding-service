package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.project.core.domain.ProjectStages;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;

import java.util.EnumSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;

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

        List<ProjectStages> stages = asList(
                createProjectSetupStage(competition, PROJECT_DETAILS, PROJECT_DETAILS_PRIORITY),
                createProjectSetupStage(competition, PROJECT_TEAM, PROJECT_TEAM_PRIORITY),
                createProjectSetupStage(competition, DOCUMENTS, DOCUMENTS_PRIORITY),
                createProjectSetupStage(competition, MONITORING_OFFICER, MONITORING_OFFICER_PRIORITY),
                createProjectSetupStage(competition, BANK_DETAILS, BANK_DETAILS_PRIORITY),
                createProjectSetupStage(competition, FINANCE_CHECKS, FINANCE_CHECKS_PRIORITY),
                createProjectSetupStage(competition, SPEND_PROFILE, SPEND_PROFILE_PRIORITY),
                createProjectSetupStage(competition, GRANT_OFFER_LETTER, GRANT_OFFER_LETTER_PRIORITY)
        );

        competition.setProjectStages(stages);

    }

    private static void addLoanProjectSetupColumns(Competition competition) {

        List<ProjectStages> stages = asList(
                createProjectSetupStage(competition, PROJECT_DETAILS, PROJECT_DETAILS_PRIORITY),
                createProjectSetupStage(competition, PROJECT_TEAM, PROJECT_TEAM_PRIORITY),
                createProjectSetupStage(competition, DOCUMENTS, DOCUMENTS_PRIORITY),
                createProjectSetupStage(competition, MONITORING_OFFICER, MONITORING_OFFICER_PRIORITY),
                createProjectSetupStage(competition, FINANCE_CHECKS, FINANCE_CHECKS_PRIORITY),
                createProjectSetupStage(competition, SPEND_PROFILE, SPEND_PROFILE_PRIORITY)
        );

        competition.setProjectStages(stages);
    }

    private static ProjectStages createProjectSetupStage(Competition competition, ProjectSetupStage projectSetupStage, long priorty) {
        return new ProjectStages(competition, projectSetupStage, priorty);
    }
}
