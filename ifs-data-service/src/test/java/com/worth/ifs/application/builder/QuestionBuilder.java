package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.form.domain.FormInput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Created by dwatson on 03/11/15.
 */
public class QuestionBuilder extends BaseBuilder<Question, QuestionBuilder> {

    private QuestionBuilder(List<BiConsumer<Integer, Question>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected QuestionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Question>> actions) {
        return new QuestionBuilder(actions);
    }

    public static QuestionBuilder newQuestion() {
        return new QuestionBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedNames("Section "))
                .withNeedingAssessorScore(true)
                .withPriority(0)
                .withQuestionNumber("1")
                .withGuidanceQuestion("Some Guidance Question Text")
                .withGuidanceAnswer("Some Guidance Answer Text");
    }

    public QuestionBuilder withNeedingAssessorScore(boolean needingAssessorScore) {
        return with(question -> setField("needingAssessorScore", needingAssessorScore, question));
    }

    public QuestionBuilder withQuestionNumber(String value) {
        return with(question -> setField("questionNumber", value, question));
    }

    public QuestionBuilder withGuidanceQuestion(String value) {
        return with(question -> setField("guidanceQuestion", value, question));
    }

    public QuestionBuilder withGuidanceAnswer(String value) {
        return with(question -> setField("guidanceAnswer", value, question));
    }

    public QuestionBuilder withPriority(int priority) {
        return with(question -> setField("priority", priority, question));
    }

    public QuestionBuilder withPriority(Function<Integer, Integer> prioritySetter) {
        return with((i, question) -> setField("priority", prioritySetter.apply(i), question));
    }

    public QuestionBuilder withFormInputs(List<FormInput> formInputs) {
        return with(question -> setField("formInputs", new ArrayList<>(formInputs), question));
    }

    @Override
    protected Question createInitial() {
        return new Question();
    }
}
