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
                createProjectSetupStage(competition, PROJECT_DETAILS, 1),
                createProjectSetupStage(competition, PROJECT_TEAM, 2),
                createProjectSetupStage(competition, DOCUMENTS, 3),
                createProjectSetupStage(competition, MONITORING_OFFICER, 4),
                createProjectSetupStage(competition, BANK_DETAILS, 5),
                createProjectSetupStage(competition, FINANCE_CHECKS, 6),
                createProjectSetupStage(competition, SPEND_PROFILE, 7),
                createProjectSetupStage(competition, GRANT_OFFER_LETTER, 8)
        );

        competition.setProjectStages(stages);

    }

    private static void addLoanProjectSetupColumns(Competition competition) {

        List<ProjectStages> stages = asList(
                createProjectSetupStage(competition, PROJECT_DETAILS, 1),
                createProjectSetupStage(competition, PROJECT_TEAM, 2),
                createProjectSetupStage(competition, DOCUMENTS, 3),
                createProjectSetupStage(competition, MONITORING_OFFICER, 4),
                createProjectSetupStage(competition, FINANCE_CHECKS, 5),
                createProjectSetupStage(competition, SPEND_PROFILE, 6)
        );

        competition.setProjectStages(stages);
    }

    private static ProjectStages createProjectSetupStage(Competition competition, ProjectSetupStage projectSetupStage, long priorty) {
        return new ProjectStages(competition, projectSetupStage, priorty);
    }
}
