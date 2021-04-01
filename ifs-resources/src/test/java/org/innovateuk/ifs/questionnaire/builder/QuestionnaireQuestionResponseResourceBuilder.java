package org.innovateuk.ifs.questionnaire.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class QuestionnaireQuestionResponseResourceBuilder extends BaseBuilder<QuestionnaireQuestionResponseResource, QuestionnaireQuestionResponseResourceBuilder> {
    private QuestionnaireQuestionResponseResourceBuilder(List<BiConsumer<Integer, QuestionnaireQuestionResponseResource>> multiActions) {
        super(multiActions);
    }

    public static QuestionnaireQuestionResponseResourceBuilder newQuestionnaireQuestionResponseResource() {
        return new QuestionnaireQuestionResponseResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected QuestionnaireQuestionResponseResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionnaireQuestionResponseResource>> actions) {
        return new QuestionnaireQuestionResponseResourceBuilder(actions);
    }

    @Override
    protected QuestionnaireQuestionResponseResource createInitial() {
        return new QuestionnaireQuestionResponseResource();
    }

    public QuestionnaireQuestionResponseResourceBuilder withId(Long... ids) {
        return withArray((id, resource) -> resource.setId(id), ids);
    }

    public QuestionnaireQuestionResponseResourceBuilder withQuestionnaireResponse(String... values) {
        return withArray((value, resource) -> resource.setQuestionnaireResponse(value), values);
    }

    public QuestionnaireQuestionResponseResourceBuilder withOption(Long... values) {
        return withArray((value, resource) -> resource.setOption(value), values);
    }

    public QuestionnaireQuestionResponseResourceBuilder withQuestion(Long... values) {
        return withArray((value, resource) -> resource.setQuestion(value), values);
    }
}