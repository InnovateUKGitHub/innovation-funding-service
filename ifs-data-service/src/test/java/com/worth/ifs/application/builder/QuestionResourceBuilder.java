package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.form.domain.FormInput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

public class QuestionResourceBuilder extends BaseBuilder<QuestionResource, QuestionResourceBuilder> {

    private QuestionResourceBuilder(List<BiConsumer<Integer, QuestionResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected QuestionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionResource>> actions) {
        return new QuestionResourceBuilder(actions);
    }

    public static QuestionResourceBuilder newQuestionResource() {
        return new QuestionResourceBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedNames("Section "))
                .withNeedingAssessorScore(true)
                .withPriority(0)
                .withQuestionNumber("1")
                .withGuidanceQuestion("Some Guidance Question Text")
                .withGuidanceAnswer("Some Guidance Answer Text");
    }

    public QuestionResourceBuilder withNeedingAssessorScore(boolean needingAssessorScore) {
        return with(question -> setField("needingAssessorScore", needingAssessorScore, question));
    }

    public QuestionResourceBuilder withQuestionNumber(String value) {
        return with(question -> setField("questionNumber", value, question));
    }

    public QuestionResourceBuilder withGuidanceQuestion(String value) {
        return with(question -> setField("guidanceQuestion", value, question));
    }

    public QuestionResourceBuilder withGuidanceAnswer(String value) {
        return with(question -> setField("guidanceAnswer", value, question));
    }

    public QuestionResourceBuilder withPriority(int priority) {
        return with(question -> setField("priority", priority, question));
    }

    public QuestionResourceBuilder withPriority(Function<Integer, Integer> prioritySetter) {
        return with((i, question) -> setField("priority", prioritySetter.apply(i), question));
    }

    public QuestionResourceBuilder withFormInputs(List<FormInput> formInputs) {
        return with(question -> setField("formInputs", new ArrayList<>(formInputs), question));
    }

    public QuestionResourceBuilder withCompetitionAndSectionAndPriority(Competition competition, Section section, Integer priority) {
        return with(question -> {
            question.setCompetition(competition);
            question.setSection(section);
            setField("priority", priority, question);
        });
    }

    @Override
    protected QuestionResource createInitial() {
        return new QuestionResource();
    }
}
