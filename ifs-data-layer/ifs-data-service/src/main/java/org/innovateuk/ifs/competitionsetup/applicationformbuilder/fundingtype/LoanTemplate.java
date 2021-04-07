package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.EDI_QUESTION_PATTERN;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.EQUALITY_DIVERSITY_INCLUSION;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.TERMS_AND_CONDITIONS;

@Component
public class LoanTemplate implements FundingTypeTemplate {

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public FundingType type() {
        return FundingType.LOAN;
    }

    @Override
    public Competition initialiseFinanceTypes(Competition competition) {
        List<FinanceRowType> types = newArrayList(LABOUR, OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS, GRANT_CLAIM_AMOUNT, OTHER_FUNDING);
        return commonBuilders.saveFinanceRows(competition, types);
    }

    @Override
    public Competition initialiseProjectSetupColumns(Competition competition) {
        addLoanProjectSetupColumns(competition);
        return competition;
    }

    @Override
    public Competition overrideTermsAndConditions(Competition competition) {
        return commonBuilders.overrideTermsAndConditions(competition);
    }

    @Override
    public Competition setGolTemplate(Competition competition) {
        return commonBuilders.getGolTemplate(competition);
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {
        competitionTypeSections.stream().filter(section -> SectionType.PROJECT_DETAILS == section.getType())
                .findAny()
                .ifPresent(section -> {
                        section.withName("Applicant details");
                        section.getQuestions().stream().filter(question -> EQUALITY_DIVERSITY_INCLUSION.equals(question.getQuestionSetupType()))
                        .findAny()
                        .ifPresent(ediQuestion ->
                                ediQuestion.withDescription(String.format(EDI_QUESTION_PATTERN, "https://bit.ly/EDIForm"))
                        );
                });
        competitionTypeSections.stream().filter(section -> SectionType.TERMS_AND_CONDITIONS == section.getType())
                .findAny()
                .ifPresent(section ->
                        section.getQuestions().stream().filter(question -> TERMS_AND_CONDITIONS.equals(question.getQuestionSetupType()))
                                .findAny()
                                .ifPresent(termsQuestion ->
                                        termsQuestion.withName("Loan terms and conditions")
                                            .withDescription("Loan terms and conditions")
                                            .withShortName("Loan terms and conditions")
                                ));
        competitionTypeSections.stream().filter(section -> SectionType.FINANCES == section.getType())
                .findAny()
                .ifPresent(section ->
                    section.withName("Project Finance")
                );
        return competitionTypeSections;
    }

    private void addLoanProjectSetupColumns(Competition competition) {
        commonBuilders.addProjectSetupStage(competition, PROJECT_DETAILS);
        commonBuilders.addProjectSetupStage(competition, PROJECT_TEAM);
        commonBuilders.addProjectSetupStage(competition, MONITORING_OFFICER);
        commonBuilders.addProjectSetupStage(competition, FINANCE_CHECKS);
        commonBuilders.addProjectSetupStage(competition, SPEND_PROFILE);
        commonBuilders.addProjectSetupStage(competition, PROJECT_SETUP_COMPLETE);
    }
}
