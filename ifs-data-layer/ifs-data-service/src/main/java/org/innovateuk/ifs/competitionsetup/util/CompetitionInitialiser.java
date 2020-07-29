package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionFinanceRowTypes;
import org.innovateuk.ifs.competition.repository.CompetitionFinanceRowsTypesRepository;
import org.innovateuk.ifs.project.core.domain.ProjectStages;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;

@Component
public class CompetitionInitialiser {

    @Autowired
    private CompetitionFinanceRowsTypesRepository competitionFinanceRowsTypesRepository;
    
    public Competition initialiseProjectSetupColumns(Competition competition) {
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

    public Competition initialiseFinanceTypes(Competition competition) {
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

    private void addKtpFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceRowTypes().addAll(
                newArrayList(
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, ASSOCIATE_SALARY_COSTS, 1)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, ASSOCIATE_SALARY_COSTS, 2)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, ASSOCIATE_DEVELOPMENT_COSTS, 3)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, KNOWLEDGE_BASE, 4)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, ADDITIONAL_COMPANY_COSTS, 5)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, CONSUMABLES, 6)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, TRAVEL, 7)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OTHER_COSTS, 8)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, ASSOCIATE_SUPPORT, 9)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, ESTATE_COSTS, 10)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, SUBCONTRACTING_COSTS, 11)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, FINANCE, 12)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OTHER_FUNDING, 13)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, YOUR_FINANCE, 14))
                ));
    }

    private void addLoanFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceRowTypes().addAll(
                newArrayList(
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, LABOUR, 1)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OVERHEADS, 2)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, MATERIALS, 3)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, CAPITAL_USAGE, 4)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, SUBCONTRACTING_COSTS, 5)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, TRAVEL, 6)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OTHER_COSTS, 7)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, GRANT_CLAIM_AMOUNT, 8)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OTHER_FUNDING, 9)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, YOUR_FINANCE, 10))
                ));
    }

    private void addProcurementFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceRowTypes().addAll(
                newArrayList(
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, LABOUR, 1)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, PROCUREMENT_OVERHEADS, 2)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, MATERIALS, 3)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, CAPITAL_USAGE, 4)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, SUBCONTRACTING_COSTS, 5)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, TRAVEL, 6)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OTHER_COSTS, 7)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, FINANCE, 8)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OTHER_FUNDING, 9)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, YOUR_FINANCE, 10)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, VAT, 11))
                ));
    }

    private void addDefaultFinanceTypes(Competition competition) {
        competition.getCompetitionFinanceRowTypes().addAll(
                newArrayList(
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, LABOUR, 1)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OVERHEADS, 2)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, MATERIALS, 3)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, CAPITAL_USAGE, 4)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, SUBCONTRACTING_COSTS, 5)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, TRAVEL, 6)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OTHER_COSTS, 7)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, FINANCE, 8)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, OTHER_FUNDING, 9)),
                        competitionFinanceRowsTypesRepository.save(new CompetitionFinanceRowTypes(competition, YOUR_FINANCE, 10))
                ));
    }

    private void addLoanProjectSetupColumns(Competition competition) {

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

    private void addProcurementProjectSetupColumns(Competition competition) {

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

    private void addDefaultProjectSetupColumns(Competition competition) {

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

    private ProjectStages createProjectSetupStage(Competition competition, ProjectSetupStage projectSetupStage) {
        return new ProjectStages(competition, projectSetupStage);
    }
}
