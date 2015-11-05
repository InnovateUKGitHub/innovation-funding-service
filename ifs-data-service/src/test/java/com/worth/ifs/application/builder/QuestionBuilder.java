package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Question;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BuilderAmendFunctions.setField;
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
                .with(uniqueIds())
                .with(idBasedNames("Section "))
                .withNeedingAssessorScore(true);
    }

    public QuestionBuilder withNeedingAssessorScore(boolean needingAssessorScore) {
        return with(question -> setField("needingAssessorScore", needingAssessorScore, question));
    }

    @Override
    protected Question createInitial() {
        return new Question();
    }
}
