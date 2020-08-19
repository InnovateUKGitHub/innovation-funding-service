package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionFinanceRowTypes;
import org.innovateuk.ifs.competition.repository.CompetitionFinanceRowsTypesRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.core.domain.ProjectStages;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

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
        List<FinanceRowType> types;
        switch (competition.getFundingType()) {
            case LOAN:
                types = loanFinanceTypes();
                break;
            case PROCUREMENT:
                types = procurementFinanceTypes();
                break;
            case KTP:
                types = ktpFinanceTypes();
                break;
            case GRANT:
            case INVESTOR_PARTNERSHIPS:
                types = defaultFinanceTypes();
                break;
            default:
                throw new IllegalArgumentException("Unrecognised funding type when initialising competition.");
        }
        IntStream.range(0, types.size()).forEach(i -> {
            competition.getCompetitionFinanceRowTypes().add(
                    competitionFinanceRowsTypesRepository.save(
                            new CompetitionFinanceRowTypes(competition, types.get(i), i)));

        });
        return competition;
    }

    private List<FinanceRowType> ktpFinanceTypes() {
        return newArrayList(ASSOCIATE_SALARY_COSTS, ASSOCIATE_DEVELOPMENT_COSTS, TRAVEL, CONSUMABLES, KNOWLEDGE_BASE, ESTATE_COSTS, ASSOCIATE_SUPPORT, SUBCONTRACTING_COSTS, OTHER_COSTS, ADDITIONAL_COMPANY_COSTS, FINANCE, OTHER_FUNDING, YOUR_FINANCE);
    }

    private List<FinanceRowType> loanFinanceTypes() {
        return newArrayList(LABOUR, OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS, GRANT_CLAIM_AMOUNT, OTHER_FUNDING, YOUR_FINANCE);
    }

    private List<FinanceRowType> procurementFinanceTypes() {
        return newArrayList(LABOUR, PROCUREMENT_OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS, FINANCE, OTHER_FUNDING, YOUR_FINANCE, VAT);
    }

    private List<FinanceRowType> defaultFinanceTypes() {
        return newArrayList(LABOUR, OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS, FINANCE, OTHER_FUNDING, YOUR_FINANCE);
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
