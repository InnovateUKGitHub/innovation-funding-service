package org.innovateuk.ifs.questionnaire.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireSecurityType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class QuestionnaireResourceBuilder extends BaseBuilder<QuestionnaireResource, QuestionnaireResourceBuilder> {
    private QuestionnaireResourceBuilder(List<BiConsumer<Integer, QuestionnaireResource>> multiActions) {
        super(multiActions);
    }

    public static QuestionnaireResourceBuilder newQuestionnaireResource() {
        return new QuestionnaireResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected QuestionnaireResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionnaireResource>> actions) {
        return new QuestionnaireResourceBuilder(actions);
    }

    @Override
    protected QuestionnaireResource createInitial() {
        return new QuestionnaireResource();
    }

    public QuestionnaireResourceBuilder withId(Long... ids) {
        return withArray((id, resource) -> resource.setId(id), ids);
    }

    public QuestionnaireResourceBuilder withDescription(String... values) {
        return withArray((value, resource) -> resource.setDescription(value), values);
    }

    public QuestionnaireResourceBuilder withTitle(String... values) {
        return withArray((value, resource) -> resource.setTitle(value), values);
    }

    public QuestionnaireResourceBuilder withSecurityType(QuestionnaireSecurityType... values) {
        return withArray((value, resource) -> resource.setSecurityType(value), values);
    }

    public QuestionnaireResourceBuilder withQuestions(List<Long>... values) {
        return withArray((value, resource) -> resource.setQuestions(value), values);
    }
}
