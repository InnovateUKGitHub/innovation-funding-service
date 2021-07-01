package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

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
        return Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> "integration-test".equals(profile))
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

        QuestionnaireQuestionResource activitiesQuestion = new QuestionnaireQuestionResource();
        activitiesQuestion.setTitle("Subsidy basis");
        activitiesQuestion.setQuestion("Will the activities that you want Innovate UK to support, have a direct link to Northern Ireland?");
        activitiesQuestion.setGuidance("For example, if the project or related activities are undertaken in Northern Ireland that would be a 'Yes' etc");
        activitiesQuestion.setQuestionnaire(questionnaire.getId());
        activitiesQuestion = questionnaireQuestionService.create(activitiesQuestion).getSuccess();

        QuestionnaireQuestionResource tradeQuestion = new QuestionnaireQuestionResource();
        tradeQuestion.setTitle("Subsidy basis");
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
        questionnaireOptionService.create(activitiesYes).getSuccess();

        QuestionnaireOptionResource activitiesNo = new QuestionnaireOptionResource();
        activitiesNo.setQuestion(activitiesQuestion.getId());
        activitiesNo.setDecisionType(DecisionType.QUESTION);
        activitiesNo.setDecision(tradeQuestion.getId());
        activitiesNo.setText("No");
        questionnaireOptionService.create(activitiesNo).getSuccess();

        QuestionnaireOptionResource tradeYes = new QuestionnaireOptionResource();
        tradeYes.setQuestion(tradeQuestion.getId());
        tradeYes.setDecisionType(DecisionType.TEXT_OUTCOME);
        tradeYes.setDecision(tradeStateAidOutcome.getId());
        tradeYes.setText("Yes");
        questionnaireOptionService.create(tradeYes).getSuccess();

        QuestionnaireOptionResource tradeNo = new QuestionnaireOptionResource();
        tradeNo.setQuestion(tradeQuestion.getId());
        tradeNo.setDecisionType(DecisionType.TEXT_OUTCOME);
        tradeNo.setDecision(tradeSubsidyControlOutcome.getId());
        tradeNo.setText("No");
        questionnaireOptionService.create(tradeNo).getSuccess();

        return questionnaireRepository.findById(questionnaire.getId()).get();
    }

}
