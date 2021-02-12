package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.config.domain.*;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireRepository;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeService;
import org.innovateuk.ifs.questionnaire.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;

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

        competitionTypeSections.add(0,
         aSection()
                .withName("Initial Details")
                .withType(SectionType.INITIAL_DETAILS)
                .withQuestions(newArrayList(aQuestion()
                            .withShortName("Northern ireland declaration")
                            .withName("Northern ireland declaration")
                            .withDescription("Northern ireland declaration")
                            .withMarkAsCompletedEnabled(true)
                            .withMultipleStatuses(true)
                            .withAssignEnabled(false)
                            .withQuestionSetupType(QuestionSetupType.QUESTIONNAIRE)
                            .withQuestionnaire(questionnaire()),
                        aQuestion()
                            .withShortName("Dummy questionnaire")
                            .withName("Dummy questionnaire")
                            .withDescription("Dummy questionnaire")
                            .withMarkAsCompletedEnabled(true)
                            .withMultipleStatuses(true)
                            .withAssignEnabled(false)
                            .withQuestionSetupType(QuestionSetupType.QUESTIONNAIRE)
                            .withQuestionnaire(dummyQuestionnaire()))));
        return competitionTypeSections;
    }
    private Questionnaire dummyQuestionnaire() {
        QuestionnaireResource questionnaire = questionnaireService.create(new QuestionnaireResource()).getSuccess();

        QuestionnaireQuestionResource animalQuestion = new QuestionnaireQuestionResource();
        animalQuestion.setTitle("Animals");
        animalQuestion.setQuestion("What is your favourite animal?");
        animalQuestion.setQuestionnaire(questionnaire.getId());
        animalQuestion = questionnaireQuestionService.create(animalQuestion).getSuccess();

        QuestionnaireQuestionResource catQuestion = new QuestionnaireQuestionResource();
        catQuestion.setTitle("Cats");
        catQuestion.setQuestion("What is your favourite cat?");
        catQuestion.setQuestionnaire(questionnaire.getId());
        catQuestion = questionnaireQuestionService.create(catQuestion).getSuccess();

        QuestionnaireQuestionResource maineCoonQuestion = new QuestionnaireQuestionResource();
        maineCoonQuestion.setTitle("Rag coon");
        maineCoonQuestion.setQuestion("What is your favourite colour of maine coon?");
        maineCoonQuestion.setQuestionnaire(questionnaire.getId());
        maineCoonQuestion = questionnaireQuestionService.create(maineCoonQuestion).getSuccess();

        QuestionnaireQuestionResource ragdollQuestion = new QuestionnaireQuestionResource();
        ragdollQuestion.setTitle("Rag doll");
        ragdollQuestion.setQuestion("What is your favourite colour of rag doll?");
        ragdollQuestion.setQuestionnaire(questionnaire.getId());
        ragdollQuestion = questionnaireQuestionService.create(ragdollQuestion).getSuccess();

        QuestionnaireQuestionResource dogQuestion = new QuestionnaireQuestionResource();
        dogQuestion.setTitle("Dogs");
        dogQuestion.setQuestion("What is your favourite dog?");
        dogQuestion.setQuestionnaire(questionnaire.getId());
        dogQuestion = questionnaireQuestionService.create(dogQuestion).getSuccess();

        QuestionnaireQuestionResource labradorQuestion = new QuestionnaireQuestionResource();
        labradorQuestion.setTitle("Labrador");
        labradorQuestion.setQuestion("What is your favourite colour of labrador?");
        labradorQuestion.setQuestionnaire(questionnaire.getId());
        labradorQuestion = questionnaireQuestionService.create(labradorQuestion).getSuccess();

        QuestionnaireQuestionResource cockerSpaniel = new QuestionnaireQuestionResource();
        cockerSpaniel.setTitle("Cocker spaniel");
        cockerSpaniel.setQuestion("What is your favourite colour of cocker spaniel?");
        cockerSpaniel.setQuestionnaire(questionnaire.getId());
        cockerSpaniel = questionnaireQuestionService.create(cockerSpaniel).getSuccess();

        QuestionnaireQuestionResource birdQuestion = new QuestionnaireQuestionResource();
        birdQuestion.setTitle("Bird");
        birdQuestion.setQuestion("What is your favourite bird?");
        birdQuestion.setQuestionnaire(questionnaire.getId());
        birdQuestion = questionnaireQuestionService.create(birdQuestion).getSuccess();

        QuestionnaireQuestionResource canaryQuestion = new QuestionnaireQuestionResource();
        canaryQuestion.setTitle("Canary");
        canaryQuestion.setQuestion("What is your favourite colour of canary?");
        canaryQuestion.setQuestionnaire(questionnaire.getId());
        canaryQuestion = questionnaireQuestionService.create(canaryQuestion).getSuccess();

        QuestionnaireQuestionResource parrotQuestion = new QuestionnaireQuestionResource();
        parrotQuestion.setTitle("Parrot");
        parrotQuestion.setQuestion("What is your favourite colour of Parrot?");
        parrotQuestion.setQuestionnaire(questionnaire.getId());
        parrotQuestion = questionnaireQuestionService.create(parrotQuestion).getSuccess();


        QuestionnaireOptionResource animalCatOption = new QuestionnaireOptionResource();
        animalCatOption.setQuestion(animalQuestion.getId());
        animalCatOption.setDecisionType(DecisionType.QUESTION);
        animalCatOption.setDecision(catQuestion.getId());
        animalCatOption.setText("Cat");
        animalCatOption = questionnaireOptionService.create(animalCatOption).getSuccess();

        QuestionnaireOptionResource animalDogOption = new QuestionnaireOptionResource();
        animalDogOption.setQuestion(animalQuestion.getId());
        animalDogOption.setDecisionType(DecisionType.QUESTION);
        animalDogOption.setDecision(dogQuestion.getId());
        animalDogOption.setText("Dog");
        animalCatOption = questionnaireOptionService.create(animalCatOption).getSuccess();

        QuestionnaireOptionResource animalBirdOption = new QuestionnaireOptionResource();
        animalBirdOption.setQuestion(animalQuestion.getId());
        animalBirdOption.setDecisionType(DecisionType.QUESTION);
        animalBirdOption.setDecision(birdQuestion.getId());
        animalBirdOption.setText("Bird");
        animalCatOption = questionnaireOptionService.create(animalCatOption).getSuccess();

        QuestionnaireOptionResource catMaineCoonOption = new QuestionnaireOptionResource();
        catMaineCoonOption.setQuestion(catQuestion.getId());
        catMaineCoonOption.setDecisionType(DecisionType.QUESTION);
        catMaineCoonOption.setDecision(maineCoonQuestion.getId());
        catMaineCoonOption.setText("Main coon");
        catMaineCoonOption = questionnaireOptionService.create(catMaineCoonOption).getSuccess();

        QuestionnaireOptionResource catRagdollOption = new QuestionnaireOptionResource();
        catRagdollOption.setQuestion(catQuestion.getId());
        catRagdollOption.setDecisionType(DecisionType.QUESTION);
        catRagdollOption.setDecision(ragdollQuestion.getId());
        catRagdollOption.setText("Rag doll");
        catRagdollOption = questionnaireOptionService.create(catRagdollOption).getSuccess();

        QuestionnaireOptionResource dogLabOption = new QuestionnaireOptionResource();
        dogLabOption.setQuestion(dogQuestion.getId());
        dogLabOption.setDecisionType(DecisionType.QUESTION);
        dogLabOption.setDecision(labradorQuestion.getId());
        dogLabOption.setText("Lab");
        dogLabOption = questionnaireOptionService.create(dogLabOption).getSuccess();

        QuestionnaireOptionResource dogCokerOption = new QuestionnaireOptionResource();
        dogCokerOption.setQuestion(dogQuestion.getId());
        dogCokerOption.setDecisionType(DecisionType.QUESTION);
        dogCokerOption.setDecision(cockerSpaniel.getId());
        dogCokerOption.setText("Coker spaniel");
        dogCokerOption = questionnaireOptionService.create(dogCokerOption).getSuccess();

        QuestionnaireOptionResource birdCanaryOption = new QuestionnaireOptionResource();
        birdCanaryOption.setQuestion(birdQuestion.getId());
        birdCanaryOption.setDecisionType(DecisionType.QUESTION);
        birdCanaryOption.setDecision(canaryQuestion.getId());
        birdCanaryOption.setText("Canary");
        birdCanaryOption = questionnaireOptionService.create(birdCanaryOption).getSuccess();

        QuestionnaireOptionResource birdParotOption = new QuestionnaireOptionResource();
        birdParotOption.setQuestion(birdQuestion.getId());
        birdParotOption.setDecisionType(DecisionType.QUESTION);
        birdParotOption.setDecision(parrotQuestion.getId());
        birdParotOption.setText("Parrot");
        birdParotOption = questionnaireOptionService.create(birdParotOption).getSuccess();


        QuestionnaireTextOutcomeResource blueMaineCoon = new QuestionnaireTextOutcomeResource();
        blueMaineCoon.setText("You love blue Maine coons");
        blueMaineCoon = textOutcomeService.create(blueMaineCoon).getSuccess();
        QuestionnaireTextOutcomeResource greenMaineCoon = new QuestionnaireTextOutcomeResource();
        greenMaineCoon.setText("You love green Maine coons");
        greenMaineCoon = textOutcomeService.create(greenMaineCoon).getSuccess();
        QuestionnaireTextOutcomeResource redMaineCoon = new QuestionnaireTextOutcomeResource();
        redMaineCoon.setText("You love red Maine coons");
        redMaineCoon = textOutcomeService.create(redMaineCoon).getSuccess();

        QuestionnaireTextOutcomeResource blueRagDoll = new QuestionnaireTextOutcomeResource();
        blueRagDoll.setText("You love blue Rag Dolls");
        blueRagDoll = textOutcomeService.create(blueRagDoll).getSuccess();
        QuestionnaireTextOutcomeResource greenRagDoll = new QuestionnaireTextOutcomeResource();
        greenRagDoll.setText("You love green Rag Dolls");
        greenRagDoll = textOutcomeService.create(greenRagDoll).getSuccess();
        QuestionnaireTextOutcomeResource redRagDoll = new QuestionnaireTextOutcomeResource();
        redRagDoll.setText("You love red Rag Dolls");
        redRagDoll = textOutcomeService.create(redRagDoll).getSuccess();

        QuestionnaireTextOutcomeResource blueLab = new QuestionnaireTextOutcomeResource();
        blueLab.setText("You love blue Labs");
        blueLab = textOutcomeService.create(blueLab).getSuccess();
        QuestionnaireTextOutcomeResource greenLab = new QuestionnaireTextOutcomeResource();
        greenLab.setText("You love green Labs");
        greenLab = textOutcomeService.create(greenLab).getSuccess();
        QuestionnaireTextOutcomeResource redLab = new QuestionnaireTextOutcomeResource();
        redLab.setText("You love red Labs");
        redLab = textOutcomeService.create(redLab).getSuccess();

        QuestionnaireTextOutcomeResource blueCocker = new QuestionnaireTextOutcomeResource();
        blueCocker.setText("You love blue Cockers");
        blueCocker = textOutcomeService.create(blueCocker).getSuccess();
        QuestionnaireTextOutcomeResource greenCocker = new QuestionnaireTextOutcomeResource();
        greenCocker.setText("You love green Cockers");
        greenCocker = textOutcomeService.create(greenCocker).getSuccess();
        QuestionnaireTextOutcomeResource redCocker = new QuestionnaireTextOutcomeResource();
        redCocker.setText("You love red Cockers");
        redCocker = textOutcomeService.create(redCocker).getSuccess();

        QuestionnaireTextOutcomeResource blueCanary = new QuestionnaireTextOutcomeResource();
        blueCanary.setText("You love blue Canarys");
        blueCanary = textOutcomeService.create(blueCanary).getSuccess();
        QuestionnaireTextOutcomeResource greenCanary = new QuestionnaireTextOutcomeResource();
        greenCanary.setText("You love green Canarys");
        greenCanary = textOutcomeService.create(greenCanary).getSuccess();
        QuestionnaireTextOutcomeResource redCanary = new QuestionnaireTextOutcomeResource();
        redCanary.setText("You love red Canarys");
        redCanary = textOutcomeService.create(redCanary).getSuccess();

        QuestionnaireTextOutcomeResource blueParrot = new QuestionnaireTextOutcomeResource();
        blueParrot.setText("You love blue Parrots");
        blueParrot = textOutcomeService.create(blueParrot).getSuccess();
        QuestionnaireTextOutcomeResource greenParrot = new QuestionnaireTextOutcomeResource();
        greenParrot.setText("You love green Parrots");
        greenParrot = textOutcomeService.create(greenParrot).getSuccess();
        QuestionnaireTextOutcomeResource redParrot = new QuestionnaireTextOutcomeResource();
        redParrot.setText("You love red Parrots");
        redParrot = textOutcomeService.create(redParrot).getSuccess();


        QuestionnaireOptionResource aMaineCoonBlueOption = new QuestionnaireOptionResource();
        aMaineCoonBlueOption.setQuestion(maineCoonQuestion.getId());
        aMaineCoonBlueOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aMaineCoonBlueOption.setDecision(blueMaineCoon.getId());
        aMaineCoonBlueOption.setText("Blue");
        aMaineCoonBlueOption = questionnaireOptionService.create(aMaineCoonBlueOption).getSuccess();
        QuestionnaireOptionResource aMaineCoonGreenOption = new QuestionnaireOptionResource();
        aMaineCoonGreenOption.setQuestion(maineCoonQuestion.getId());
        aMaineCoonGreenOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aMaineCoonGreenOption.setDecision(greenMaineCoon.getId());
        aMaineCoonGreenOption.setText("Green");
        aMaineCoonGreenOption = questionnaireOptionService.create(aMaineCoonGreenOption).getSuccess();
        QuestionnaireOptionResource aMaineCoonRedOption = new QuestionnaireOptionResource();
        aMaineCoonRedOption.setQuestion(maineCoonQuestion.getId());
        aMaineCoonRedOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aMaineCoonRedOption.setDecision(redMaineCoon.getId());
        aMaineCoonRedOption.setText("Red");
        aMaineCoonRedOption = questionnaireOptionService.create(aMaineCoonRedOption).getSuccess();

        QuestionnaireOptionResource aRagDollBlueOption = new QuestionnaireOptionResource();
        aRagDollBlueOption.setQuestion(ragdollQuestion.getId());
        aRagDollBlueOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aRagDollBlueOption.setDecision(blueRagDoll.getId());
        aRagDollBlueOption.setText("Blue");
        aRagDollBlueOption = questionnaireOptionService.create(aRagDollBlueOption).getSuccess();
        QuestionnaireOptionResource aRagDollGreenOption = new QuestionnaireOptionResource();
        aRagDollGreenOption.setQuestion(ragdollQuestion.getId());
        aRagDollGreenOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aRagDollGreenOption.setDecision(greenRagDoll.getId());
        aRagDollGreenOption.setText("Green");
        aRagDollGreenOption = questionnaireOptionService.create(aRagDollGreenOption).getSuccess();
        QuestionnaireOptionResource aRagDollRedOption = new QuestionnaireOptionResource();
        aRagDollRedOption.setQuestion(ragdollQuestion.getId());
        aRagDollRedOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aRagDollRedOption.setDecision(redRagDoll.getId());
        aRagDollRedOption.setText("Red");
        aRagDollRedOption = questionnaireOptionService.create(aRagDollRedOption).getSuccess();

        QuestionnaireOptionResource aLabBlueOption = new QuestionnaireOptionResource();
        aLabBlueOption.setQuestion(labradorQuestion.getId());
        aLabBlueOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aLabBlueOption.setDecision(blueLab.getId());
        aLabBlueOption.setText("Blue");
        aLabBlueOption = questionnaireOptionService.create(aLabBlueOption).getSuccess();
        QuestionnaireOptionResource aLabGreenOption = new QuestionnaireOptionResource();
        aLabGreenOption.setQuestion(labradorQuestion.getId());
        aLabGreenOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aLabGreenOption.setDecision(greenLab.getId());
        aLabGreenOption.setText("Green");
        aLabGreenOption = questionnaireOptionService.create(aLabGreenOption).getSuccess();
        QuestionnaireOptionResource aLabRedOption = new QuestionnaireOptionResource();
        aLabRedOption.setQuestion(labradorQuestion.getId());
        aLabRedOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aLabRedOption.setDecision(redLab.getId());
        aLabRedOption.setText("Red");
        aLabRedOption = questionnaireOptionService.create(aLabRedOption).getSuccess();

        QuestionnaireOptionResource aCockerBlueOption = new QuestionnaireOptionResource();
        aCockerBlueOption.setQuestion(cockerSpaniel.getId());
        aCockerBlueOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aCockerBlueOption.setDecision(blueCocker.getId());
        aCockerBlueOption.setText("Blue");
        aCockerBlueOption = questionnaireOptionService.create(aCockerBlueOption).getSuccess();
        QuestionnaireOptionResource aCockerGreenOption = new QuestionnaireOptionResource();
        aCockerGreenOption.setQuestion(cockerSpaniel.getId());
        aCockerGreenOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aCockerGreenOption.setDecision(greenCocker.getId());
        aCockerGreenOption.setText("Green");
        aCockerGreenOption = questionnaireOptionService.create(aCockerGreenOption).getSuccess();
        QuestionnaireOptionResource aCockerRedOption = new QuestionnaireOptionResource();
        aCockerRedOption.setQuestion(cockerSpaniel.getId());
        aCockerRedOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aCockerRedOption.setDecision(redCocker.getId());
        aCockerRedOption.setText("Red");
        aCockerRedOption = questionnaireOptionService.create(aCockerRedOption).getSuccess();

        QuestionnaireOptionResource aCanaryBlueOption = new QuestionnaireOptionResource();
        aCanaryBlueOption.setQuestion(canaryQuestion.getId());
        aCanaryBlueOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aCanaryBlueOption.setDecision(blueCanary.getId());
        aCanaryBlueOption.setText("Blue");
        aCanaryBlueOption = questionnaireOptionService.create(aCanaryBlueOption).getSuccess();
        QuestionnaireOptionResource aCanaryGreenOption = new QuestionnaireOptionResource();
        aCanaryGreenOption.setQuestion(canaryQuestion.getId());
        aCanaryGreenOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aCanaryGreenOption.setDecision(greenCanary.getId());
        aCanaryGreenOption.setText("Green");
        aCanaryGreenOption = questionnaireOptionService.create(aCanaryGreenOption).getSuccess();
        QuestionnaireOptionResource aCanaryRedOption = new QuestionnaireOptionResource();
        aCanaryRedOption.setQuestion(canaryQuestion.getId());
        aCanaryRedOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aCanaryRedOption.setDecision(redCanary.getId());
        aCanaryRedOption.setText("Red");
        aCanaryRedOption = questionnaireOptionService.create(aCanaryRedOption).getSuccess();

        QuestionnaireOptionResource aParrotBlueOption = new QuestionnaireOptionResource();
        aParrotBlueOption.setQuestion(parrotQuestion.getId());
        aParrotBlueOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aParrotBlueOption.setDecision(blueParrot.getId());
        aParrotBlueOption.setText("Blue");
        aParrotBlueOption = questionnaireOptionService.create(aParrotBlueOption).getSuccess();
        QuestionnaireOptionResource aParrotGreenOption = new QuestionnaireOptionResource();
        aParrotGreenOption.setQuestion(parrotQuestion.getId());
        aParrotGreenOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aParrotGreenOption.setDecision(greenParrot.getId());
        aParrotGreenOption.setText("Green");
        aParrotGreenOption = questionnaireOptionService.create(aParrotGreenOption).getSuccess();
        QuestionnaireOptionResource aParrotRedOption = new QuestionnaireOptionResource();
        aParrotRedOption.setQuestion(parrotQuestion.getId());
        aParrotRedOption.setDecisionType(DecisionType.TEXT_OUTCOME);
        aParrotRedOption.setDecision(redParrot.getId());
        aParrotRedOption.setText("Red");
        aParrotRedOption = questionnaireOptionService.create(aParrotRedOption).getSuccess();

        return questionnaireRepository.findById(questionnaire.getId()).get();
    }

    private Questionnaire questionnaire() {
        //TODO replace with service calls.
        Questionnaire questionnaire = new Questionnaire();

        QuestionnaireQuestion questionOne = new QuestionnaireQuestion();
        questionOne.setTitle("Question 1");
        questionOne.setQuestion("Is your business based in NI?");
        questionOne.setDepth(0);
        questionOne.setQuestionnaire(questionnaire);

        QuestionnaireOption questionOneYes = new QuestionnaireOption();
        questionOneYes.setText("Yes");
        questionOneYes.setQuestion(questionOne);

        QuestionnaireTextOutcome questionOneYesOutcome = new QuestionnaireTextOutcome();
        questionOneYesOutcome.setText("You are from NI, please apply with state aid");
        questionOneYesOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_TRUE);
        questionOneYes.setDecision(questionOneYesOutcome);

        QuestionnaireOption questionOneNo = new QuestionnaireOption();
        questionOneNo.setText("No");
        questionOneNo.setQuestion(questionOne);

        QuestionnaireQuestion questionTwo = new QuestionnaireQuestion();
        questionTwo.setTitle("Question 2");
        questionTwo.setQuestion("Do you trade in NI?");
        questionTwo.setDepth(1);
        questionTwo.setQuestionnaire(questionnaire);

        questionOneNo.setDecision(questionTwo);

        QuestionnaireOption questionTwoYes = new QuestionnaireOption();
        questionTwoYes.setText("Yes");
        questionTwoYes.setQuestion(questionTwo);

        QuestionnaireTextOutcome questionTwoYesOutcome = new QuestionnaireTextOutcome();
        questionTwoYesOutcome.setText("You trade with NI, please apply with state aid");
        questionTwoYesOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_TRUE);
        questionTwoYes.setDecision(questionTwoYesOutcome);

        QuestionnaireOption questionTwoNo = new QuestionnaireOption();
        questionTwoNo.setText("No");
        questionTwoNo.setQuestion(questionTwo);

        QuestionnaireTextOutcome questionTwoNoOutcome = new QuestionnaireTextOutcome();
        questionTwoNoOutcome.setText("You do not operate with NI, please apply with subsidy control");
        questionTwoNoOutcome.setImplementation(QuestionnaireDecisionImplementation.SET_NORTHERN_IRELAND_DECLARATION_FALSE);
        questionTwoNo.setDecision(questionTwoNoOutcome);

        return questionnaire;
    }
}
