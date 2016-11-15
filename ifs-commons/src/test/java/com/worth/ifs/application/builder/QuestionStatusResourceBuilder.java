package com.worth.ifs.application.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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

    public QuestionStatusResourceBuilder withApplication(ApplicationResource application) {
        return with(questionStatus -> {
            questionStatus.setApplication(application.getId());
        });
    }

    @Override
    protected QuestionStatusResource createInitial() {
        return new QuestionStatusResource();
    }
}
