package org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder;

import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class QuestionBuilder {
    private String questionNumber;
    private String name;
    private String shortName;
    private String description;
    private Boolean markAsCompletedEnabled = false;
    private Boolean assignEnabled = true;
    private Boolean multipleStatuses = false;
    private List<FormInputBuilder> formInputs = new ArrayList<>();
    private QuestionType type = QuestionType.GENERAL;
    private QuestionSetupType questionSetupType;
    private Integer assessorMaximumScore;
    private Questionnaire questionnaire;

    private QuestionBuilder() {
    }

    public static QuestionBuilder aQuestion() {
        return new QuestionBuilder();
    }

    public static QuestionBuilder aDefaultAssessedQuestion() {
        return aQuestion()
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(10);
    }

    public static QuestionBuilder aQuestionWithMultipleStatuses() {
        return new QuestionBuilder()
                .withMultipleStatuses(true)
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(true);
    }

    public QuestionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public QuestionBuilder withShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public QuestionBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public QuestionBuilder withMarkAsCompletedEnabled(Boolean markAsCompletedEnabled) {
        this.markAsCompletedEnabled = markAsCompletedEnabled;
        return this;
    }

    public QuestionBuilder withAssignEnabled(Boolean assignEnabled) {
        this.assignEnabled = assignEnabled;
        return this;
    }

    public QuestionBuilder withMultipleStatuses(Boolean multipleStatuses) {
        this.multipleStatuses = multipleStatuses;
        return this;
    }

    public QuestionBuilder withFormInputs(List<FormInputBuilder> formInputs) {
        this.formInputs = formInputs;
        return this;
    }

    public List<FormInputBuilder> getFormInputs() {
        return formInputs;
    }

    public QuestionBuilder withQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
        return this;
    }

    public QuestionBuilder withType(QuestionType type) {
        this.type = type;
        return this;
    }

    public QuestionBuilder withQuestionSetupType(QuestionSetupType questionSetupType) {
        this.questionSetupType = questionSetupType;
        return this;
    }

    public QuestionBuilder withQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
        return this;
    }


    public QuestionSetupType getQuestionSetupType() {
        return questionSetupType;
    }

    public QuestionBuilder withAssessorMaximumScore(Integer assessorMaximumScore) {
        this.assessorMaximumScore = assessorMaximumScore;
        return this;
    }

    public Question build() {
        Question question = new Question();
        question.setName(name);
        question.setShortName(shortName);
        question.setDescription(description);
        question.setMarkAsCompletedEnabled(markAsCompletedEnabled);
        question.setAssignEnabled(assignEnabled);
        question.setMultipleStatuses(multipleStatuses);
        question.setFormInputs(formInputs.stream().map(FormInputBuilder::build).collect(Collectors.toList()));
        question.setQuestionNumber(questionNumber);
        question.setType(type);
        question.setQuestionSetupType(questionSetupType);
        question.setAssessorMaximumScore(assessorMaximumScore);
        question.setQuestionnaire(questionnaire);
        return question;
    }
}
