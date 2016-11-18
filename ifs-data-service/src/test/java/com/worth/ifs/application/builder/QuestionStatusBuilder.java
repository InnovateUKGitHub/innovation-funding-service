package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.QuestionStatus;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class QuestionStatusBuilder extends BaseBuilder<QuestionStatus, QuestionStatusBuilder> {

    private QuestionStatusBuilder(List<BiConsumer<Integer, QuestionStatus>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected QuestionStatusBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionStatus>> actions) {
        return new QuestionStatusBuilder(actions);
    }

    public static QuestionStatusBuilder newQuestionStatus() {
        return new QuestionStatusBuilder(emptyList()).with(uniqueIds());
    }

    public QuestionStatusBuilder withApplication(Application application) {
        return with(questionStatus -> {
            questionStatus.setApplication(application);
        });
    }

    @Override
    protected QuestionStatus createInitial() {
        return new QuestionStatus();
    }
}
