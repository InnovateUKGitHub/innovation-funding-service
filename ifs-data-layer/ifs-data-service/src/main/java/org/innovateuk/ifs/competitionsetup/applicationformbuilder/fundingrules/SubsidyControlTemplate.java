package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.config.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;

@Component
public class SubsidyControlTemplate implements FundingRulesTemplate {

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
                        .withQuestionnaire(questionnaire()))));
        return competitionTypeSections;
    }

    private Questionnaire questionnaire() {
        Questionnaire questionnaire = new Questionnaire();

        QuestionnaireQuestion questionOne = new QuestionnaireQuestion();
        questionOne.setTitle("Question 1");
        questionOne.setQuestion("Is your business based in NI?");
        questionOne.setPriority(0);
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
        questionTwo.setPriority(1);
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
