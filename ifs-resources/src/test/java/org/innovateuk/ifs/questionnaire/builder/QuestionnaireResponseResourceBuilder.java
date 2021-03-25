package org.innovateuk.ifs.questionnaire.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class QuestionnaireResponseResourceBuilder extends BaseBuilder<QuestionnaireResponseResource, QuestionnaireResponseResourceBuilder> {
    private QuestionnaireResponseResourceBuilder(List<BiConsumer<Integer, QuestionnaireResponseResource>> multiActions) {
        super(multiActions);
    }

    public static QuestionnaireResponseResourceBuilder newQuestionnaireResponseResource() {
        return new QuestionnaireResponseResourceBuilder(emptyList());
    }

    @Override
    protected QuestionnaireResponseResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionnaireResponseResource>> actions) {
        return new QuestionnaireResponseResourceBuilder(actions);
    }

    @Override
    protected QuestionnaireResponseResource createInitial() {
        return new QuestionnaireResponseResource();
    }

    public QuestionnaireResponseResourceBuilder withId(String... ids) {
        return withArray((id, resource) -> resource.setId(id), ids);
    }

    public QuestionnaireResponseResourceBuilder withQuestionnaire(Long... values) {
        return withArray((value, resource) -> resource.setQuestionnaire(value), values);
    }

    public QuestionnaireResponseResourceBuilder withQuestion(List<Long>... values) {
        return withArray((value, resource) -> resource.setQuestionnaireQuestionResponse(value), values);
    }
}