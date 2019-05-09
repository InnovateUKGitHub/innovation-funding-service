package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public QuestionStatusResourceBuilder withMarkedAsComplete(Boolean markedAsComplete) {
        return with(questionStatus -> {
            questionStatus.setMarkedAsComplete(markedAsComplete);
        });
    }

    public QuestionStatusResourceBuilder withQuestion(Long question) {
        return with(questionStatus -> {
            questionStatus.setQuestion(question);
        });
    }

    public QuestionStatusResourceBuilder withMarkedAsCompleteOn(ZonedDateTime... dates) {
        return withArray((date, questionStatusResource) -> questionStatusResource.setMarkedAsCompleteOn(date), dates);
    }

    public QuestionStatusResourceBuilder withMarkedAsCompleteByUserId(Long... userIds) {
        return withArray((userId, questionStatusResource) -> questionStatusResource.setMarkedAsCompleteByUserId(userId), userIds);
    }

    public QuestionStatusResourceBuilder withMarkedAsCompleteByUserName(String... names) {
        return withArray((name, questionStatusResource) -> questionStatusResource.setMarkedAsCompleteByUserName(name), names);
    }

    @Override
    protected QuestionStatusResource createInitial() {
        return new QuestionStatusResource();
    }
}
