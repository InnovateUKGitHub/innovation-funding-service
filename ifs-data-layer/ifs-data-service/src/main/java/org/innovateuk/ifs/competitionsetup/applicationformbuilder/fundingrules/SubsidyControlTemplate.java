package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireRepository;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Override
    public FundingRules type() {
        return FundingRules.SUBSIDY_CONTROL;
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {

        competitionTypeSections.get(0)
                .getQuestions().add(
                        aQuestion()
                            .withShortName("Subsidy basis")
                            .withName("Subsidy basis")
                            .withDescription("Subsidy basis")
                            .withMarkAsCompletedEnabled(true)
                            .withMultipleStatuses(true)
                            .withAssignEnabled(false)
                            .withQuestionSetupType(QuestionSetupType.SUBSIDY_BASIS)
                            .withQuestionnaire(northernIrelandDeclaration()));
        return competitionTypeSections;
    }

    private Questionnaire northernIrelandDeclaration() {
        QuestionnaireResource questionnaire = new QuestionnaireResource();
        questionnaire.setSecurityType(QuestionnaireSecurityType.LINK);
        questionnaire = questionnaireService.create(questionnaire).getSuccess();

        QuestionnaireQuestionResource activitiesQuestion = new QuestionnaireQuestionResource();
        activitiesQuestion.setTitle("Activities");
        activitiesQuestion.setQuestion("Will the activities that you want Innovate UK to support, have a direct link to Northern Ireland?");
        activitiesQuestion.setGuidance("For example, if the project or related activities are undertaken in Northern Ireland that would be a 'Yes' etc");
        activitiesQuestion.setQuestionnaire(questionnaire.getId());
        activitiesQuestion = questionnaireQuestionService.create(activitiesQuestion).getSuccess();

        QuestionnaireQuestionResource tradeQuestion = new QuestionnaireQuestionResource();
        tradeQuestion.setTitle("Trade");
        tradeQuestion.setQuestion("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?");
        tradeQuestion.setQuestionnaire(questionnaire.getId());
        tradeQuestion = questionnaireQuestionService.create(tradeQuestion).getSuccess();

        QuestionnaireTextOutcomeResource activitiesStateAidOutcome = new QuestionnaireTextOutcomeResource();
        activitiesStateAidOutcome.setText(null);
        activitiesStateAidOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_TRUE);
        activitiesStateAidOutcome = textOutcomeService.create(activitiesStateAidOutcome).getSuccess();
        QuestionnaireTextOutcomeResource tradeStateAidOutcome = new QuestionnaireTextOutcomeResource();
        tradeStateAidOutcome.setText(null);
        tradeStateAidOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_TRUE);
        tradeStateAidOutcome = textOutcomeService.create(tradeStateAidOutcome).getSuccess();
        QuestionnaireTextOutcomeResource tradeSubsidyControlOutcome = new QuestionnaireTextOutcomeResource();
        tradeSubsidyControlOutcome.setText(null);
        tradeSubsidyControlOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_FALSE);
        tradeSubsidyControlOutcome = textOutcomeService.create(tradeSubsidyControlOutcome).getSuccess();

        QuestionnaireOptionResource activitiesYes = new QuestionnaireOptionResource();
        activitiesYes.setQuestion(activitiesQuestion.getId());
        activitiesYes.setDecisionType(DecisionType.TEXT_OUTCOME);
        activitiesYes.setDecision(activitiesStateAidOutcome.getId());
        activitiesYes.setText("Yes");
        activitiesYes = questionnaireOptionService.create(activitiesYes).getSuccess();

        QuestionnaireOptionResource activitiesNo = new QuestionnaireOptionResource();
        activitiesNo.setQuestion(activitiesQuestion.getId());
        activitiesNo.setDecisionType(DecisionType.QUESTION);
        activitiesNo.setDecision(tradeQuestion.getId());
        activitiesNo.setText("No");
        activitiesNo = questionnaireOptionService.create(activitiesNo).getSuccess();

        QuestionnaireOptionResource tradeYes = new QuestionnaireOptionResource();
        tradeYes.setQuestion(tradeQuestion.getId());
        tradeYes.setDecisionType(DecisionType.TEXT_OUTCOME);
        tradeYes.setDecision(tradeStateAidOutcome.getId());
        tradeYes.setText("Yes");
        tradeYes = questionnaireOptionService.create(tradeYes).getSuccess();

        QuestionnaireOptionResource tradeNo = new QuestionnaireOptionResource();
        tradeNo.setQuestion(tradeQuestion.getId());
        tradeNo.setDecisionType(DecisionType.TEXT_OUTCOME);
        tradeNo.setDecision(tradeSubsidyControlOutcome.getId());
        tradeNo.setText("No");
        tradeNo = questionnaireOptionService.create(tradeNo).getSuccess();

        return questionnaireRepository.findById(questionnaire.getId()).get();
    }

}
