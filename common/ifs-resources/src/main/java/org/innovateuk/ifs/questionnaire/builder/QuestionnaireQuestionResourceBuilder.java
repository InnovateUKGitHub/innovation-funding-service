package org.innovateuk.ifs.questionnaire.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class QuestionnaireQuestionResourceBuilder extends BaseBuilder<QuestionnaireQuestionResource, QuestionnaireQuestionResourceBuilder> {
    private QuestionnaireQuestionResourceBuilder(List<BiConsumer<Integer, QuestionnaireQuestionResource>> multiActions) {
        super(multiActions);
    }

    public static QuestionnaireQuestionResourceBuilder newQuestionnaireQuestionResource() {
        return new QuestionnaireQuestionResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected QuestionnaireQuestionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionnaireQuestionResource>> actions) {
        return new QuestionnaireQuestionResourceBuilder(actions);
    }

    @Override
    protected QuestionnaireQuestionResource createInitial() {
        return new QuestionnaireQuestionResource();
    }

    public QuestionnaireQuestionResourceBuilder withId(Long... ids) {
        return withArray((id, resource) -> resource.setId(id), ids);
    }

    public QuestionnaireQuestionResourceBuilder withDepth(Integer... values) {
        return withArray((value, resource) -> resource.setDepth(value), values);
    }

    public QuestionnaireQuestionResourceBuilder withTitle(String... values) {
        return withArray((value, resource) -> resource.setTitle(value), values);
    }

    public QuestionnaireQuestionResourceBuilder withQuestion(String... values) {
        return withArray((value, resource) -> resource.setQuestion(value), values);
    }

    public QuestionnaireQuestionResourceBuilder withGuidance(String... values) {
        return withArray((value, resource) -> resource.setGuidance(value), values);
    }

    public QuestionnaireQuestionResourceBuilder withQuestionnaire(Long... values) {
        return withArray((value, resource) -> resource.setQuestionnaire(value), values);
    }

    public QuestionnaireQuestionResourceBuilder withOptions(List<Long>... values) {
        return withArray((value, resource) -> resource.setOptions(value), values);
    }

    public QuestionnaireQuestionResourceBuilder withPreviousQuestions(List<Long>... values) {
        return withArray((value, resource) -> resource.setPreviousQuestions(value), values);
    }
}
