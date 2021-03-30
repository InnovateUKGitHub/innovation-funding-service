package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.EDI_QUESTION_PATTERN;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.EQUALITY_DIVERSITY_INCLUSION;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION;

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
        competitionTypeSections.stream().filter(section -> "Project details".equals(section.getName()))
                .findAny()
                .ifPresent(projectDetailsSection -> {
                        setEdiQuestionDescription(projectDetailsSection);
                        addLoanBusinessAndFinanceInformationQuestion(projectDetailsSection);
                });
        return competitionTypeSections;
    }

    private void setEdiQuestionDescription(SectionBuilder projectDetailSection){
        projectDetailSection.getQuestions()
                .stream()
                .filter(question -> EQUALITY_DIVERSITY_INCLUSION.equals(question.getQuestionSetupType()))
                .findAny()
                .ifPresent(ediQuestion ->
                        ediQuestion.withDescription(String.format(EDI_QUESTION_PATTERN, "https://bit.ly/EDIForm")));
    }

    private void addLoanBusinessAndFinanceInformationQuestion(SectionBuilder projectDetailSection){
        projectDetailSection.getQuestions().add(0, loanBusinessAndFinancialInformation());
    }

    private static QuestionBuilder loanBusinessAndFinancialInformation() {
        return aQuestion()
                .withQuestionSetupType(LOAN_BUSINESS_AND_FINANCIAL_INFORMATION)
                .withShortName("Business and financial information")
                .withName("Have you completed the business information, including uploading your financial submission")
                .withDescription("First part of guidance")
                .withFormInputs(asList(aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(true)
                                .withWordCount(400),
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false)));
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
