package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import static org.innovateuk.ifs.question.resource.QuestionSetupType.TERMS_AND_CONDITIONS;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION;

@Component
public class LoanTemplate implements FundingTypeTemplate {

    @Autowired
    private CommonBuilders commonBuilders;

    @Value("${ifs.loan.survey.question.enabled}")
    private boolean ifsLoanSurveyQuestionEnabled;

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
                .ifPresent(projectDetailsSection -> {
                    projectDetailsSection.withName("Applicant details");
                    setEdiQuestionDescription(projectDetailsSection);
                    addLoanBusinessAndFinanceInformationQuestion(projectDetailsSection);
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
                        section.withName("Project finance")
                );
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
        if (ifsLoanSurveyQuestionEnabled){
            projectDetailSection.getQuestions().add(0, loanBusinessAndFinancialInformation());
        }
    }

    private static QuestionBuilder loanBusinessAndFinancialInformation() {
        return aQuestion()
                .withQuestionSetupType(LOAN_BUSINESS_AND_FINANCIAL_INFORMATION)
                .withShortName("Business and financial information")
                .withName("Have you completed the business information, including uploading your financial submission?")
                .withMarkAsCompletedEnabled(true)
                .withDescription(
                        "<p><strong>Business &amp; financial details</strong></p>" +
                        "<p>For us to consider the suitability of your business for a loan, we need detailed information about your business as well as financial information and forecasts</p>" +
                        "<p><strong>Business information</strong></p>" +
                        "<p>You must submit information to us about your business. This is done through answering questions in the format of an online survey.</a></p>"
                )
                .withDescription2(
                        "<p><strong>Financial information</strong></p>" +
                        "<p>You must submit financial information to us about your business. This is done through completion of a spreadsheet template. Please download the financial spreadsheet template and fill this in offline. At the end of the survey you will be asked to upload the financial spreadsheet.</p>"
                        )
                .withAssignEnabled(true)
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
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false)
                        ));

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
