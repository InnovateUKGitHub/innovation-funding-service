package com.worth.ifs.application.domain;

import com.worth.ifs.BaseBuilder;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
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
                .with(uniqueIds());
    }

    @Override
    protected Question createInitial() {
        return new Question();
    }
}
