package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionFinanceType;
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
        competition.getCompetitionFinanceTypes().addAll(
                newArrayList(
                        new CompetitionFinanceType(competition, ASSOCIATE_SALARY_COSTS, 1),
                        new CompetitionFinanceType(competition, ASSOCIATE_SALARY_COSTS, 2),
                        new CompetitionFinanceType(competition, ASSOCIATE_DEVELOPMENT_COSTS, 3),
                        new CompetitionFinanceType(competition, KNOWLEDGE_BASE, 4),
                        new CompetitionFinanceType(competition, ADDITIONAL_COMPANY_COSTS, 5),
                        new CompetitionFinanceType(competition, CONSUMABLES, 6),
                        new CompetitionFinanceType(competition, TRAVEL, 7),
                        new CompetitionFinanceType(competition, OTHER_COSTS, 8),
                        new CompetitionFinanceType(competition, ASSOCIATE_SUPPORT, 9),
                        new CompetitionFinanceType(competition, ESTATE_COSTS, 10),
                        new CompetitionFinanceType(competition, SUBCONTRACTING_COSTS, 11),
                        new CompetitionFinanceType(competition, FINANCE, 12),
                        new CompetitionFinanceType(competition, OTHER_FUNDING, 13),
                        new CompetitionFinanceType(competition, YOUR_FINANCE, 14)
                ));
    }

    private static void addLoanFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceTypes().addAll(
                newArrayList(
                        new CompetitionFinanceType(competition, LABOUR, 1),
                        new CompetitionFinanceType(competition, OVERHEADS, 2),
                        new CompetitionFinanceType(competition, MATERIALS, 3),
                        new CompetitionFinanceType(competition, CAPITAL_USAGE, 4),
                        new CompetitionFinanceType(competition, SUBCONTRACTING_COSTS, 5),
                        new CompetitionFinanceType(competition, TRAVEL, 6),
                        new CompetitionFinanceType(competition, OTHER_COSTS, 7),
                        new CompetitionFinanceType(competition, GRANT_CLAIM_AMOUNT, 8),
                        new CompetitionFinanceType(competition, OTHER_FUNDING, 9),
                        new CompetitionFinanceType(competition, YOUR_FINANCE, 10)
                ));
    }

    private static void addProcurementFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceTypes().addAll(
                newArrayList(
                        new CompetitionFinanceType(competition, LABOUR, 1),
                        new CompetitionFinanceType(competition, PROCUREMENT_OVERHEADS, 2),
                        new CompetitionFinanceType(competition, MATERIALS, 3),
                        new CompetitionFinanceType(competition, CAPITAL_USAGE, 4),
                        new CompetitionFinanceType(competition, SUBCONTRACTING_COSTS, 5),
                        new CompetitionFinanceType(competition, TRAVEL, 6),
                        new CompetitionFinanceType(competition, OTHER_COSTS, 7),
                        new CompetitionFinanceType(competition, FINANCE, 8),
                        new CompetitionFinanceType(competition, OTHER_FUNDING, 9),
                        new CompetitionFinanceType(competition, YOUR_FINANCE, 10),
                        new CompetitionFinanceType(competition, VAT, 11)
                ));
    }

    private static void addDefaultFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceTypes().addAll(
                newArrayList(
                        new CompetitionFinanceType(competition, LABOUR, 1),
                        new CompetitionFinanceType(competition, OVERHEADS, 2),
                        new CompetitionFinanceType(competition, MATERIALS, 3),
                        new CompetitionFinanceType(competition, CAPITAL_USAGE, 4),
                        new CompetitionFinanceType(competition, SUBCONTRACTING_COSTS, 5),
                        new CompetitionFinanceType(competition, TRAVEL, 6),
                        new CompetitionFinanceType(competition, OTHER_COSTS, 7),
                        new CompetitionFinanceType(competition, FINANCE, 8),
                        new CompetitionFinanceType(competition, OTHER_FUNDING, 9),
                        new CompetitionFinanceType(competition, YOUR_FINANCE, 10)
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
