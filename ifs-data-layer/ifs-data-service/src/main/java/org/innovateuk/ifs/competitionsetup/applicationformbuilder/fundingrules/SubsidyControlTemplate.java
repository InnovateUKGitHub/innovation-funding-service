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

    private static final String SUBSIDYBASIS = "Subsidy basis";
    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String QUESTION2_TITLE = "Is your enterprise based in or active in Northern Ireland?";
    private static final String QUESTION3_TITLE = "Does your enterprise trade directly with customers in Northern Ireland?";
    private static final String QUESTION3_GUIDANCE =
            "Note: if you have not made any sales or you do not intend to sell to Northern Ireland " +
                    "or you consider it possible to say that any effect of the Innovate UK funding upon: " + "\n" +
                    "(a) goods that will be traded between Northern Ireland and the EU " + "\n" +
                    "or \n" +
                    "(b) the single electricity market of Ireland, " + "\n" +
                    "will be merely \"hypothetical, presumed, or without a genuine and direct link to Northern Ireland\", " +
                    "then answer No to this question.";
    private static final String QUESTION4_TITLE =
            "Does your enterprise make goods or provide services to third parties with a view to:" + "\n" +
                    "(a) enabling them to manufacture goods that will be traded between Northern Ireland and the EU" + "\n" +
                    "or" + "\n" +
                    "(b) effect the single electricity market of Ireland";
    private static final String QUESTION4_GUIDANCE =
            "Note: this question seeks to understand if you are carrying out activities that may indirectly lead " +
                    "to effects on trade in goods to Northern Ireland or the single electricity market of Ireland. " +
                    "If you are not aware of any such impact answer No to this question.";
    private static final String QUESTION5_TITLE =
            "Is your enterprise engaged in the production, processing or marketing " +
                    "of agricultural products; or active in the fisheries and aquaculture sector and " +
                    "involved in trade in such products with Northern Ireland?";
    private static final String QUESTION6_TITLE =
            "Can you confirm that the Innovate UK funding will be directed towards " +
                    "activities other than the production, processing or marketing of agricultural products or " +
                    "the fisheries and aquaculture sector?";
    private static final String QUESTION6_GUIDANCE =
            "If your activities do come under these sectors, prior to receipt of the Innovate UK funding you must " +
                    "establish accounting segregations to ensure that the funding does not cross-subsidise " +
                    "any of those activities." +
                    "\n" +
                    "Note: You may be required to supply evidence of such accounting segregation for up to 10 years " +
                    "from the date the award is granted.";

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
                            .withShortName(SUBSIDYBASIS)
                            .withName(SUBSIDYBASIS)
                            .withDescription(SUBSIDYBASIS)
                            .withMarkAsCompletedEnabled(true)
                            .withMultipleStatuses(true)
                            .withAssignEnabled(false)
                            .withQuestionSetupType(QuestionSetupType.SUBSIDY_BASIS)
                            .withQuestionnaire(northernIrelandDeclaration()));
        }
        return competitionTypeSections;
    }

    private boolean generatingWebtestDataForComp(Competition competition) {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch(IfsProfileConstants.INTEGRATION_TEST::equals)
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
                .withShortName(SUBSIDYBASIS)
                .withName(
                        "Will the project, including any related activities you want Innovate UK to fund, " +
                        "affect trade between Northern Ireland and the EU?")
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
                                                .withText(YES),
                                        aMultipleChoiceOption()
                                                .withText(NO)
                                ))
                ));
    }

    private Questionnaire northernIrelandDeclaration() {

        /* ---------------------- LANDING PAGE --------------------------------------------------- */

        QuestionnaireResource questionnaire = new QuestionnaireResource();
        questionnaire.setSecurityType(QuestionnaireSecurityType.LINK);
        questionnaire.setTitle(SUBSIDYBASIS);
        questionnaire.setDescription(
                "<p>Use this section to:</p>" +
                "<ul class=\"list-bullet\">" +
                "<li>determine the subsidy basis for your organisation on this application</li>" +
                "<li>set the maximum funding level you can apply for</li>" +
                "<li>confirm your organisations terms and conditions</li>" +
                "</ul>");
        questionnaire = questionnaireService.create(questionnaire).getSuccess();

        /* ---------------------- OUTCOMES ------------------------------------------------------ */

        QuestionnaireTextOutcomeResource northernIrelandProtocolOutcome = new QuestionnaireTextOutcomeResource();
        northernIrelandProtocolOutcome.setText(null);
        northernIrelandProtocolOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_TRUE);
        northernIrelandProtocolOutcome = textOutcomeService.create(northernIrelandProtocolOutcome).getSuccess();

        QuestionnaireTextOutcomeResource subsidyControlActOutcome = new QuestionnaireTextOutcomeResource();
        subsidyControlActOutcome.setText(null);
        subsidyControlActOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_FALSE);
        subsidyControlActOutcome = textOutcomeService.create(subsidyControlActOutcome).getSuccess();

        /* ---------------------- QUESTION 1 --------------------------------------------------- */

        QuestionnaireQuestionResource fundingDirectlyOrIndirectlyQuestion = new QuestionnaireQuestionResource();
        fundingDirectlyOrIndirectlyQuestion.setTitle(SUBSIDYBASIS);
        fundingDirectlyOrIndirectlyQuestion.setQuestion(
                "Will the Innovate UK funding directly or indirectly have an effect upon either:" + "\n" +
                        "goods that will be traded between Northern Ireland and the EU" + "\n" +
                        "the single electricity market (of Ireland)?");
        fundingDirectlyOrIndirectlyQuestion.setQuestionnaire(questionnaire.getId());
        fundingDirectlyOrIndirectlyQuestion = questionnaireQuestionService.create(fundingDirectlyOrIndirectlyQuestion).getSuccess();

        /* ---------------------- QUESTION 1.5 ------------------------------------------------- */

        QuestionnaireQuestionResource intendingToTradeAnyGoodsQuestion = new QuestionnaireQuestionResource();
        intendingToTradeAnyGoodsQuestion.setTitle(SUBSIDYBASIS);
        intendingToTradeAnyGoodsQuestion.setQuestion(
                "Are you intending to trade any goods arising from the activities " +
                "funded by Innovate UK with the European Union through Northern Ireland?");
        intendingToTradeAnyGoodsQuestion.setQuestionnaire(questionnaire.getId());
        intendingToTradeAnyGoodsQuestion = questionnaireQuestionService.create(intendingToTradeAnyGoodsQuestion).getSuccess();

        /* ---------------------- QUESTION 2 --------------------------------------------------- */

        QuestionnaireQuestionResource enterpriseBasedInNorthernIrelandQuestion = new QuestionnaireQuestionResource();
        enterpriseBasedInNorthernIrelandQuestion.setTitle(SUBSIDYBASIS);
        enterpriseBasedInNorthernIrelandQuestion.setQuestion(QUESTION2_TITLE);
        enterpriseBasedInNorthernIrelandQuestion.setQuestionnaire(questionnaire.getId());
        enterpriseBasedInNorthernIrelandQuestion.setMessage(
                "The funding you are applying for is potentially \"in score\" of Article 10 " +
                        "of the Northern Ireland Protocol. Your answers to the following questions will confirm " +
                        "if your project is in scope of the protocol.");
        enterpriseBasedInNorthernIrelandQuestion = questionnaireQuestionService.create(enterpriseBasedInNorthernIrelandQuestion).getSuccess();

        /* ---------------------- QUESTION 3 --------------------------------------------------- */

        QuestionnaireQuestionResource enterpriseTradeDirectlyQuestion1 = new QuestionnaireQuestionResource();
        enterpriseTradeDirectlyQuestion1.setTitle(SUBSIDYBASIS);
        enterpriseTradeDirectlyQuestion1.setQuestion(QUESTION3_TITLE);
        enterpriseTradeDirectlyQuestion1.setQuestionnaire(questionnaire.getId());
        enterpriseTradeDirectlyQuestion1.setGuidance(QUESTION3_GUIDANCE);
        enterpriseTradeDirectlyQuestion1 = questionnaireQuestionService.create(enterpriseTradeDirectlyQuestion1).getSuccess();

        QuestionnaireQuestionResource enterpriseTradeDirectlyQuestion2 = new QuestionnaireQuestionResource();
        enterpriseTradeDirectlyQuestion2.setTitle(SUBSIDYBASIS);
        enterpriseTradeDirectlyQuestion2.setQuestion(QUESTION3_TITLE);
        enterpriseTradeDirectlyQuestion2.setQuestionnaire(questionnaire.getId());
        enterpriseTradeDirectlyQuestion2.setGuidance(QUESTION3_GUIDANCE);
        enterpriseTradeDirectlyQuestion2 = questionnaireQuestionService.create(enterpriseTradeDirectlyQuestion2).getSuccess();

        /* ---------------------- QUESTION 4 --------------------------------------------------- */

        QuestionnaireQuestionResource enterpriseMakeGoodsQuestion1 = new QuestionnaireQuestionResource();
        enterpriseMakeGoodsQuestion1.setTitle(SUBSIDYBASIS);
        enterpriseMakeGoodsQuestion1.setQuestion(QUESTION4_TITLE);
        enterpriseMakeGoodsQuestion1.setQuestionnaire(questionnaire.getId());
        enterpriseMakeGoodsQuestion1.setGuidance(QUESTION4_GUIDANCE);
        enterpriseMakeGoodsQuestion1 = questionnaireQuestionService.create(enterpriseMakeGoodsQuestion1).getSuccess();

        QuestionnaireQuestionResource enterpriseMakeGoodsQuestion2 = new QuestionnaireQuestionResource();
        enterpriseMakeGoodsQuestion2.setTitle(SUBSIDYBASIS);
        enterpriseMakeGoodsQuestion2.setQuestion(QUESTION4_TITLE);
        enterpriseMakeGoodsQuestion2.setQuestionnaire(questionnaire.getId());
        enterpriseMakeGoodsQuestion2.setGuidance(QUESTION4_GUIDANCE);
        enterpriseMakeGoodsQuestion2 = questionnaireQuestionService.create(enterpriseMakeGoodsQuestion2).getSuccess();

        QuestionnaireQuestionResource enterpriseMakeGoodsQuestion3 = new QuestionnaireQuestionResource();
        enterpriseMakeGoodsQuestion3.setTitle(SUBSIDYBASIS);
        enterpriseMakeGoodsQuestion3.setQuestion(QUESTION4_TITLE);
        enterpriseMakeGoodsQuestion3.setQuestionnaire(questionnaire.getId());
        enterpriseMakeGoodsQuestion3.setGuidance(QUESTION4_GUIDANCE);
        enterpriseMakeGoodsQuestion3 = questionnaireQuestionService.create(enterpriseMakeGoodsQuestion3).getSuccess();

        /* ---------------------- QUESTION 5 --------------------------------------------------- */

        QuestionnaireQuestionResource enterpriseEngagedQuestion1 = new QuestionnaireQuestionResource();
        enterpriseEngagedQuestion1.setTitle(SUBSIDYBASIS);
        enterpriseEngagedQuestion1.setQuestion(QUESTION5_TITLE);
        enterpriseEngagedQuestion1.setQuestionnaire(questionnaire.getId());
        enterpriseEngagedQuestion1 = questionnaireQuestionService.create(enterpriseEngagedQuestion1).getSuccess();

        QuestionnaireQuestionResource enterpriseEngagedQuestion2 = new QuestionnaireQuestionResource();
        enterpriseEngagedQuestion2.setTitle(SUBSIDYBASIS);
        enterpriseEngagedQuestion2.setQuestion(QUESTION5_TITLE);
        enterpriseEngagedQuestion2.setQuestionnaire(questionnaire.getId());
        enterpriseEngagedQuestion2 = questionnaireQuestionService.create(enterpriseEngagedQuestion2).getSuccess();

        QuestionnaireQuestionResource enterpriseEngagedQuestion3 = new QuestionnaireQuestionResource();
        enterpriseEngagedQuestion3.setTitle(SUBSIDYBASIS);
        enterpriseEngagedQuestion3.setQuestion(QUESTION5_TITLE);
        enterpriseEngagedQuestion3.setQuestionnaire(questionnaire.getId());
        enterpriseEngagedQuestion3 = questionnaireQuestionService.create(enterpriseEngagedQuestion3).getSuccess();

        /* ---------------------- QUESTION 6 --------------------------------------------------- */

        QuestionnaireQuestionResource confirmInnovateFundingQuestion1 = new QuestionnaireQuestionResource();
        confirmInnovateFundingQuestion1.setTitle(SUBSIDYBASIS);
        confirmInnovateFundingQuestion1.setQuestion(QUESTION6_TITLE);
        confirmInnovateFundingQuestion1.setQuestionnaire(questionnaire.getId());
        confirmInnovateFundingQuestion1.setGuidance(QUESTION6_GUIDANCE);
        confirmInnovateFundingQuestion1 = questionnaireQuestionService.create(confirmInnovateFundingQuestion1).getSuccess();

        QuestionnaireQuestionResource confirmInnovateFundingQuestion2 = new QuestionnaireQuestionResource();
        confirmInnovateFundingQuestion2.setTitle(SUBSIDYBASIS);
        confirmInnovateFundingQuestion2.setQuestion(QUESTION6_TITLE);
        confirmInnovateFundingQuestion2.setQuestionnaire(questionnaire.getId());
        confirmInnovateFundingQuestion2.setGuidance(QUESTION6_GUIDANCE);
        confirmInnovateFundingQuestion2 = questionnaireQuestionService.create(confirmInnovateFundingQuestion2).getSuccess();

        /* ------------------------------------------------------------------------------------ */

        /* ---------------------- QUESTION 1 - OPTIONS ---------------------------------------- */

        QuestionnaireOptionResource fundingDirectlyOrIndirectlyQuestionOptionYes = new QuestionnaireOptionResource();
        fundingDirectlyOrIndirectlyQuestionOptionYes.setQuestion(fundingDirectlyOrIndirectlyQuestion.getId());
        fundingDirectlyOrIndirectlyQuestionOptionYes.setDecisionType(DecisionType.QUESTION);
        fundingDirectlyOrIndirectlyQuestionOptionYes.setDecision(enterpriseBasedInNorthernIrelandQuestion.getId());
        fundingDirectlyOrIndirectlyQuestionOptionYes.setText(YES);
        questionnaireOptionService.create(fundingDirectlyOrIndirectlyQuestionOptionYes).getSuccess();

        QuestionnaireOptionResource fundingDirectlyOrIndirectlyQuestionOptionNo = new QuestionnaireOptionResource();
        fundingDirectlyOrIndirectlyQuestionOptionNo.setQuestion(fundingDirectlyOrIndirectlyQuestion.getId());
        fundingDirectlyOrIndirectlyQuestionOptionNo.setDecisionType(DecisionType.QUESTION);
        fundingDirectlyOrIndirectlyQuestionOptionNo.setDecision(intendingToTradeAnyGoodsQuestion.getId());
        fundingDirectlyOrIndirectlyQuestionOptionNo.setText(NO);
        questionnaireOptionService.create(fundingDirectlyOrIndirectlyQuestionOptionNo).getSuccess();

        /* ---------------------- QUESTION 1.5 - OPTIONS ------------------------------------- */

        QuestionnaireOptionResource intendingToTradeAnyGoodsQuestionOptionYes = new QuestionnaireOptionResource();
        intendingToTradeAnyGoodsQuestionOptionYes.setQuestion(intendingToTradeAnyGoodsQuestion.getId());
        intendingToTradeAnyGoodsQuestionOptionYes.setDecisionType(DecisionType.QUESTION);
        intendingToTradeAnyGoodsQuestionOptionYes.setDecision(enterpriseBasedInNorthernIrelandQuestion.getId());
        intendingToTradeAnyGoodsQuestionOptionYes.setText(YES);
        questionnaireOptionService.create(intendingToTradeAnyGoodsQuestionOptionYes).getSuccess();

        QuestionnaireOptionResource intendingToTradeAnyGoodsQuestionOptionNo = new QuestionnaireOptionResource();
        intendingToTradeAnyGoodsQuestionOptionNo.setQuestion(intendingToTradeAnyGoodsQuestion.getId());
        intendingToTradeAnyGoodsQuestionOptionNo.setDecisionType(DecisionType.TEXT_OUTCOME);
        intendingToTradeAnyGoodsQuestionOptionNo.setDecision(subsidyControlActOutcome.getId());
        intendingToTradeAnyGoodsQuestionOptionNo.setText(NO);
        questionnaireOptionService.create(intendingToTradeAnyGoodsQuestionOptionNo).getSuccess();

        /* ---------------------- QUESTION 2 - OPTIONS ---------------------------------------- */

        QuestionnaireOptionResource enterpriseBasedInNorthernIrelandQuestionOptionYes = new QuestionnaireOptionResource();
        enterpriseBasedInNorthernIrelandQuestionOptionYes.setQuestion(enterpriseBasedInNorthernIrelandQuestion.getId());
        enterpriseBasedInNorthernIrelandQuestionOptionYes.setDecisionType(DecisionType.QUESTION);
        enterpriseBasedInNorthernIrelandQuestionOptionYes.setDecision(enterpriseTradeDirectlyQuestion1.getId());
        enterpriseBasedInNorthernIrelandQuestionOptionYes.setText(YES);
        questionnaireOptionService.create(enterpriseBasedInNorthernIrelandQuestionOptionYes).getSuccess();

        QuestionnaireOptionResource enterpriseBasedInNorthernIrelandQuestionOptionNo = new QuestionnaireOptionResource();
        enterpriseBasedInNorthernIrelandQuestionOptionNo.setQuestion(enterpriseBasedInNorthernIrelandQuestion.getId());
        enterpriseBasedInNorthernIrelandQuestionOptionNo.setDecisionType(DecisionType.QUESTION);
        enterpriseBasedInNorthernIrelandQuestionOptionNo.setDecision(enterpriseTradeDirectlyQuestion2.getId());
        enterpriseBasedInNorthernIrelandQuestionOptionNo.setText(NO);
        questionnaireOptionService.create(enterpriseBasedInNorthernIrelandQuestionOptionNo).getSuccess();

        /* ---------------------- QUESTION 3 - OPTIONS ---------------------------------------- */

        QuestionnaireOptionResource enterpriseTradeDirectlyQuestionOptionYes1 = new QuestionnaireOptionResource();
        enterpriseTradeDirectlyQuestionOptionYes1.setQuestion(enterpriseTradeDirectlyQuestion1.getId());
        enterpriseTradeDirectlyQuestionOptionYes1.setDecisionType(DecisionType.QUESTION);
        enterpriseTradeDirectlyQuestionOptionYes1.setDecision(enterpriseMakeGoodsQuestion1.getId());
        enterpriseTradeDirectlyQuestionOptionYes1.setText(YES);
        questionnaireOptionService.create(enterpriseTradeDirectlyQuestionOptionYes1).getSuccess();

        QuestionnaireOptionResource enterpriseTradeDirectlyQuestionOptionNo1 = new QuestionnaireOptionResource();
        enterpriseTradeDirectlyQuestionOptionNo1.setQuestion(enterpriseTradeDirectlyQuestion1.getId());
        enterpriseTradeDirectlyQuestionOptionNo1.setDecisionType(DecisionType.QUESTION);
        enterpriseTradeDirectlyQuestionOptionNo1.setDecision(enterpriseMakeGoodsQuestion2.getId());
        enterpriseTradeDirectlyQuestionOptionNo1.setText(NO);
        questionnaireOptionService.create(enterpriseTradeDirectlyQuestionOptionNo1).getSuccess();

        QuestionnaireOptionResource enterpriseTradeDirectlyQuestionOptionYes2 = new QuestionnaireOptionResource();
        enterpriseTradeDirectlyQuestionOptionYes2.setQuestion(enterpriseTradeDirectlyQuestion2.getId());
        enterpriseTradeDirectlyQuestionOptionYes2.setDecisionType(DecisionType.QUESTION);
        enterpriseTradeDirectlyQuestionOptionYes2.setDecision(enterpriseMakeGoodsQuestion2.getId());
        enterpriseTradeDirectlyQuestionOptionYes2.setText(YES);
        questionnaireOptionService.create(enterpriseTradeDirectlyQuestionOptionYes2).getSuccess();

        QuestionnaireOptionResource enterpriseTradeDirectlyQuestionOptionNo2 = new QuestionnaireOptionResource();
        enterpriseTradeDirectlyQuestionOptionNo2.setQuestion(enterpriseTradeDirectlyQuestion2.getId());
        enterpriseTradeDirectlyQuestionOptionNo2.setDecisionType(DecisionType.QUESTION);
        enterpriseTradeDirectlyQuestionOptionNo2.setDecision(enterpriseMakeGoodsQuestion3.getId());
        enterpriseTradeDirectlyQuestionOptionNo2.setText(NO);
        questionnaireOptionService.create(enterpriseTradeDirectlyQuestionOptionNo2).getSuccess();

        /* ---------------------- QUESTION 4 - OPTIONS ---------------------------------------- */

        QuestionnaireOptionResource enterpriseMakeGoodsQuestionOptionYes1 = new QuestionnaireOptionResource();
        enterpriseMakeGoodsQuestionOptionYes1.setQuestion(enterpriseMakeGoodsQuestion1.getId());
        enterpriseMakeGoodsQuestionOptionYes1.setDecisionType(DecisionType.QUESTION);
        enterpriseMakeGoodsQuestionOptionYes1.setDecision(enterpriseEngagedQuestion1.getId());
        enterpriseMakeGoodsQuestionOptionYes1.setText(YES);
        questionnaireOptionService.create(enterpriseMakeGoodsQuestionOptionYes1).getSuccess();

        QuestionnaireOptionResource enterpriseMakeGoodsQuestionOptionNo1 = new QuestionnaireOptionResource();
        enterpriseMakeGoodsQuestionOptionNo1.setQuestion(enterpriseMakeGoodsQuestion1.getId());
        enterpriseMakeGoodsQuestionOptionNo1.setDecisionType(DecisionType.QUESTION);
        enterpriseMakeGoodsQuestionOptionNo1.setDecision(enterpriseEngagedQuestion2.getId());
        enterpriseMakeGoodsQuestionOptionNo1.setText(NO);
        questionnaireOptionService.create(enterpriseMakeGoodsQuestionOptionNo1).getSuccess();

        QuestionnaireOptionResource enterpriseMakeGoodsQuestionOptionYes2 = new QuestionnaireOptionResource();
        enterpriseMakeGoodsQuestionOptionYes2.setQuestion(enterpriseMakeGoodsQuestion2.getId());
        enterpriseMakeGoodsQuestionOptionYes2.setDecisionType(DecisionType.QUESTION);
        enterpriseMakeGoodsQuestionOptionYes2.setDecision(enterpriseEngagedQuestion2.getId());
        enterpriseMakeGoodsQuestionOptionYes2.setText(YES);
        questionnaireOptionService.create(enterpriseMakeGoodsQuestionOptionYes2).getSuccess();

        QuestionnaireOptionResource enterpriseMakeGoodsQuestionOptionNo2 = new QuestionnaireOptionResource();
        enterpriseMakeGoodsQuestionOptionNo2.setQuestion(enterpriseMakeGoodsQuestion2.getId());
        enterpriseMakeGoodsQuestionOptionNo2.setDecisionType(DecisionType.QUESTION);
        enterpriseMakeGoodsQuestionOptionNo2.setDecision(enterpriseEngagedQuestion2.getId());
        enterpriseMakeGoodsQuestionOptionNo2.setText(NO);
        questionnaireOptionService.create(enterpriseMakeGoodsQuestionOptionNo2).getSuccess();

        QuestionnaireOptionResource enterpriseMakeGoodsQuestionOptionYes3 = new QuestionnaireOptionResource();
        enterpriseMakeGoodsQuestionOptionYes3.setQuestion(enterpriseMakeGoodsQuestion3.getId());
        enterpriseMakeGoodsQuestionOptionYes3.setDecisionType(DecisionType.QUESTION);
        enterpriseMakeGoodsQuestionOptionYes3.setDecision(enterpriseEngagedQuestion2.getId());
        enterpriseMakeGoodsQuestionOptionYes3.setText(YES);
        questionnaireOptionService.create(enterpriseMakeGoodsQuestionOptionYes3).getSuccess();

        QuestionnaireOptionResource enterpriseMakeGoodsQuestionOptionNo3 = new QuestionnaireOptionResource();
        enterpriseMakeGoodsQuestionOptionNo3.setQuestion(enterpriseMakeGoodsQuestion3.getId());
        enterpriseMakeGoodsQuestionOptionNo3.setDecisionType(DecisionType.QUESTION);
        enterpriseMakeGoodsQuestionOptionNo3.setDecision(enterpriseEngagedQuestion3.getId());
        enterpriseMakeGoodsQuestionOptionNo3.setText(NO);
        questionnaireOptionService.create(enterpriseMakeGoodsQuestionOptionNo3).getSuccess();

        /* ---------------------- QUESTION 5 - OPTIONS ---------------------------------------- */

        QuestionnaireOptionResource enterpriseEngagedQuestionOptionYes1 = new QuestionnaireOptionResource();
        enterpriseEngagedQuestionOptionYes1.setQuestion(enterpriseEngagedQuestion1.getId());
        enterpriseEngagedQuestionOptionYes1.setDecisionType(DecisionType.QUESTION);
        enterpriseEngagedQuestionOptionYes1.setDecision(confirmInnovateFundingQuestion1.getId());
        enterpriseEngagedQuestionOptionYes1.setText(YES);
        questionnaireOptionService.create(enterpriseEngagedQuestionOptionYes1).getSuccess();

        QuestionnaireOptionResource enterpriseEngagedQuestionOptionNo1 = new QuestionnaireOptionResource();
        enterpriseEngagedQuestionOptionNo1.setQuestion(enterpriseEngagedQuestion1.getId());
        enterpriseEngagedQuestionOptionNo1.setDecisionType(DecisionType.TEXT_OUTCOME);
        enterpriseEngagedQuestionOptionNo1.setDecision(northernIrelandProtocolOutcome.getId());
        enterpriseEngagedQuestionOptionNo1.setText(NO);
        questionnaireOptionService.create(enterpriseEngagedQuestionOptionNo1).getSuccess();

        QuestionnaireOptionResource enterpriseEngagedQuestionOptionYes2 = new QuestionnaireOptionResource();
        enterpriseEngagedQuestionOptionYes2.setQuestion(enterpriseEngagedQuestion2.getId());
        enterpriseEngagedQuestionOptionYes2.setDecisionType(DecisionType.QUESTION);
        enterpriseEngagedQuestionOptionYes2.setDecision(confirmInnovateFundingQuestion2.getId());
        enterpriseEngagedQuestionOptionYes2.setText(YES);
        questionnaireOptionService.create(enterpriseEngagedQuestionOptionYes2).getSuccess();

        QuestionnaireOptionResource enterpriseEngagedQuestionOptionNo2 = new QuestionnaireOptionResource();
        enterpriseEngagedQuestionOptionNo2.setQuestion(enterpriseEngagedQuestion2.getId());
        enterpriseEngagedQuestionOptionNo2.setDecisionType(DecisionType.TEXT_OUTCOME);
        enterpriseEngagedQuestionOptionNo2.setDecision(northernIrelandProtocolOutcome.getId());
        enterpriseEngagedQuestionOptionNo2.setText(NO);
        questionnaireOptionService.create(enterpriseEngagedQuestionOptionNo2).getSuccess();

        QuestionnaireOptionResource enterpriseEngagedQuestionOptionYes3 = new QuestionnaireOptionResource();
        enterpriseEngagedQuestionOptionYes3.setQuestion(enterpriseEngagedQuestion3.getId());
        enterpriseEngagedQuestionOptionYes3.setDecisionType(DecisionType.QUESTION);
        enterpriseEngagedQuestionOptionYes3.setDecision(confirmInnovateFundingQuestion2.getId());
        enterpriseEngagedQuestionOptionYes3.setText(YES);
        questionnaireOptionService.create(enterpriseEngagedQuestionOptionYes3).getSuccess();

        QuestionnaireOptionResource enterpriseEngagedQuestionOptionNo3 = new QuestionnaireOptionResource();
        enterpriseEngagedQuestionOptionNo3.setQuestion(enterpriseEngagedQuestion3.getId());
        enterpriseEngagedQuestionOptionNo3.setDecisionType(DecisionType.TEXT_OUTCOME);
        enterpriseEngagedQuestionOptionNo3.setDecision(subsidyControlActOutcome.getId());
        enterpriseEngagedQuestionOptionNo3.setText(NO);
        questionnaireOptionService.create(enterpriseEngagedQuestionOptionNo3).getSuccess();

        /* ---------------------- QUESTION 6 - OPTIONS ---------------------------------------- */

        QuestionnaireOptionResource confirmInnovateFundingQuestionOptionYes1 = new QuestionnaireOptionResource();
        confirmInnovateFundingQuestionOptionYes1.setQuestion(confirmInnovateFundingQuestion1.getId());
        confirmInnovateFundingQuestionOptionYes1.setDecisionType(DecisionType.TEXT_OUTCOME);
        confirmInnovateFundingQuestionOptionYes1.setDecision(northernIrelandProtocolOutcome.getId());
        confirmInnovateFundingQuestionOptionYes1.setText(YES);
        questionnaireOptionService.create(confirmInnovateFundingQuestionOptionYes1).getSuccess();

        QuestionnaireOptionResource confirmInnovateFundingQuestionOptionNo1 = new QuestionnaireOptionResource();
        confirmInnovateFundingQuestionOptionNo1.setQuestion(confirmInnovateFundingQuestion1.getId());
        confirmInnovateFundingQuestionOptionNo1.setDecisionType(DecisionType.TEXT_OUTCOME);
        confirmInnovateFundingQuestionOptionNo1.setDecision(northernIrelandProtocolOutcome.getId());
        confirmInnovateFundingQuestionOptionNo1.setText(NO);
        questionnaireOptionService.create(confirmInnovateFundingQuestionOptionNo1).getSuccess();

        QuestionnaireOptionResource confirmInnovateFundingQuestionOptionYes2 = new QuestionnaireOptionResource();
        confirmInnovateFundingQuestionOptionYes2.setQuestion(confirmInnovateFundingQuestion2.getId());
        confirmInnovateFundingQuestionOptionYes2.setDecisionType(DecisionType.TEXT_OUTCOME);
        confirmInnovateFundingQuestionOptionYes2.setDecision(northernIrelandProtocolOutcome.getId());
        confirmInnovateFundingQuestionOptionYes2.setText(YES);
        questionnaireOptionService.create(confirmInnovateFundingQuestionOptionYes2).getSuccess();

        QuestionnaireOptionResource confirmInnovateFundingQuestionOptionNo2 = new QuestionnaireOptionResource();
        confirmInnovateFundingQuestionOptionNo2.setQuestion(confirmInnovateFundingQuestion2.getId());
        confirmInnovateFundingQuestionOptionNo2.setDecisionType(DecisionType.TEXT_OUTCOME);
        confirmInnovateFundingQuestionOptionNo2.setDecision(northernIrelandProtocolOutcome.getId());
        confirmInnovateFundingQuestionOptionNo2.setText(NO);
        questionnaireOptionService.create(confirmInnovateFundingQuestionOptionNo2).getSuccess();

        return questionnaireRepository.findById(questionnaire.getId()).get();
    }
}