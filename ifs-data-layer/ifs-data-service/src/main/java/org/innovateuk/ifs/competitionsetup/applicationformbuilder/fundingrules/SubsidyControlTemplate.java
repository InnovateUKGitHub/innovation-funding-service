package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireRepository;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.MultipleChoiceOptionBuilder.aMultipleChoiceOption;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;

@Component
public class SubsidyControlTemplate implements FundingRulesTemplate {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionnaireQuestionService questionnaireQuestionService;

    @Autowired
    private QuestionnaireOptionService questionnaireOptionService;

    @Autowired
    private QuestionnaireTextOutcomeService textOutcomeService;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Value("${ifs.subsidy.control.northern.ireland.enabled}")
    private boolean northernIrelandSubsidyControlToggle;

    @Autowired
    private Environment environment;

    @Override
    public FundingRules type() {
        return FundingRules.SUBSIDY_CONTROL;
    }

    @Override
    public List<SectionBuilder> sections(Competition competition, List<SectionBuilder> competitionTypeSections) {

        if (competitionTypeSections.stream().noneMatch(section -> section.getType() == SectionType.FINANCES)) {
            return competitionTypeSections;
        }

        if (!northernIrelandSubsidyControlToggle || generatingWebtestDataForComp(competition)) {
            insertNorthernIrelandTacticalDeclaration(competitionTypeSections);
        } else if (northernIrelandSubsidyControlToggle) {
            competitionTypeSections.get(0)
                    .getQuestions().add(0,
                    aQuestion()
                            .withShortName("Subsidy basis")
                            .withName("Subsidy basis")
                            .withDescription("Subsidy basis")
                            .withMarkAsCompletedEnabled(true)
                            .withMultipleStatuses(true)
                            .withAssignEnabled(false)
                            .withQuestionSetupType(QuestionSetupType.SUBSIDY_BASIS)
                            .withQuestionnaire(northernIrelandDeclaration()));
        }
        return competitionTypeSections;
    }

