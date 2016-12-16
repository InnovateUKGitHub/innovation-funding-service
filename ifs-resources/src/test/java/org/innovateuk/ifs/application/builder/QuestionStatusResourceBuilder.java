package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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
