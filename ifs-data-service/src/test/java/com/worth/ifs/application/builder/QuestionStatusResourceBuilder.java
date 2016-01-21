package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.QuestionStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class QuestionStatusResourceBuilder extends BaseBuilder<QuestionStatusResource, QuestionStatusResourceBuilder> {

    private QuestionStatusResourceBuilder(List<BiConsumer<Integer, QuestionStatusResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected QuestionStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionStatusResource>> actions) {
        return new QuestionStatusResourceBuilder(actions);
    }

    public static QuestionStatusResourceBuilder newQuestionStatusResource() {
        return new QuestionStatusResourceBuilder(emptyList()).with(uniqueIds());
    }

    public QuestionStatusResourceBuilder withApplication(Application application) {
        return with(questionStatus -> {
            questionStatus.setApplication(application);
        });
    }

    @Override
    protected QuestionStatusResource createInitial() {
        return new QuestionStatusResource();
    }
}