    private boolean generatingWebtestDataForComp(Competition competition) {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> IfsProfileConstants.INTEGRATION_TEST.equals(profile))
                && competition.getName().contains("Subsidy control tactical");
    }

    private static void insertNorthernIrelandTacticalDeclaration(List<SectionBuilder> sectionBuilders) {
        sectionBuilders.stream()
                .filter(section -> SectionType.PROJECT_DETAILS == section.getType())
                .findAny()
                .ifPresent(section -> section.getQuestions().add(0, northernIrelandTacticalDeclaration()));
    }

    private static QuestionBuilder northernIrelandTacticalDeclaration() {
        return aQuestion()
                .withShortName("Subsidy basis")
                .withName("Will the project, including any related activities you want Innovate UK to fund, affect trade between Northern Ireland and the EU?")
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(true)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.NORTHERN_IRELAND_DECLARATION)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION)
                                .withMultipleChoiceOptions(newArrayList(
                                        aMultipleChoiceOption()
                                                .withText("Yes"),
                                        aMultipleChoiceOption()
                                                .withText("No")
                                ))
                ));
    }

    private Questionnaire northernIrelandDeclaration() {

        QuestionnaireResource questionnaire = new QuestionnaireResource();
        questionnaire.setSecurityType(QuestionnaireSecurityType.LINK);
        questionnaire.setTitle("Subsidy basis");
        questionnaire.setDescription("<p>Use this section to:</p>" +
                "<ul class=\"list-bullet\">" +
                "<li>determine the subsidy basis for your organisation on this application</li>" +
                "<li>set the maximum funding level you can apply for</li>" +
                "<li>confirm your organisations terms and conditions</li>" +
                "</ul>");
        questionnaire = questionnaireService.create(questionnaire).getSuccess();

        // final
        QuestionnaireTextOutcomeResource northernIrelandProtocolOutcome = new QuestionnaireTextOutcomeResource();
        northernIrelandProtocolOutcome.setText(null);
        northernIrelandProtocolOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_TRUE);
        northernIrelandProtocolOutcome = textOutcomeService.create(northernIrelandProtocolOutcome).getSuccess();

        QuestionnaireTextOutcomeResource subsidyControlActOutcome = new QuestionnaireTextOutcomeResource();
        subsidyControlActOutcome.setText(null);
        subsidyControlActOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_FALSE);
        subsidyControlActOutcome = textOutcomeService.create(subsidyControlActOutcome).getSuccess();





        // ----------------------------------------------------------------------------------------------------------------------------------

        QuestionnaireQuestionResource question1 = new QuestionnaireQuestionResource();
        question1.setTitle("Subsidy basis");
        question1.setQuestion("1 - Will the Innovate UK funding directly or indirectly have an effect upon either:");
        question1.setQuestionnaire(questionnaire.getId());
        question1.setQuestionName("activities");
        question1 = questionnaireQuestionService.create(question1).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        QuestionnaireQuestionResource question1_5 = new QuestionnaireQuestionResource();
        question1_5.setTitle("Subsidy basis");
        question1_5.setQuestion("1.5 - Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?");
        question1_5.setQuestionnaire(questionnaire.getId());
        question1_5.setQuestionName("tradeQuestion");
        question1_5 = questionnaireQuestionService.create(question1_5).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        QuestionnaireQuestionResource question2 = new QuestionnaireQuestionResource();
        question2.setTitle("Subsidy basis");
        question2.setQuestion("2 - Is your enterprise based in or active in Northern Ireland?");
        question2.setQuestionnaire(questionnaire.getId());
        question2.setQuestionName("enterprisedBasedQuestion");
        question2 = questionnaireQuestionService.create(question2).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        QuestionnaireQuestionResource question3_J1 = new QuestionnaireQuestionResource();
        question3_J1.setTitle("Subsidy basis");
        question3_J1.setQuestion("3 J1 - Does your enterprise trade directly with customers in Northern Ireland?");
        question3_J1.setQuestionnaire(questionnaire.getId());
        question3_J1.setQuestionName("enterpriseTradeQuestion");
        question3_J1 = questionnaireQuestionService.create(question3_J1).getSuccess();

        QuestionnaireQuestionResource question3_J3 = new QuestionnaireQuestionResource();
        question3_J3.setTitle("Subsidy basis");
        question3_J3.setQuestion("3 J3 - Does your enterprise trade directly with customers in Northern Ireland?");
        question3_J3.setQuestionnaire(questionnaire.getId());
        question3_J3.setQuestionName("enterpriseTradeQuestion");
        question3_J3 = questionnaireQuestionService.create(question3_J3).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        QuestionnaireQuestionResource question4_J1 = new QuestionnaireQuestionResource();
        question4_J1.setTitle("Subsidy basis");
        question4_J1.setQuestion("4 J1 - Does your enterprise make goods or provide services to third parties with a view to:");
        question4_J1.setQuestionnaire(questionnaire.getId());
        question4_J1.setQuestionName("goodsAndServicesQuestion");
        question4_J1 = questionnaireQuestionService.create(question4_J1).getSuccess();

        QuestionnaireQuestionResource question4_J2 = new QuestionnaireQuestionResource();
        question4_J2.setTitle("Subsidy basis");
        question4_J2.setQuestion("4 J2 - Does your enterprise make goods or provide services to third parties with a view to:");
        question4_J2.setQuestionnaire(questionnaire.getId());
        question4_J2.setQuestionName("goodsAndServicesQuestion");
        question4_J2 = questionnaireQuestionService.create(question4_J2).getSuccess();

        QuestionnaireQuestionResource question4_J3 = new QuestionnaireQuestionResource();
        question4_J3.setTitle("Subsidy basis");
        question4_J3.setQuestion("4 J3 - Does your enterprise make goods or provide services to third parties with a view to:");
        question4_J3.setQuestionnaire(questionnaire.getId());
        question4_J3.setQuestionName("goodsAndServicesQuestion");
        question4_J3 = questionnaireQuestionService.create(question4_J3).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        QuestionnaireQuestionResource question5_J1 = new QuestionnaireQuestionResource();
        question5_J1.setTitle("Subsidy basis");
        question5_J1.setQuestion(
                " 5 J1 - Is your enterprise engaged in the production, processing or marketing " +
                        "of agricultural products; or active in the fisheries and aquaculture sector and involved in trade in such products with Northern Ireland?");
        question5_J1.setQuestionnaire(questionnaire.getId());
        question5_J1.setQuestionName("enterpriseEngagedQuestion");
        question5_J1 = questionnaireQuestionService.create(question5_J1).getSuccess();

        QuestionnaireQuestionResource question5_J2 = new QuestionnaireQuestionResource();
        question5_J2.setTitle("Subsidy basis");
        question5_J2.setQuestion(
                "5 J2 - Is your enterprise engaged in the production, processing or marketing " +
                        "of agricultural products; or active in the fisheries and aquaculture sector and involved in trade in such products with Northern Ireland?");
        question5_J2.setQuestionnaire(questionnaire.getId());
        question5_J2.setQuestionName("enterpriseEngagedQuestion");
        question5_J2 = questionnaireQuestionService.create(question5_J2).getSuccess();

        QuestionnaireQuestionResource question5_J3 = new QuestionnaireQuestionResource();
        question5_J3.setTitle("Subsidy basis");
        question5_J3.setQuestion(
                "5 J3 - Is your enterprise engaged in the production, processing or marketing " +
                        "of agricultural products; or active in the fisheries and aquaculture sector and involved in trade in such products with Northern Ireland?");
        question5_J3.setQuestionnaire(questionnaire.getId());
        question5_J3.setQuestionName("enterpriseEngagedQuestion");
        question5_J3 = questionnaireQuestionService.create(question5_J3).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        QuestionnaireQuestionResource question6_J1 = new QuestionnaireQuestionResource();
        question6_J1.setTitle("Subsidy basis");
        question6_J1.setQuestion(
                "6 J1 - Can you confirm that the Innovate UK funding will be directed towards " +
                        "activities other than the production, processing or marketing of agricultural products or the fisheries and aquaculture sector?");
        question6_J1.setQuestionnaire(questionnaire.getId());
        question6_J1.setQuestionName("fundingDirectedQuestion");
        question6_J1 = questionnaireQuestionService.create(question6_J1).getSuccess();

        // No question 6 journey 3

        QuestionnaireQuestionResource question6_J2 = new QuestionnaireQuestionResource();
        question6_J2.setTitle("Subsidy basis");
        question6_J2.setQuestion(
                "6 J2 - Can you confirm that the Innovate UK funding will be directed towards " +
                        "activities other than the production, processing or marketing of agricultural products or the fisheries and aquaculture sector?");
        question6_J2.setQuestionnaire(questionnaire.getId());
        question6_J2.setQuestionName("fundingDirectedQuestion");
        question6_J2 = questionnaireQuestionService.create(question6_J2).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        // OPTIONS

        // QUESTION 1

        QuestionnaireOptionResource question1Yes = new QuestionnaireOptionResource();
        question1Yes.setQuestion(question1.getId());
        question1Yes.setDecisionType(DecisionType.QUESTION);
        question1Yes.setDecision(question2.getId());
        question1Yes.setText("Yes");
        questionnaireOptionService.create(question1Yes).getSuccess();

        QuestionnaireOptionResource question2No = new QuestionnaireOptionResource();
        question2No.setQuestion(question1.getId());
        question2No.setDecisionType(DecisionType.QUESTION);
        question2No.setDecision(question1_5.getId());
        question2No.setText("No");
        questionnaireOptionService.create(question2No).getSuccess();

        // QUESTION 1.5

        QuestionnaireOptionResource question1_5Yes = new QuestionnaireOptionResource();
        question1_5Yes.setQuestion(question1_5.getId());
        question1_5Yes.setDecisionType(DecisionType.QUESTION);
        question1_5Yes.setDecision(question2.getId());
        question1_5Yes.setText("Yes");
        questionnaireOptionService.create(question1_5Yes).getSuccess();

        QuestionnaireOptionResource question1_5No = new QuestionnaireOptionResource();
        question1_5No.setQuestion(question1_5.getId());
        question1_5No.setDecisionType(DecisionType.TEXT_OUTCOME);
        question1_5No.setDecision(subsidyControlActOutcome.getId());
        question1_5No.setText("No");
        questionnaireOptionService.create(question1_5No).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        // QUESTION 2

        QuestionnaireOptionResource question2_J1_Yes = new QuestionnaireOptionResource();
        question2_J1_Yes.setQuestion(question2.getId());
        question2_J1_Yes.setDecisionType(DecisionType.QUESTION);
        question2_J1_Yes.setDecision(question3_J1.getId());
        question2_J1_Yes.setText("Yes");
        questionnaireOptionService.create(question2_J1_Yes).getSuccess();

        QuestionnaireOptionResource question2_J1_No = new QuestionnaireOptionResource();
        question2_J1_No.setQuestion(question2.getId());
        question2_J1_No.setDecisionType(DecisionType.QUESTION);
        question2_J1_No.setDecision(question3_J3.getId());
        question2_J1_No.setText("No");
        questionnaireOptionService.create(question2_J1_No).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        // QUESTION 3

        QuestionnaireOptionResource question3_J1_Yes = new QuestionnaireOptionResource();
        question3_J1_Yes.setQuestion(question3_J1.getId());
        question3_J1_Yes.setDecisionType(DecisionType.QUESTION);
        question3_J1_Yes.setDecision(question4_J1.getId());
        question3_J1_Yes.setText("Yes");
        questionnaireOptionService.create(question3_J1_Yes).getSuccess();

        QuestionnaireOptionResource question3_J1_No = new QuestionnaireOptionResource();
        question3_J1_No.setQuestion(question3_J1.getId());
        question3_J1_No.setDecisionType(DecisionType.QUESTION);
        question3_J1_No.setDecision(question4_J2.getId());
        question3_J1_No.setText("No");
        questionnaireOptionService.create(question3_J1_No).getSuccess();

        QuestionnaireOptionResource question3_J3_Yes = new QuestionnaireOptionResource();
        question3_J3_Yes.setQuestion(question3_J3.getId());
        question3_J3_Yes.setDecisionType(DecisionType.QUESTION);
        question3_J3_Yes.setDecision(question4_J2.getId());
        question3_J3_Yes.setText("Yes");
        questionnaireOptionService.create(question3_J3_Yes).getSuccess();

        QuestionnaireOptionResource question3_J3_No = new QuestionnaireOptionResource();
        question3_J3_No.setQuestion(question3_J3.getId());
        question3_J3_No.setDecisionType(DecisionType.QUESTION);
        question3_J3_No.setDecision(question4_J3.getId());
        question3_J3_No.setText("No");
        questionnaireOptionService.create(question3_J3_No).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        // QUESTION 4

        QuestionnaireOptionResource question4_J1_Yes = new QuestionnaireOptionResource();
        question4_J1_Yes.setQuestion(question4_J1.getId());
        question4_J1_Yes.setDecisionType(DecisionType.QUESTION);
        question4_J1_Yes.setDecision(question5_J1.getId());
        question4_J1_Yes.setText("Yes");
        questionnaireOptionService.create(question4_J1_Yes).getSuccess();

        QuestionnaireOptionResource question4_J1_No = new QuestionnaireOptionResource();
        question4_J1_No.setQuestion(question4_J1.getId());
        question4_J1_No.setDecisionType(DecisionType.QUESTION);
        question4_J1_No.setDecision(question5_J2.getId());
        question4_J1_No.setText("No");
        questionnaireOptionService.create(question4_J1_No).getSuccess();


        QuestionnaireOptionResource question4_J2_Yes = new QuestionnaireOptionResource();
        question4_J2_Yes.setQuestion(question4_J2.getId());
        question4_J2_Yes.setDecisionType(DecisionType.QUESTION);
        question4_J2_Yes.setDecision(question5_J2.getId());
        question4_J2_Yes.setText("Yes");
        questionnaireOptionService.create(question4_J2_Yes).getSuccess();

        QuestionnaireOptionResource question4_J2_No = new QuestionnaireOptionResource();
        question4_J2_No.setQuestion(question4_J2.getId());
        question4_J2_No.setDecisionType(DecisionType.QUESTION);
        question4_J2_No.setDecision(question5_J2.getId());
        question4_J2_No.setText("No");
        questionnaireOptionService.create(question4_J2_No).getSuccess();

        QuestionnaireOptionResource question4_J3_Yes = new QuestionnaireOptionResource();
        question4_J3_Yes.setQuestion(question4_J3.getId());
        question4_J3_Yes.setDecisionType(DecisionType.QUESTION);
        question4_J3_Yes.setDecision(question5_J2.getId());
        question4_J3_Yes.setText("Yes");
        questionnaireOptionService.create(question4_J3_Yes).getSuccess();

        QuestionnaireOptionResource question4_J3_No = new QuestionnaireOptionResource();
        question4_J3_No.setQuestion(question4_J3.getId());
        question4_J3_No.setDecisionType(DecisionType.QUESTION);
        question4_J3_No.setDecision(question5_J3.getId());
        question4_J3_No.setText("No");
        questionnaireOptionService.create(question4_J3_No).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        // QUESTION 5

        QuestionnaireOptionResource question5_J1_Yes = new QuestionnaireOptionResource();
        question5_J1_Yes.setQuestion(question5_J1.getId());
        question5_J1_Yes.setDecisionType(DecisionType.QUESTION);
        question5_J1_Yes.setDecision(question6_J1.getId());
        question5_J1_Yes.setText("Yes");
        questionnaireOptionService.create(question5_J1_Yes).getSuccess();

        QuestionnaireOptionResource question5_J1_No = new QuestionnaireOptionResource();
        question5_J1_No.setQuestion(question5_J1.getId());
        question5_J1_No.setDecisionType(DecisionType.TEXT_OUTCOME);
        question5_J1_No.setDecision(northernIrelandProtocolOutcome.getId());
        question5_J1_No.setText("No");
        questionnaireOptionService.create(question5_J1_No).getSuccess();


        QuestionnaireOptionResource question5_J2_Yes = new QuestionnaireOptionResource();
        question5_J2_Yes.setQuestion(question5_J2.getId());
        question5_J2_Yes.setDecisionType(DecisionType.QUESTION);
        question5_J2_Yes.setDecision(question6_J2.getId());
        question5_J2_Yes.setText("Yes");
        questionnaireOptionService.create(question5_J2_Yes).getSuccess();

        QuestionnaireOptionResource question5_J2_No = new QuestionnaireOptionResource();
        question5_J2_No.setQuestion(question5_J2.getId());
        question5_J2_No.setDecisionType(DecisionType.TEXT_OUTCOME);
        question5_J2_No.setDecision(northernIrelandProtocolOutcome.getId());
        question5_J2_No.setText("No");
        questionnaireOptionService.create(question5_J2_No).getSuccess();

        QuestionnaireOptionResource question5_J3_Yes = new QuestionnaireOptionResource();
        question5_J3_Yes.setQuestion(question5_J3.getId());
        question5_J3_Yes.setDecisionType(DecisionType.QUESTION);
        question5_J3_Yes.setDecision(question6_J2.getId());
        question5_J3_Yes.setText("Yes");
        questionnaireOptionService.create(question5_J3_Yes).getSuccess();

        QuestionnaireOptionResource question5_J3_No = new QuestionnaireOptionResource();
        question5_J3_No.setQuestion(question5_J3.getId());
        question5_J3_No.setDecisionType(DecisionType.TEXT_OUTCOME);
        question5_J3_No.setDecision(subsidyControlActOutcome.getId());
        question5_J3_No.setText("No");
        questionnaireOptionService.create(question5_J3_No).getSuccess();

        // ----------------------------------------------------------------------------------------------------------------------------------

        // QUESTION 6

        QuestionnaireOptionResource question6_J1_Yes = new QuestionnaireOptionResource();
        question6_J1_Yes.setQuestion(question6_J1.getId());
        question6_J1_Yes.setDecisionType(DecisionType.TEXT_OUTCOME);
        question6_J1_Yes.setDecision(northernIrelandProtocolOutcome.getId());
        question6_J1_Yes.setText("Yes");
        questionnaireOptionService.create(question6_J1_Yes).getSuccess();

        QuestionnaireOptionResource question6_J1_No = new QuestionnaireOptionResource();
        question6_J1_No.setQuestion(question6_J1.getId());
        question6_J1_No.setDecisionType(DecisionType.TEXT_OUTCOME);
        question6_J1_No.setDecision(northernIrelandProtocolOutcome.getId());
        question6_J1_No.setText("No");
        questionnaireOptionService.create(question6_J1_No).getSuccess();

        QuestionnaireOptionResource question6_J2_Yes = new QuestionnaireOptionResource();
        question6_J2_Yes.setQuestion(question6_J2.getId());
        question6_J2_Yes.setDecisionType(DecisionType.TEXT_OUTCOME);
        question6_J2_Yes.setDecision(northernIrelandProtocolOutcome.getId());
        question6_J2_Yes.setText("Yes");
        questionnaireOptionService.create(question6_J2_Yes).getSuccess();

        QuestionnaireOptionResource question6_J2_No = new QuestionnaireOptionResource();
        question6_J2_No.setQuestion(question6_J2.getId());
        question6_J2_No.setDecisionType(DecisionType.TEXT_OUTCOME);
        question6_J2_No.setDecision(northernIrelandProtocolOutcome.getId());
        question6_J2_No.setText("No");
        questionnaireOptionService.create(question6_J2_No).getSuccess();

        return questionnaireRepository.findById(questionnaire.getId()).get();
    }
}