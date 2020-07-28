package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionFinanceRowTypes;
import org.innovateuk.ifs.project.core.domain.ProjectStages;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;

public final class CompetitionInitialiser {

    public static Competition initialiseProjectSetupColumns(Competition competition) {
        if (competition.getFundingType() == null) {
            return competition;
        }
        switch (competition.getFundingType()) {
            case LOAN:
                addLoanProjectSetupColumns(competition);
                break;
            case PROCUREMENT:
                addProcurementProjectSetupColumns(competition);
                break;
            case GRANT:
            case KTP:
            case INVESTOR_PARTNERSHIPS:
                addDefaultProjectSetupColumns(competition);
                break;
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
            case LOAN:
                addLoanFinanceTypes(competition);
                break;
            case PROCUREMENT:
                addProcurementFinanceTypes(competition);
                break;
            case KTP:
                addKtpFinanceTypes(competition);
                break;
            case GRANT:
            case INVESTOR_PARTNERSHIPS:
                addDefaultFinanceTypes(competition);
                break;
            default:
                throw new IllegalArgumentException("Unrecognised funding type when initialising competition.");
        }
        return competition;
    }

    private static void addKtpFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceRowTypes().addAll(
                newArrayList(
                        new CompetitionFinanceRowTypes(competition, ASSOCIATE_SALARY_COSTS, 1),
                        new CompetitionFinanceRowTypes(competition, ASSOCIATE_SALARY_COSTS, 2),
                        new CompetitionFinanceRowTypes(competition, ASSOCIATE_DEVELOPMENT_COSTS, 3),
                        new CompetitionFinanceRowTypes(competition, KNOWLEDGE_BASE, 4),
                        new CompetitionFinanceRowTypes(competition, ADDITIONAL_COMPANY_COSTS, 5),
                        new CompetitionFinanceRowTypes(competition, CONSUMABLES, 6),
                        new CompetitionFinanceRowTypes(competition, TRAVEL, 7),
                        new CompetitionFinanceRowTypes(competition, OTHER_COSTS, 8),
                        new CompetitionFinanceRowTypes(competition, ASSOCIATE_SUPPORT, 9),
                        new CompetitionFinanceRowTypes(competition, ESTATE_COSTS, 10),
                        new CompetitionFinanceRowTypes(competition, SUBCONTRACTING_COSTS, 11),
                        new CompetitionFinanceRowTypes(competition, FINANCE, 12),
                        new CompetitionFinanceRowTypes(competition, OTHER_FUNDING, 13),
                        new CompetitionFinanceRowTypes(competition, YOUR_FINANCE, 14)
                ));
    }

    private static void addLoanFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceRowTypes().addAll(
                newArrayList(
                        new CompetitionFinanceRowTypes(competition, LABOUR, 1),
                        new CompetitionFinanceRowTypes(competition, OVERHEADS, 2),
                        new CompetitionFinanceRowTypes(competition, MATERIALS, 3),
                        new CompetitionFinanceRowTypes(competition, CAPITAL_USAGE, 4),
                        new CompetitionFinanceRowTypes(competition, SUBCONTRACTING_COSTS, 5),
                        new CompetitionFinanceRowTypes(competition, TRAVEL, 6),
                        new CompetitionFinanceRowTypes(competition, OTHER_COSTS, 7),
                        new CompetitionFinanceRowTypes(competition, GRANT_CLAIM_AMOUNT, 8),
                        new CompetitionFinanceRowTypes(competition, OTHER_FUNDING, 9),
                        new CompetitionFinanceRowTypes(competition, YOUR_FINANCE, 10)
                ));
    }

    private static void addProcurementFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceRowTypes().addAll(
                newArrayList(
                        new CompetitionFinanceRowTypes(competition, LABOUR, 1),
                        new CompetitionFinanceRowTypes(competition, PROCUREMENT_OVERHEADS, 2),
                        new CompetitionFinanceRowTypes(competition, MATERIALS, 3),
                        new CompetitionFinanceRowTypes(competition, CAPITAL_USAGE, 4),
                        new CompetitionFinanceRowTypes(competition, SUBCONTRACTING_COSTS, 5),
                        new CompetitionFinanceRowTypes(competition, TRAVEL, 6),
                        new CompetitionFinanceRowTypes(competition, OTHER_COSTS, 7),
                        new CompetitionFinanceRowTypes(competition, FINANCE, 8),
                        new CompetitionFinanceRowTypes(competition, OTHER_FUNDING, 9),
                        new CompetitionFinanceRowTypes(competition, YOUR_FINANCE, 10),
                        new CompetitionFinanceRowTypes(competition, VAT, 11)
                ));
    }

    private static void addDefaultFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceRowTypes().addAll(
                newArrayList(
                        new CompetitionFinanceRowTypes(competition, LABOUR, 1),
                        new CompetitionFinanceRowTypes(competition, OVERHEADS, 2),
                        new CompetitionFinanceRowTypes(competition, MATERIALS, 3),
                        new CompetitionFinanceRowTypes(competition, CAPITAL_USAGE, 4),
                        new CompetitionFinanceRowTypes(competition, SUBCONTRACTING_COSTS, 5),
                        new CompetitionFinanceRowTypes(competition, TRAVEL, 6),
                        new CompetitionFinanceRowTypes(competition, OTHER_COSTS, 7),
                        new CompetitionFinanceRowTypes(competition, FINANCE, 8),
                        new CompetitionFinanceRowTypes(competition, OTHER_FUNDING, 9),
                        new CompetitionFinanceRowTypes(competition, YOUR_FINANCE, 10)
                ));
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

    private static void addDefaultProjectSetupColumns(Competition competition) {

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
