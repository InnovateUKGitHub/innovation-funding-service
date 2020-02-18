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

    public static Competition initialiseProjectSetupColumns(Competition competition) {
        if (competition.getFundingType() == null) {
            return competition;
        }
        switch (competition.getFundingType()) {
            case GRANT:
                addGrantProjectSetupColumns(competition);
                break;
            case LOAN:
                addLoanProjectSetupColumns(competition);
                break;
            case PROCUREMENT:
                addProcurementProjectSetupColumns(competition);
            case KTP:
                addKTPProjectSetupColumns(competition);
            default:
                break;
        }
        return competition;

    }
    public static Competition initialiseFinanceTypes(Competition competition) {
        if (competition.getFundingType() == null) {
            return competition;
        }
        switch (competition.getFundingType()) {
            case GRANT:
                addGrantFinanceTypes(competition);
                break;
            case LOAN:
                addLoanFinanceTypes(competition);
                break;
            case PROCUREMENT:
                addProcurementFinanceTypes(competition);
            case KTP:
                addKTPFinanceTypes(competition);
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

    private static void addKTPFinanceTypes(Competition competition) {
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

    private static void addGrantProjectSetupColumns(Competition competition) {

        List<ProjectStages> stages = asList(
                createProjectSetupStage(competition, PROJECT_DETAILS),
                createProjectSetupStage(competition, PROJECT_TEAM),
                createProjectSetupStage(competition, DOCUMENTS),
                createProjectSetupStage(competition, MONITORING_OFFICER),
                createProjectSetupStage(competition, BANK_DETAILS),
                createProjectSetupStage(competition, FINANCE_CHECKS),
                createProjectSetupStage(competition, SPEND_PROFILE),
                createProjectSetupStage(competition, GRANT_OFFER_LETTER)
        );

        competition.setProjectStages(stages);

    }

    private static void addLoanProjectSetupColumns(Competition competition) {

        List<ProjectStages> stages = asList(
                createProjectSetupStage(competition, PROJECT_DETAILS),
                createProjectSetupStage(competition, PROJECT_TEAM),
                createProjectSetupStage(competition, MONITORING_OFFICER),
                createProjectSetupStage(competition, FINANCE_CHECKS),
                createProjectSetupStage(competition, SPEND_PROFILE),
                createProjectSetupStage(competition, PROJECT_SETUP_COMPLETE)
        );

        competition.setProjectStages(stages);
    }

    private static void addProcurementProjectSetupColumns(Competition competition) {

        List<ProjectStages> stages = asList(
                createProjectSetupStage(competition, PROJECT_DETAILS),
                createProjectSetupStage(competition, PROJECT_TEAM),
                createProjectSetupStage(competition, MONITORING_OFFICER),
                createProjectSetupStage(competition, BANK_DETAILS),
                createProjectSetupStage(competition, FINANCE_CHECKS),
                createProjectSetupStage(competition, SPEND_PROFILE),
                createProjectSetupStage(competition, GRANT_OFFER_LETTER)
        );

        competition.setProjectStages(stages);
    }

    private static void addKTPProjectSetupColumns(Competition competition) {

        List<ProjectStages> stages = asList(
                createProjectSetupStage(competition, PROJECT_DETAILS),
                createProjectSetupStage(competition, PROJECT_TEAM),
                createProjectSetupStage(competition, DOCUMENTS),
                createProjectSetupStage(competition, MONITORING_OFFICER),
                createProjectSetupStage(competition, BANK_DETAILS),
                createProjectSetupStage(competition, FINANCE_CHECKS),
                createProjectSetupStage(competition, SPEND_PROFILE),
                createProjectSetupStage(competition, GRANT_OFFER_LETTER)
        );

        competition.setProjectStages(stages);

    }

    private static ProjectStages createProjectSetupStage(Competition competition, ProjectSetupStage projectSetupStage) {
        return new ProjectStages(competition, projectSetupStage);
    }
}
