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
        QuestionnaireTextOutcomeResource activitiesStateAidOutcome = new QuestionnaireTextOutcomeResource();
        activitiesStateAidOutcome.setText(null);
        activitiesStateAidOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_TRUE);
        activitiesStateAidOutcome = textOutcomeService.create(activitiesStateAidOutcome).getSuccess();

        // question 1
        QuestionnaireQuestionResource activitiesQuestion = new QuestionnaireQuestionResource();
        activitiesQuestion.setTitle("Subsidy basis");
        activitiesQuestion.setQuestion("Will the Innovate UK funding directly or indirectly have an effect upon either:");
        // TODO: Add the full question content.
        activitiesQuestion.setQuestionnaire(questionnaire.getId());
        activitiesQuestion.setQuestionName("activities");
        activitiesQuestion = questionnaireQuestionService.create(activitiesQuestion).getSuccess();

        // question 1.5 - no to first question
        QuestionnaireQuestionResource tradeQuestion = new QuestionnaireQuestionResource();
        tradeQuestion.setTitle("Subsidy basis");
        tradeQuestion.setQuestion("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?");
        tradeQuestion.setQuestionnaire(questionnaire.getId());
        tradeQuestion.setQuestionName("tradeQuestion");
        tradeQuestion = questionnaireQuestionService.create(tradeQuestion).getSuccess();

        // question 2
        QuestionnaireQuestionResource enterprisedBasedQuestion = new QuestionnaireQuestionResource();
        enterprisedBasedQuestion.setTitle("Subsidy basis");
        enterprisedBasedQuestion.setQuestion("Is your enterprise based in or active in Northern Ireland?");
        enterprisedBasedQuestion.setQuestionnaire(questionnaire.getId());
        enterprisedBasedQuestion.setQuestionName("enterprisedBasedQuestion");
        enterprisedBasedQuestion = questionnaireQuestionService.create(enterprisedBasedQuestion).getSuccess();

        // question 3
        QuestionnaireQuestionResource enterpriseTradeQuestion = new QuestionnaireQuestionResource();
        enterpriseTradeQuestion.setTitle("Subsidy basis");
        enterpriseTradeQuestion.setQuestion("Does your enterprise trade directly with customers in Northern Ireland?");
        enterpriseTradeQuestion.setQuestionnaire(questionnaire.getId());
        enterpriseTradeQuestion.setQuestionName("enterpriseTradeQuestion");
        enterpriseTradeQuestion = questionnaireQuestionService.create(enterpriseTradeQuestion).getSuccess();

        // question 4
        QuestionnaireQuestionResource goodsAndServicesQuestion = new QuestionnaireQuestionResource();
        goodsAndServicesQuestion.setTitle("Subsidy basis");
        goodsAndServicesQuestion.setQuestion("Does your enterprise make goods or provide services to third parties with a view to:");
        // TODO: Add the full question content.
        goodsAndServicesQuestion.setQuestionnaire(questionnaire.getId());
        goodsAndServicesQuestion.setQuestionName("goodsAndServicesQuestion");
        goodsAndServicesQuestion = questionnaireQuestionService.create(goodsAndServicesQuestion).getSuccess();

        // question 5
        QuestionnaireQuestionResource enterpriseEngagedQuestion = new QuestionnaireQuestionResource();
        enterpriseEngagedQuestion.setTitle("Subsidy basis");
        enterpriseEngagedQuestion.setQuestion(
                "Is your enterprise engaged in the production, processing or marketing " +
                        "of agricultural products; or active in the fisheries and aquaculture sector and involved in trade in such products with Northern Ireland?");
        enterpriseEngagedQuestion.setQuestionnaire(questionnaire.getId());
        enterpriseEngagedQuestion.setQuestionName("enterpriseEngagedQuestion");
        enterpriseEngagedQuestion = questionnaireQuestionService.create(enterpriseEngagedQuestion).getSuccess();

        // question 6
        QuestionnaireQuestionResource fundingDirectedQuestion = new QuestionnaireQuestionResource();
        fundingDirectedQuestion.setTitle("Subsidy basis");
        fundingDirectedQuestion.setQuestion(
                "Can you confirm that the Innovate UK funding will be directed towards " +
                        "activities other than the production, processing or marketing of agricultural products or the fisheries and aquaculture sector?");
        fundingDirectedQuestion.setQuestionnaire(questionnaire.getId());
        fundingDirectedQuestion.setQuestionName("fundingDirectedQuestion");
        fundingDirectedQuestion = questionnaireQuestionService.create(fundingDirectedQuestion).getSuccess();

        // question 1 - options
        QuestionnaireOptionResource activitiesYes = new QuestionnaireOptionResource();
        activitiesYes.setQuestion(activitiesQuestion.getId());
        activitiesYes.setDecisionType(DecisionType.QUESTION);
        activitiesYes.setDecision(enterprisedBasedQuestion.getId());
        activitiesYes.setText("Yes");
        questionnaireOptionService.create(activitiesYes).getSuccess();

        QuestionnaireOptionResource activitiesNo = new QuestionnaireOptionResource();
        activitiesNo.setQuestion(activitiesQuestion.getId());
        activitiesNo.setDecisionType(DecisionType.QUESTION);
        activitiesNo.setDecision(tradeQuestion.getId());
        activitiesNo.setText("No");
        questionnaireOptionService.create(activitiesNo).getSuccess();

        // question 1.5 - options
        QuestionnaireOptionResource tradeYes = new QuestionnaireOptionResource();
        tradeYes.setQuestion(tradeQuestion.getId());
        tradeYes.setDecisionType(DecisionType.QUESTION);
        tradeYes.setDecision(enterprisedBasedQuestion.getId());
        tradeYes.setText("Yes");
        questionnaireOptionService.create(tradeYes).getSuccess();

        QuestionnaireOptionResource tradeNo = new QuestionnaireOptionResource();
        tradeNo.setQuestion(tradeQuestion.getId());
        tradeNo.setDecisionType(DecisionType.TEXT_OUTCOME);
        tradeNo.setDecision(activitiesStateAidOutcome.getId());
        tradeNo.setText("No");
        questionnaireOptionService.create(tradeNo).getSuccess();

        // question 2 - options
        QuestionnaireOptionResource enterprisedBasedYes = new QuestionnaireOptionResource();
        enterprisedBasedYes.setQuestion(enterprisedBasedQuestion.getId());
        enterprisedBasedYes.setDecisionType(DecisionType.QUESTION);
        enterprisedBasedYes.setDecision(enterpriseTradeQuestion.getId());
        enterprisedBasedYes.setText("Yes");
        questionnaireOptionService.create(enterprisedBasedYes).getSuccess();

        QuestionnaireOptionResource enterprisedBasedNo = new QuestionnaireOptionResource();
        enterprisedBasedNo.setQuestion(enterprisedBasedQuestion.getId());
        enterprisedBasedNo.setDecisionType(DecisionType.QUESTION);
        enterprisedBasedNo.setDecision(enterpriseTradeQuestion.getId());
        enterprisedBasedNo.setText("No");
        questionnaireOptionService.create(enterprisedBasedNo).getSuccess();

        // question 3 - options
        QuestionnaireOptionResource enterpriseTradeYes = new QuestionnaireOptionResource();
        enterpriseTradeYes.setQuestion(enterpriseTradeQuestion.getId());
        enterpriseTradeYes.setDecisionType(DecisionType.QUESTION);
        enterpriseTradeYes.setDecision(goodsAndServicesQuestion.getId());
        enterpriseTradeYes.setText("Yes");
        questionnaireOptionService.create(enterpriseTradeYes).getSuccess();

        QuestionnaireOptionResource enterpriseTradeNo = new QuestionnaireOptionResource();
        enterpriseTradeNo.setQuestion(enterpriseTradeQuestion.getId());
        enterpriseTradeNo.setDecisionType(DecisionType.QUESTION);
        enterpriseTradeNo.setDecision(goodsAndServicesQuestion.getId());
        enterpriseTradeNo.setText("No");
        questionnaireOptionService.create(enterpriseTradeNo).getSuccess();

        // question 4 - options
        QuestionnaireOptionResource goodsAndServicesYes = new QuestionnaireOptionResource();
        goodsAndServicesYes.setQuestion(goodsAndServicesQuestion.getId());
        goodsAndServicesYes.setDecisionType(DecisionType.QUESTION);
        goodsAndServicesYes.setDecision(enterpriseEngagedQuestion.getId());
        goodsAndServicesYes.setText("Yes");
        questionnaireOptionService.create(goodsAndServicesYes).getSuccess();

        QuestionnaireOptionResource goodsAndServicesNo = new QuestionnaireOptionResource();
        goodsAndServicesNo.setQuestion(goodsAndServicesQuestion.getId());
        goodsAndServicesNo.setDecisionType(DecisionType.QUESTION);
        goodsAndServicesNo.setDecision(enterpriseEngagedQuestion.getId());
        goodsAndServicesNo.setText("No");
        questionnaireOptionService.create(goodsAndServicesNo).getSuccess();

        // question 5 - options
        QuestionnaireOptionResource enterpriseEngagedYes = new QuestionnaireOptionResource();
        enterpriseEngagedYes.setQuestion(enterpriseEngagedQuestion.getId());
        enterpriseEngagedYes.setDecisionType(DecisionType.QUESTION);
        enterpriseEngagedYes.setDecision(fundingDirectedQuestion.getId());
        enterpriseEngagedYes.setText("Yes");
        questionnaireOptionService.create(enterpriseEngagedYes).getSuccess();

        QuestionnaireOptionResource enterpriseEngagedNo = new QuestionnaireOptionResource();
        enterpriseEngagedNo.setQuestion(enterpriseEngagedQuestion.getId());
        enterpriseEngagedNo.setDecisionType(DecisionType.TEXT_OUTCOME);
        enterpriseEngagedNo.setDecision(activitiesStateAidOutcome.getId());
        enterpriseEngagedNo.setText("No");
        questionnaireOptionService.create(enterpriseEngagedNo).getSuccess();

        // question 6 - options
        QuestionnaireOptionResource fundingDirectedYes = new QuestionnaireOptionResource();
        fundingDirectedYes.setQuestion(fundingDirectedQuestion.getId());
        fundingDirectedYes.setDecisionType(DecisionType.TEXT_OUTCOME);
        fundingDirectedYes.setDecision(activitiesStateAidOutcome.getId());
        fundingDirectedYes.setText("Yes");
        questionnaireOptionService.create(fundingDirectedYes).getSuccess();

        QuestionnaireOptionResource fundingDirectedNo = new QuestionnaireOptionResource();
        fundingDirectedNo.setQuestion(fundingDirectedQuestion.getId());
        fundingDirectedNo.setDecisionType(DecisionType.TEXT_OUTCOME);
        fundingDirectedNo.setDecision(activitiesStateAidOutcome.getId());
        fundingDirectedNo.setText("No");
        questionnaireOptionService.create(fundingDirectedNo).getSuccess();

        return questionnaireRepository.findById(questionnaire.getId()).get();
    }
}